import java.io.Closeable
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object revcomp {

    @Throws(Exception::class)
    fun main(args: Array<String>) {
        Strand().use({ strand ->
            FileInputStream(FileDescriptor.`in`).use({ standIn ->
                FileOutputStream(FileDescriptor.out).use({ standOut ->

                    while (strand.readOneStrand(standIn) >= 0) {
                        strand.reverse()
                        strand.write(standOut)
                        strand.reset()
                    }

                })
            })
        })
    }
}

internal class Chunk {

    var capacity = 0
    var length = 0

    val bytes = ByteArray(CHUNK_SIZE)

    fun clear() {
        capacity = 0
        length = 0
    }

    companion object {
        val CHUNK_SIZE = 64 * 1024
    }
}

internal class Strand : Closeable {
    companion object {

        private val NEW_LINE: Byte = '\n'.toByte()
        private val ANGLE: Byte = '>'.toByte()
        private val LINE_LENGTH = 61

        private val map = ByteArray(128)

        init {
            for (i in map.indices) {
                map[i] = i.toByte()
            }

            map['T'] = 'A'.toByte()
            map['t'] = map['T']
            map['A'] = 'T'.toByte()
            map['a'] = map['A']
            map['G'] = 'C'.toByte()
            map['g'] = map['G']
            map['C'] = 'G'.toByte()
            map['c'] = map['C']
            map['V'] = 'B'.toByte()
            map['v'] = map['V']
            map['H'] = 'D'.toByte()
            map['h'] = map['H']
            map['R'] = 'Y'.toByte()
            map['r'] = map['R']
            map['M'] = 'K'.toByte()
            map['m'] = map['M']
            map['Y'] = 'R'.toByte()
            map['y'] = map['Y']
            map['K'] = 'M'.toByte()
            map['k'] = map['K']
            map['B'] = 'V'.toByte()
            map['b'] = map['B']
            map['D'] = 'H'.toByte()
            map['d'] = map['D']
            map['U'] = 'A'.toByte()
            map['u'] = map['U']
        }
    }

    private val NCPU = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(NCPU)

    private var chunkCount = 0

    private val chunks = ArrayList<Chunk>()

    private fun ensureSize() {
        if (chunkCount == chunks.size()) {
            chunks.add(Chunk())
        }
    }

    private fun isLastChunk(chunk: Chunk): Boolean {
        return chunk.length != chunk.capacity
    }

    private fun correctLentgh(chunk: Chunk, skipFirst: Boolean) {
        val bytes = chunk.bytes

        val start = if (skipFirst) 1 else 0
        val end = chunk.capacity

        for (i in start until end) {
            if (ANGLE == bytes[i]) {
                chunk.length = i
                return
            }
        }

        chunk.length = chunk.capacity
    }

    private fun prepareNextStrand() {
        if (chunkCount == 0) {
            return
        }

        val first = chunks.get(0)
        val last = chunks.get(chunkCount - 1)

        if (last.capacity == last.length) {
            for (i in 0 until chunkCount) {
                chunks.get(i).clear()
            }

            return
        }

        System.arraycopy(last.bytes, last.length, first.bytes, 0, last.capacity - last.length)

        first.capacity = last.capacity - last.length
        correctLentgh(first, true)

        for (i in 1 until chunkCount) {
            chunks.get(i).clear()
        }
    }

    @Throws(IOException::class)
    fun readOneStrand(`is`: InputStream): Int {
        while (true) {
            ensureSize()

            val chunk = chunks.get(chunkCount)
            chunkCount++

            if (isLastChunk(chunk)) {
                return chunkCount
            }

            val bytes = chunk.bytes

            val readLength = `is`.read(bytes, chunk.length, Chunk.CHUNK_SIZE - chunk.length)

            if (chunkCount == 1 && readLength < 0 && chunk.length == 0) {
                return -1
            }

            if (readLength > 0) {
                chunk.capacity += readLength
                correctLentgh(chunk, chunkCount == 1)
            }

            if (readLength < 0 || isLastChunk(chunk)) {
                return chunkCount
            }
        }
    }

    fun reset() {
        prepareNextStrand()
        chunkCount = 0
    }

    @Throws(IOException::class)
    fun write(out: OutputStream) {
        for (i in 0 until chunkCount) {
            val chunk = chunks.get(i)
            out.write(chunk.bytes, 0, chunk.length)
        }
    }

    @Throws(InterruptedException::class)
    fun reverse() {
        val sumLength = getSumLength()
        val titleLength = getTitleLength()
        val dataLength = sumLength - titleLength
        val realDataLength = dataLength - ceilDiv(dataLength, LINE_LENGTH)

        val leftEndIndex = realDataLength / 2
        val rawLeftEndIndex = leftEndIndex + leftEndIndex / (LINE_LENGTH - 1)
        val leftEndChunkIndex = ceilDiv(rawLeftEndIndex + titleLength, Chunk.CHUNK_SIZE) - 1
        val realLeftEndIndex = (rawLeftEndIndex + titleLength) % Chunk.CHUNK_SIZE - 1

        val tasks = ArrayList<Callable<Void>>(NCPU)

        val itemCount = ceilDiv(leftEndChunkIndex + 1, NCPU)

        for (t in 0 until NCPU) {
            val start = itemCount * t
            val end = Math.min(start + itemCount, leftEndChunkIndex + 1)

            val task = {
                for (i in start until end) {
                    val rawLeftIndex = if (i == 0) 0 else i * Chunk.CHUNK_SIZE - titleLength

                    val leftIndex = rawLeftIndex - rawLeftIndex / LINE_LENGTH
                    val rightIndex = realDataLength - leftIndex - 1

                    val rawRightIndex = rightIndex + rightIndex / (LINE_LENGTH - 1)

                    val rightChunkIndex = ceilDiv(rawRightIndex + titleLength, Chunk.CHUNK_SIZE) - 1

                    val realLeftIndex = (rawLeftIndex + titleLength) % Chunk.CHUNK_SIZE
                    val realRightIndex = (rawRightIndex + titleLength) % Chunk.CHUNK_SIZE

                    val endIndex = if (leftEndChunkIndex == i)
                        realLeftEndIndex
                    else
                        chunks.get(i).length - 1

                    reverse(i, rightChunkIndex, realLeftIndex, realRightIndex, endIndex)
                }

                null
            }

            tasks.add(task)
        }

        executor.invokeAll(tasks)
    }

    private fun reverse(leftChunkIndex: Int, rightChunkIndex: Int, leftIndex: Int, rightIndex: Int, leftEndIndex: Int) {
        var rightChunkIndex = rightChunkIndex
        var leftIndex = leftIndex
        var rightIndex = rightIndex

        val map = Strand.map

        val leftChunk = chunks.get(leftChunkIndex)
        var rightChunk = chunks.get(rightChunkIndex)

        val leftBytes = leftChunk.bytes
        var rightBytes = rightChunk.bytes

        while (leftIndex <= leftEndIndex) {
            if (rightIndex < 0) {
                rightChunk = chunks.get(--rightChunkIndex)
                rightBytes = rightChunk.bytes
                rightIndex = rightChunk.length - 1
            }

            if (leftBytes[leftIndex] == NEW_LINE) {
                leftIndex++
            }

            if (rightBytes[rightIndex] == NEW_LINE) {
                rightIndex--

                if (rightIndex < 0) {
                    rightChunk = chunks.get(--rightChunkIndex)
                    rightBytes = rightChunk.bytes
                    rightIndex = rightChunk.length - 1
                }
            }

            if (leftIndex <= leftEndIndex) {
                val lByte = leftBytes[leftIndex]
                val rByte = rightBytes[rightIndex]

                leftBytes[leftIndex++] = map[rByte]
                rightBytes[rightIndex--] = map[lByte]
            }
        }

    }
    private fun ceilDiv(a: Int, b: Int): Int {
        return (a + b - 1) / b
    }

    private fun getSumLength(): Int {
        var sumLength = 0

        for (i in 0 until chunkCount) {
            sumLength += chunks.get(i).length
        }

        return sumLength
    }

    private fun getTitleLength(): Int {
        val first = chunks.get(0)
        val bytes = first.bytes

        for (i in 0 until first.length) {
            if (bytes[i] == NEW_LINE) {
                return i + 1
            }
        }

        return -1
    }

    @Override
    @Throws(IOException::class)
    fun close() {
        executor.shutdown()
    }

}