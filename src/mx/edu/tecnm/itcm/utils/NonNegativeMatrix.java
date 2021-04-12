package mx.edu.tecnm.itcm.utils;

/**
 * This class implements algorithms on nonnegative matrices.
 * The matrices are square matrices with real coefficients.
 */
public class NonNegativeMatrix {

    NonNegativeVector[] rows;
    int dimension;

    /**
     * Creates an <code>n</code> times <code>n</code> matrix.
     */
    public NonNegativeMatrix(int n) {
        dimension = n;
        rows = new NonNegativeVector[n];
        for (int i = 0; i < n; i++)
            rows[i] = new NonNegativeVector(n);
    }

    /**
     * Returns the result of the action of the matrix on the
     * comlumn vector <code>x</code>.
     */
    public NonNegativeVector leftAction(NonNegativeVector x) {
        NonNegativeVector y = new NonNegativeVector(dimension);
        for (int i = 0; i < dimension; i++)
            y.coefficients[i] = rows[i].scalarProduct(x);
        return y;
    }

    /**
     * Returns an approximate value of <code>rho_M</code>, the
     * maximal eigenvalue of the matrix <code>M</code>. The
     * matrix <code>M</code> is supposed to be primitive.
     *
     * @param x a positive vector
     */
    public float dominantEigenvalue(NonNegativeVector x, float epsilon) {
        float r, R;
        NonNegativeVector y;
        do {
            y = leftAction(x);
            r = R = y.coefficients[0] / x.coefficients[0];
            for (int i = 1; i < dimension; i++) {
                float s = y.coefficients[i] / x.coefficients[i];
                if (s < r) r = y.coefficients[i] / x.coefficients[i];
                if (s > R) R = y.coefficients[i] / x.coefficients[i];
                x = y;
            }
            y = y.scale(1 / r);
        } while (R - r > epsilon);
        return r;
    }

    /**
     * Returns a vector <code>y</code> such that <code>My >= ra y</code>
     * iterating the transformation <code>y = (1 / r(y)) M y</code>
     * starting with <code>y = x</code> with
     * <code>r(y) = min((My)_i / y_i)</code> the Wielandt function.
     */
    public NonNegativeVector approximateEigenvector(NonNegativeVector x,
                                                    float ra) {
        float r;
        NonNegativeVector y = new NonNegativeVector(dimension);
        for (int i = 0; i < dimension; i++)
            y.coefficients[i] = x.coefficients[i];
        do {
            y = leftAction(x);
            r = y.coefficients[0] / x.coefficients[0];
            for (int i = 1; i < dimension; i++) {
                float s = y.coefficients[i] / x.coefficients[i];
                if (s < r) r = y.coefficients[i] / x.coefficients[i];
                x = y;
            }
            y = y.scale(1 / r);
        } while (r < ra);
        return y;
    }

    /**
     * The classical <code>2</code> by <code>2</code>
     * matrix of the golden mean system.
     */
    public static NonNegativeMatrix golden() {
        NonNegativeMatrix M = new NonNegativeMatrix(2);
        M.rows[0].coefficients[0] = 1;
        M.rows[0].coefficients[1] = 1;
        M.rows[1].coefficients[0] = 1;
        M.rows[1].coefficients[1] = 0;
        return M;
    }
}


