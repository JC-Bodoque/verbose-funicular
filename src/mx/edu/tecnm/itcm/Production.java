package mx.edu.tecnm.itcm;

/**
 * The objects of this class are productions of context-free grammars.
 *
 * @author Henry McElwaine
 */
public class Production {

    /**
     * The left side (a variable).
     */
    public char left;

    /**
     * The right side (a string of terminals and variables).
     */
    public String right;

    /**
     * Creates a production <code>c -> s</code> with left side
     * <code>c</code> and right side <code>s</code>.
     */
    public Production(char c, String s) {
        left = c;
        right = s;
    }

    public String toString() {
        return left + "->" + right;
    }
}
