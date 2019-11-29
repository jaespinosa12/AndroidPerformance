/**
 * Based on the "Matrix Operations in Java" code by Ata Amini
 * https://www.codeproject.com/Articles/405128/Matrix-Operations-in-Java
 */
public class matrixdeterminant {

    private static class Matrix{

        private int nrows;
        private int ncols;
        private double[][] data;

        public Matrix(double[][] dat) {
            this.data = dat;
            this.nrows = dat.length;
            this.ncols = dat[0].length;
        }

        public Matrix(int nrow, int ncol) {
            this.nrows = nrow;
            this.ncols = ncol;
            data = new double[nrow][ncol];
        }

        public int getNrows() {
            return nrows;
        }

        public void setNrows(int nrows) {
            this.nrows = nrows;
        }

        public int getNcols() {
            return ncols;
        }

        public void setNcols(int ncols) {
            this.ncols = ncols;
        }

        public double[][] getValues() {
            return data;
        }

        public void setValues(double[][] values) {
            this.data = values;
        }

        public void setValueAt(int row, int col, double value) {
            data[row][col] = value;
        }

        public double getValueAt(int row, int col) {
            return data[row][col];
        }

        public boolean isSquare() {
            return nrows == ncols;
        }

        public int size() {
            if (isSquare())
                return nrows;
            return -1;
        }

        public Matrix multiplyByConstant(double constant) {
            Matrix mat = new Matrix(nrows, ncols);
            for (int i = 0; i < nrows; i++) {
                for (int j = 0; j < ncols; j++) {
                    mat.setValueAt(i, j, data[i][j] * constant);
                }
            }
            return mat;
        }
        public Matrix insertColumnWithValue1() {
            Matrix X_ = new Matrix(this.getNrows(), this.getNcols()+1);
            for (int i=0;i<X_.getNrows();i++) {
                for (int j=0;j<X_.getNcols();j++) {
                    if (j==0)
                        X_.setValueAt(i, j, 1.0);
                    else
                        X_.setValueAt(i, j, this.getValueAt(i, j-1));

                }
            }
            return X_;
        }

        public void fillRandom(){
            for (int i = 0; i < nrows; i++){
                for (int j = 0; j < ncols; j++){
                    data[i][j] = (Math.random() * ((10000 - 0) + 1)) + 0;
                }
            }
        }
        
        public void fillMatrix(){
            int cont = 1;
            for(int i =0; i < nrows ;i++){
                for(int j = 0; j < ncols ; j++){
                    data[i][j] = cont;
                    cont++;
                }
            }
        }

        public boolean isSquare(Matrix matrix){
            if(matrix.ncols == matrix.nrows){
                return true;
            }else return false;
        }

        public static Matrix createSubMatrix(Matrix matrix, int excluding_row, int excluding_col) {
            Matrix mat = new Matrix(matrix.getNrows()-1, matrix.getNcols()-1);
            int r = -1;
            for (int i=0;i<matrix.getNrows();i++) {
                if (i==excluding_row)
                    continue;
                r++;
                int c = -1;
                for (int j=0;j<matrix.getNcols();j++) {
                    if (j==excluding_col)
                        continue;
                    mat.setValueAt(r, ++c, matrix.getValueAt(i, j));
                }
            }
            return mat;
        }

        public static int changeSign( int i ){
            if(i%2 == 0){
                return 1;
            }else return -1;
        }

        public static double determinant(Matrix matrix) throws Exception {
            if (!matrix.isSquare())
                throw new Exception("matrix need to be square.");
            if (matrix.size() == 1) {
                return matrix.getValueAt(0, 0);
            }
            if (matrix.size()==2) {
                return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1)) -
                        ( matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
            }
            double sum = 0.0;
            for (int i=0; i<matrix.getNcols(); i++) {
                sum += changeSign(i) * matrix.getValueAt(0, i) * determinant(createSubMatrix(matrix, 0, i));
            }
            return sum;
        }
    }
    public static void main(final String[] args) throws Exception {
        int size = Integer.parseInt(args[0]);
        Matrix matrix1 = new Matrix(size, size);
        Matrix matrix2 = new Matrix(size,size);
        matrix1.fillRandom();
        matrix2.fillMatrix();
        System.out.println(Matrix.determinant(matrix1));
        System.out.println(Matrix.determinant(matrix2));
    }
}