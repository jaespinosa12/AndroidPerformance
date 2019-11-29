/**
* Based on the "Matrix Operations in Java" code by Ata Amini
* https://www.codeproject.com/Articles/405128/Matrix-Operations-in-Java
*/

fun main(args: Array<String>){
    matrixdeterminant.execute(args)
}

object matrixdeterminant { 
    @Throws(Exception::class)
    @JvmStatic 
    fun execute(args: Array<String>) {
        val size = Integer.parseInt(args[0])
        val matrix1 = Matrix(size, size)
        val matrix2 = Matrix(size, size)
        matrix1.fillRandom()
        matrix2.fillMatrix()
        println(Matrix.determinant(matrix1))
        println(Matrix.determinant(matrix2))
    }
}
internal class Matrix {
    var nrows:Int = 0
    var ncols:Int = 0
    var values:Array<DoubleArray>
    val isSquare:Boolean
    get() {
      return nrows == ncols
    }
    constructor(dat:Array<DoubleArray>) {
      this.values = dat
      this.nrows = dat.size
      this.ncols = dat[0].size
    }
    constructor(nrow:Int, ncol:Int) {
      this.nrows = nrow
      this.ncols = ncol
      values = Array<DoubleArray>(nrow, {DoubleArray(ncol)})
    }
    fun setValueAt(row:Int, col:Int, value:Double) {
      values[row][col] = value
    }
    fun getValueAt(row:Int, col:Int):Double {
      return values[row][col]
    }
    fun size():Int {
      if (isSquare)
      return nrows
      return -1
    }
    fun multiplyByConstant(constant:Double):Matrix {
      val mat = Matrix(nrows, ncols)
      for (i in 0 until nrows)
      {
        for (j in 0 until ncols)
        {
          mat.setValueAt(i, j, values[i][j] * constant)
        }
      }
      return mat
    }
    fun insertColumnWithValue1():Matrix {
      val X_ = Matrix(this.nrows, this.ncols + 1)
      for (i in 0 until X_.nrows)
      {
        for (j in 0 until X_.ncols)
        {
          if (j == 0)
          X_.setValueAt(i, j, 1.0)
          else
          X_.setValueAt(i, j, this.getValueAt(i, j - 1))
        }
      }
      return X_
    }
    fun fillRandom() {
      for (i in 0 until nrows)
      {
        for (j in 0 until ncols)
        {
          values[i][j] = (Math.random() * ((10000 - 0) + 1)) + 0
        }
      }
    }
    fun fillMatrix() {
      var cont = 1
      for (i in 0 until nrows)
      {
        for (j in 0 until ncols)
        {
          values[i][j] = cont.toDouble()
          cont++
        }
      }
    }
    fun isSquare(matrix:Matrix):Boolean {
      if (matrix.ncols == matrix.nrows)
      {
        return true
      }
      else
      return false
    }
    companion object {
      fun createSubMatrix(matrix:Matrix, excluding_row:Int, excluding_col:Int):Matrix {
        val mat = Matrix(matrix.nrows - 1, matrix.ncols - 1)
        var r = -1
        for (i in 0 until matrix.nrows)
        {
          if (i == excluding_row)
          continue
          r++
          var c = -1
          for (j in 0 until matrix.ncols)
          {
            if (j == excluding_col)
            continue
            mat.setValueAt(r, ++c, matrix.getValueAt(i, j))
          }
        }
        return mat
      }
      fun changeSign(i:Int):Int {
        if (i % 2 == 0)
        {
          return 1
        }
        else
        return -1
      }
      @Throws(Exception::class)
      fun determinant(matrix:Matrix):Double {
        if (!matrix.isSquare)
        throw Exception("matrix need to be square.")
        if (matrix.size() == 1)
        {
          return matrix.getValueAt(0, 0)
        }
        if (matrix.size() == 2)
        {
          return ((matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - (matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0)))
        }
        var sum = 0.0
        for (i in 0 until matrix.ncols)
        {
          sum += changeSign(i).toDouble() * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i))
        }
        return sum
      }
    }
}