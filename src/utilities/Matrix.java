package utilities;

import java.util.List;
import java.util.Random;

/**
 * Matrix class. The original class is borrowed from 'https://introcs.cs.princeton.edu/java/95linear/Matrix.java.html'.
 *
 * Implemented interface for matrix operations.
 */
final public class Matrix {

    /** Number of rows. */
    private final int M;

    /** Number of columns. */
    private final int N;

    /** 2D-array of values in the matrix. */
    private final double[][] data;

    /**
     * Matrix constructor with specified dimensions [M x N].
     *
     * @param M Number of rows.
     * @param N Number of columns.
     */
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    /**
     * Matrix constructor with specified data array. Constructs a 2D-matrix.
     *
     * @param data 2D-array of values.
     */
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < M; i++) {
                this.set(i, j, data[i][j]);
            }
        }
    }

    /**
     * Matrix constructor with specified data vector. Constructs a 1D-matrix.
     *
     * @param data 1D-array of values.
     */
    public Matrix(double[] data) {
        M = data.length;
        N = 1;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            this.set(i, 0, data[i]);
        }
    }

    /**
     * Returns a transposed matrix of this class.
     *
     * @return Transposed matrix.
     */
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < A.rows(); i++) {
            for (int j = 0; j < A.cols(); j++) {
                A.set(i, j, this.get(j, i));
            }
        }
        return A;
    }

    /**
     * Returns a summation of current and passed matrices.
     *
     * @param B Matrix passed in the parameter.
     * @return Summation of matrices.
     */
    public Matrix plus(Matrix B) {
        Matrix A = this;
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < C.rows(); i++) {
            for (int j = 0; j < C.cols(); j++) {
                C.set(i, j, A.get(i, j) + B.get(i, j));
            }
        }
        return C;
    }


    /**
     * Returns a difference of current and passed matrices.
     *
     * @param B Matrix passed in the parameter.
     * @return Difference of matrices.
     */
    public Matrix minus(Matrix B) {
        Matrix A = this;
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < C.rows(); i++) {
            for (int j = 0; j < C.cols(); j++) {
                C.set(i, j, A.get(i, j) - B.get(i, j));
            }
        }
        return C;
    }

    /**
     * Returns a matrix where each element has been decreased by a constant value
     * passed in the parameter.
     *
     * @param constant Constant that is taken of all values.
     * @return Resulting matrix.
     */
    public Matrix minus(double constant) {
        Matrix A = this;
        Matrix B = new Matrix(M, N);
        for (int i = 0; i < B.rows(); i++) {
            for (int j = 0; j < B.cols(); j++) {
                B.set(i, j, A.get(i, j) - constant);
            }
        }
        return B;
    }

    /**
     * Returns a matrix product of two matrices.
     * If current matrix is of dimensions [A x B] and matrix passed in the parameters
     * is of dimensions [B x C], the resulting matrix is of dimensions [A x C].
     *
     * @param B Matrix passed in the parameter.
     * @return Resulting matrix of dimensions [A x C].
     */
    public Matrix times(Matrix B) {
        Matrix A = this;
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.rows(); i++) {
            for (int j = 0; j < C.cols(); j++) {
                for (int k = 0; k < A.cols(); k++) {
                    C.set(i, j, C.get(i, j) + A.get(i, k) * B.get(k, j));
                }
            }
        }
        return C;
    }

    /**
     * Prints current matrix to the standard output.
     */
    public void show() {
        System.out.printf("DIMS: %dx%d\n", this.M, this.N);
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++)
                System.out.printf("%9.4f ", data[i][j]);
            System.out.println();
        }
    }

    /**
     * Prints the specified row of the matrix.
     *
     * @param row Index of the row.
     */
    public void show_row(int row) {
        System.out.printf("DIMS: %dx%d\n", this.M, this.N);
        System.out.printf("ROW: %d\n", row);
        for (int i = 0; i < cols(); i++) {
            System.out.printf("%9.4f ", data[row][i]);
        }
        System.out.println();
    }

    /**
     * Prints the specified column of the matrix.
     *
     * @param col Index of the column.
     */
    public void show_col(int col) {
        System.out.printf("DIMS: %dx%d\n", this.M, this.N);
        System.out.printf("COL: %d\n", col);
        for (int i = 0; i < rows(); i++) {
            System.out.printf("%9.4f ", data[i][col]);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Sets elements of current matrix to random values sampled
     * from Gaussian distribution in range [0, 1).
     */
    public void gaussian() {
        Random r = new Random();
        Matrix A = this;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                A.set(i, j, r.nextGaussian());
            }
        }
    }

    /**
     * Returns number of columns in this matrix. For readability purposes.
     *
     * @return Number of columns.
     */
    public int cols() {
        return this.N;
    }

    /**
     * Returns number of rows in this matrix. For readability purposes.
     *
     * @return Number of rows.
     */
    public int rows() {
        return this.M;
    }

    /**
     * Sets the element in 'row' and 'col' in this matrix to 'val'.
     *
     * @param row Row of the matrix.
     * @param col Column of the matrix.
     * @param val Value to be set.
     */
    public void set(int row, int col, double val) {
        this.data[row][col] = val;
    }

    /**
     * Sets the element in double 'row' and double 'col' to 'val'. To avoid casting issues.
     *
     * @param row Double row.
     * @param col Double column.
     * @param val Value to be set.
     */
    public void set(double row, double col, double val) {
        this.data[(int) row][(int) col] = val;
    }

    /**
     * Returns the value of element in A['row']['col'].
     *
     * @param row Row of the matrix.
     * @param col Column of the matrix.
     * @return The value.
     */
    public double get(int row, int col) {
        return this.data[row][col];
    }

    /**
     * Returns the value of element in A[double 'row'][double 'col']. To avoid casting issues.
     *
     * @param row Double row.
     * @param col Double column.
     * @return The value.
     */
    public double get(double row, double col) {
        return this.data[(int) row][(int) col];
    }

    /**
     * Returns data array.
     *
     * @return The data.
     */
    public double[][] get() {
        return this.data;
    }

    /**
     * Multiplies vector ([N x 1] matrix) with a matrix. Returns a vector ([M x 1] matrix).
     * To avoid the need to transpose matrices before multiplying.
     *
     * @param B Matrix to multiply current vector ([N x 1] matrix) with.
     * @return Resulting matrix.
     */
    public Matrix vec_times(Matrix B) {
        Matrix A = this;
        Matrix C = new Matrix(B.cols(), 1);
        for (int i = 0; i < B.rows(); i++) {
            if (A.get(i, 0) != 0) {
                for (int j = 0; j < B.cols(); j++) {
                    C.set(j, 0, A.get(i, 0) * B.get(i, j));
                }
            }
        }
        return C;
    }

    /**
     * Sums elements in each row and returns a column matrix of sums ([M x 1] matrix).
     *
     * @return Resulting vector ([M x 1] matrix).
     */
    public Matrix sum_cols() {
        Matrix A = this;
        Matrix C = new Matrix(A.rows(), 1);
        for (int i = 0; i < A.rows(); i++) {
            double sum = 0;
            for (int j = 0; j < A.cols(); j++) {
                sum += A.get(i, j);
            }
            C.set(i, 0, sum);
        }
        return C;
    }

    /**
     * Multiplies this matrix with a constant.
     *
     * @param constant The constant to multiply with.
     * @return Resulting matrix.
     */
    public Matrix times(double constant) {
        Matrix result = new Matrix(this.rows(), this.cols());
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                result.set(i, j, this.get(i, j) * constant);
            }
        }
        return result;
    }

    /**
     * Returns a sum of all elements of this matrix.
     *
     * @return Double sum.
     */
    public double sum() {
        double sum = 0;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                sum += get(i, j);
            }
        }
        return sum;
    }

    /**
     * Returns maximum value in this matrix.
     *
     * @return Double max value.
     */
    public double max() {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                if (max < get(i, j)) {
                    max = get(i, j);
                }
            }
        }
        return max;
    }

    /**
     * Returns the index of maximum value in a vector ([M x 1] matrix).
     *
     * @return Index of row with the maximum value.
     */
    public int which_max() {
        double max = Double.MIN_VALUE;
        int idx = 0;
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < cols(); j++) {
                if (max < get(i, j)) {
                    max = get(i, j);
                    idx = i;
                }
            }
        }
        return idx;
    }

    /**
     * Multiplies matrices only in specified rows. Rows are passed in a List.
     * Increases performance when only certain rows need multiplying.
     *
     * @param B Matrix to multiply with.
     * @param rows List of rows to multiply.
     * @return Resulting matrix with 0's where rows were not multiplied.
     */
    public Matrix multiply_rows(Matrix B, List<Integer> rows) {
        Matrix A = this;
        Matrix C = new Matrix(A.M, 1);
        for (int i : rows) {
            for (int j = 0; j < A.cols(); j++) {
                C.set(i, 0, C.get(i, 0) + A.get(i, j) * B.get(j, 0));
            }
        }
        return C;
    }

    /**
     * Multiplies matrices only in specified rows. Rows are passed in a List.
     * Increases performance when only certain rows need multiplying.
     *
     * @param B Matrix to multiply with.
     * @param rows List of rows to multiply.
     * @return Resulting vector with 0's where rows were not multiplied.
     */
    public Matrix multiply_rows_to_vector(Matrix B, List<Integer> rows) {
        Matrix A = this;
        Matrix C = new Matrix(B.N, 1);
        for (int j = 0; j < B.cols(); j++) {
            for (int i : rows) {
                C.set(j, 0, C.get(j, 0) + A.get(i, 0) * B.get(i, j));
            }
        }
        return C;
    }

    /**
     * Computes a vector ([M x 1] matrix) of same dimensions, but only sigmoid values passed
     * in parameter 'indexes' are computed.
     *
     * @param indexes List of rows to compute sigmoid function on.
     * @return Vector ([M x 1] matrix) of sigmoid values.
     */
    public Matrix sigmoid(List<Integer> indexes) {
        Matrix A = this;
        Matrix C = new Matrix(A.rows(), A.cols());
        for (int i : indexes) {
            double val = sigmoid(A.get(i, 0));
            C.set(i, 0, val);
        }
        return C;
    }

    /**
     * Computes sigmoid(x) for a double x, passed in parameter.
     *
     * @param x Double x.
     * @return Double sigmoid(x).
     */
    public static double sigmoid(double x) {
        return (1d / (1 + Math.exp(-x)));
    }

    /**
     * Computes a difference of matrices, but only in the row specified by index.
     *
     * @param B Matrix that is used to calculate difference with.
     * @param index Index of the row.
     * @return Resulting matrix of dimensions [M x N].
     */
    public Matrix minus(Matrix B, int index) {
        Matrix A = this;
        Matrix C = new Matrix(A.data);
        for (int j = 0; j < cols(); j++) {
            C.set(index, j, A.get(index, j) - B.get(0, j));
        }
        return C;
    }

    /**
     * Computes a difference of matrices, but only in the rows specified by List of indexes.
     *
     * @param B Matrix that is used to calculate difference with.
     * @param index List of indexes of rows.
     * @return Resulting matrix of dimensions [M x N].
     */
    public Matrix minus(Matrix B, List<Integer> index) {
        Matrix A = this;
        Matrix C = new Matrix(A.data);
        int idx = 0;
        for (int i : index) {
            for (int j = 0; j < rows(); j++) {
                C.set(j, i, A.get(j, i) - B.get(idx, j));
            }
            idx++;
        }
        return C;
    }

    /**
     * Problem specific function.
     *
     * Multiplies two vectors only on specific indices.
     *
     * @param B Second vector ([N x 1] matrix).
     * @param rows Where to multiply.
     * @return Resulting matrix.
     */
    public Matrix multiply_vector_with_indices(Matrix B, List<Integer> rows) {
        Matrix A = this;
        Matrix C = new Matrix(rows.size(), B.cols());
        int num = 0;
        for (int i : rows) {
            for (int j = 0; j < B.cols(); j++) {
                C.set(num, j, A.get(i, 0) * B.get(0, j));
            }
            num++;
        }
        return C;
    }

    /**
     * Calculates e^x_i where x_i are elements of this matrix.
     *
     * @return Matrix of e^x_i elements.
     */
    public Matrix exp() {
        Matrix A = this;
        Matrix C = new Matrix(A.rows(), A.cols());
        for (int i = 0; i < A.rows(); i++) {
            for (int j = 0; j < A.cols(); j++) {
                C.set(i, j, Math.exp(A.get(i, j)));
            }
        }
        return C;
    }
}