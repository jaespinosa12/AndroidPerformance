/**
 * Based on the "Matrix Operations in Java" code by Ata Amini
 * https://www.codeproject.com/Articles/405128/Matrix-Operations-in-Java
 */
private class Matrix {

    var nrows: Int = 0
    var ncols: Int = 0
    var values: Array<DoubleArray>? = null

    val isSquare: Boolean
        get() = nrows == ncols

    constructor(dat: Array<DoubleArray>) {
        this.values = dat
        this.nrows = dat.size
        this.ncols = dat[0].size
    }

    constructor(nrow: Int, ncol: Int) {
        this.nrows = nrow
        this.ncols = ncol
        values = Array(nrow) { DoubleArray(ncol) }
    }

    fun setValueAt(row: Int, col: Int, value: Double) {
        values!![row][col] = value
    }

    fun getValueAt(row: Int, col: Int): Double {
        return values!![row][col]
    }

    fun size(): Int {
        return if (isSquare) nrows else -1
    }

    fun multiplyByConstant(constant: Double): Matrix {
        val mat = Matrix(nrows, ncols)
        for (i in 0 until nrows) {
            for (j in 0 until ncols) {
                mat.setValueAt(i, j, values!![i][j] * constant)
            }
        }
        return mat
    }

    fun insertColumnWithValue1(): Matrix {
        val X_ = Matrix(this.nrows, this.ncols + 1)
        for (i in 0 until X_.nrows) {
            for (j in 0 until X_.ncols) {
                if (j == 0)
                    X_.setValueAt(i, j, 1.0)
                else
                    X_.setValueAt(i, j, this.getValueAt(i, j - 1))

            }
        }
        return X_
    }

    fun fillMatrix() {
        for (i in 0 until nrows) {
            for (j in 0 until ncols) {
                values!![i][j] = Math.random() * (10000 - 0 + 1) + 0
            }
        }
    }

    fun isSquare(matrix: Matrix): Boolean {
        return if (matrix.ncols == matrix.nrows) {
            true
        } else
            false
    }

    companion object {

        fun createSubMatrix(matrix: Matrix, excluding_row: Int, excluding_col: Int): Matrix {
            val mat = Matrix(matrix.nrows - 1, matrix.ncols - 1)
            var r = -1
            for (i in 0 until matrix.nrows) {
                if (i == excluding_row)
                    continue
                r++
                var c = -1
                for (j in 0 until matrix.ncols) {
                    if (j == excluding_col)
                        continue
                    mat.setValueAt(r, ++c, matrix.getValueAt(i, j))
                }
            }
            return mat
        }

        fun changeSign(i: Int): Int {
            return if (i % 2 == 0) {
                1
            } else
                -1
        }

        @Throws(Exception::class)
        fun determinant(matrix: Matrix): Double {
            if (!matrix.isSquare)
                throw Exception("matrix need to be square.")
            if (matrix.size() == 1) {
                return matrix.getValueAt(0, 0)
            }
            if (matrix.size() == 2) {
                return matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1) - matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0)
            }
            var sum = 0.0
            for (i in 0 until matrix.ncols) {
                sum += changeSign(i).toDouble() * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i))
            }
            return sum
        }
    }
}

@Throws(Exception::class)
fun main(args: Array<String>) {
    System.out.println("Creating new Matrix")
    val matrix = Matrix(10, 10)
    System.out.println("Matrix created")
    matrix.fillMatrix()
    System.out.println("Matrix filled")
    System.out.println("Matrix determinant is:" + Matrix.determinant(matrix))
}