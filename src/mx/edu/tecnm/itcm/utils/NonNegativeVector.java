package mx.edu.tecnm.itcm.utils;

/**
 * This class implements non-negative real vectors.
 */
public class NonNegativeVector {
    public float[] coefficients;
    public int dimension;

    /**
     * Creates an <code>n</code> vector with <code>float</code>
     * coordinates.
     */
    public NonNegativeVector(int n) {
        coefficients = new float[n];
        dimension = n;
    }

    /**
     * Computes the scalar product of the vector with
     * the vector <code>y</code>.
     */
    public float scalarProduct(NonNegativeVector y) {
        float s = 0;
        for (int i = 0; i < dimension; i++)
            s += coefficients[i] * y.coefficients[i];
        return s;
    }

    /**
     * Divides all coordinates of the vector by the factor <code>r</code>.
     */
    public NonNegativeVector scale(float r) {
        NonNegativeVector y = new NonNegativeVector(dimension);
        for (int i = 0; i < dimension; i++)
            y.coefficients[i] = r * coefficients[i];
        return y;
    }

    /**
     * Creates the vector of dimension <code>n</code> with
     * all coordinates equal to <code>1</code>..
     */
    static NonNegativeVector ones(int n) {
        NonNegativeVector x = new NonNegativeVector(n);
        for (int i = 0; i < n; i++)
            x.coefficients[i] = 1;
        return x;
    }

    public String toString() {
        String s = "" + coefficients[0];

        for (int i = 1; i < dimension; i++)
            s = s + "," + coefficients[i];
        return s;
    }
}
