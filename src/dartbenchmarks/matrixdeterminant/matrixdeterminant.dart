import 'dart:math';

void main(args) {
  var size = int.parse(args[0]);
  Matrix matrix1 = new Matrix(size,size);
  Matrix matrix2 = new Matrix(size,size);
  matrix1.fillRandom();
  matrix2.fillMatrix();
	print(Matrix.determinant(matrix1));
  print(Matrix.determinant(matrix2));

}

class Matrix{
  var nrows;
  var ncols;
  var data;
/*
  Matrix(List dat){
    data = new List();
    nrows = dat.length;
    ncols = dat.length;
  }
*/

  Matrix(int row, int col){
    nrows = row;
    ncols = col;
    data = new List(row);
    for(var i = 0; i < nrows; i++){
      data[i] = new List(col);
    }
  }

  int get getNrows{
    return nrows;
  }

  void set setNrows(int row){
    nrows = row;
  }

  int get getNcols{
    return ncols;
  }

  void set setNcols(int col){
    ncols = col;
  }
  
  List getValues(){
    return data;
  }

  void set setValues(List dat){
    data = dat;
  }

  void setValueAt(int row, int col, double value){
    data[row][col] = value;
  }

  double getValueAt(int row, int col){
    return data[row][col];
  }

  bool isSquare(){
    return nrows == ncols;
  }

  int size(){
    return isSquare() ? nrows : -1;
  }

  Matrix multiplyByConstant(double constant){
    Matrix mat = new Matrix(nrows, ncols);
    for(var i = 0; i < nrows; i++){
      for(var j = 0; j < ncols; j++){
        mat.setValueAt(i, j, data[i][j] * constant);
      }
    }
  }

  Matrix insertColumnWithValue1(){
    Matrix X_ = new Matrix(this.nrows, this.ncols +1);
    for(var i=0; i<X_.nrows; i++){
      for(var j=0; j<X_.ncols; j++){
        if(j==0){
          X_.setValueAt(i, j, 1.0);
        }
        else X_.setValueAt(i, j, this.getValueAt(i, j-1));
      }
    }
    return X_;
  }

  void fillRandom(){
    var rnd = new Random();
    for(var i = 0; i < nrows; i++){
      for(var j =0; j < ncols;j++){
        data[i][j] = ((rnd.nextDouble() * ((10000 - 0) + 1)) + 0);
      }
    }
  }

  void fillMatrix(){
    var cont = 1.0;
    for(var i = 0; i < nrows; i++){
      for(var j = 0;j < ncols; j++){
        data[i][j] = cont;
        cont++;
      }
    }
  }

  bool issSquare(Matrix matrix){
    if(matrix.nrows == matrix.ncols){
      return true;
    }else return false;
  }

  static Matrix createSubMatrix(Matrix matrix, int excluding_row, int excluding_col){
    Matrix mat = new Matrix(matrix.getNrows-1, matrix.getNcols-1);
    var r = -1;
    for(var i = 0; i < matrix.getNrows; i++){
      if(i==excluding_row)
        continue;
      r++;
      var c = -1;
      for(var j = 0; j < matrix.getNcols; j++){
        if(j==excluding_col)
          continue;
        mat.setValueAt(r, ++c, matrix.getValueAt(i,j));
      }
    }
    return mat;
  }

  static int changeSign(int i){
    if(i%2 == 0){
      return 1;
    }else return -1;
  }

  static double determinant(Matrix matrix){
    if(!matrix.isSquare())
      throw new Exception("Matrix need to be square.");
    if(matrix.size() == 1){
      return matrix.getValueAt(0,0);
    }
    if(matrix.size() == 2){
      return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) - (matrix.getValueAt(0,1) * matrix.getValueAt(1, 0));
    }
    var sum = 0.0;
    for(var i = 0; i < matrix.getNcols; i++){
      sum += changeSign(i) * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i));
    }
    return sum;
  }

}