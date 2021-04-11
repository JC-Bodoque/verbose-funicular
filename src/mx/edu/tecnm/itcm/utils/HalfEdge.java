package mx.edu.tecnm.itcm.utils;

/**
 * This class implements half-edges which are either
 * unary, i.e. a state or binary, i.e. a
 * pair formed by a  word and a state or
 * ternary, i.e. triples of
 * two words (input and output) and a state.
 */
public class HalfEdge implements Comparable {
    public String label1;
    public String label2;
    public int end;

    /**
     * Creates a unary half-edge.
     */
    public HalfEdge(int q) {
        end = q;
    }

    /**
     * Creates a binary half-edge.
     */
    public HalfEdge(String s, int q) {
        label1 = s;
        end = q;
    }

    /**
     * Creates a ternary half-egde.
     */
    public HalfEdge(String s, String t, int q) {
        label1 = s;
        label2 = t;
        end = q;
    }

    /**
     * Overrides the method <code>Object.compareTo</code>. Orders the half-edges
     * lexicographically first by end, then by input label and last
     * by output label.
     */
    public int compareTo(Object o) {
        HalfEdge f = (HalfEdge) o;
        if (end < f.end)
            return -1;
        if (end > f.end)
            return 1;
        if (label1 == null)
            return 0;
        int v = label1.compareTo(f.label1);
        if (v != 0 || label2 == null)
            return v;
        return label2.compareTo(f.label2);
    }

    /**
     * Overrides the method <code>Object.hashCode</code>.
     */
    public boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    /**
     * Overrides the method <code>Object.hashCode</code>. The hashcode of a half-edge
     * is <ul>
     * <li> <code>end</code> if there are no labels.
     * <li> <code>hashcode(label1) + end</code> if there is one label.
     * <li> <code>hashcode(label1) + hashcode(label2) + end</code>
     * if there are two labels.
     * </ul>
     */
    public int hashCode() {
        return (label1 == null) ?
                end :
                ((label2 == null) ?
                        end + label1.hashCode() :
                        end + label1.hashCode() + label2.hashCode());
    }

    /**
     * Overrides the method <code>toString</code>
     */
    public String toString() {
        if (label1 == null)
            return "(" + end + ")";
        if (label2 == null)
            return "(" + label1 + "," + end + ")";
        return "(" + label1 + "," + label2 + "," + end + ")";
    }
}


