package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.utils.Partition;
import mx.edu.tecnm.itcm.automatons.minimizers.Minimizer;

import java.util.Arrays;

/**
 * This class implements sequential (or deterministic)
 * finite-state transducers.
 * These inherit from deterministic finite-state automata (DFA) adding
 * an output function, an initial output (associated with the initial state)
 * and a terminal output function. The terminal states are those with
 * a nonempty output.
 */
public class DFT extends DFA {
    public String[][] output;          // the output function
    public String initialOutput;
    public String[] terminalOutput;

    /**
     * creates a DFT with <code>n</code> states and  <code>k</code> letters.
     */
    public DFT(int n, int k) {
        this(n, new Alphabet(k));
    }

    /**
     * creates a DFT with <code>n</code> states and alphabet <code>a</code>.
     */
    public DFT(int n, Alphabet a) {
        super(n, a);
        output = new String[nbStates][nbLetters];
        terminalOutput = new String[nbStates];
    }

    static DFT rightShift() {
        DFT s = new DFT(2, new Alphabet(2));
        s.next[0][0] = 0;
        s.next[0][1] = 1;
        s.next[1][0] = 0;
        s.next[1][1] = 1;
        s.initial = 0;
        s.output[0][0] = "a";
        s.output[0][1] = "a";
        s.output[1][0] = "b";
        s.output[1][1] = "b";
        s.initialOutput = "";
        s.terminalOutput[0] = "";
        s.terminal = new Partition(new int[]{1, 0});
        return s;
    }

    /**
     * Returns the output from state <code>q</code> under input
     * <code>w</code> (without regard to terminal states and a possible
     * terminal output).
     */
    public String star(int q, String s) {
        if (s == null) return null;
        return star(q, alphabet.toShort(s));
    }

    /**
     * Returns the output corresponding to the input <code>s</code>.
     */
    public String output(String s) {
        return initialOutput + star(initial, s)
                + terminalOutput[next(initial, s)];
    }

    String star(int q, short[] w) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < w.length; i++) {
            s.append(output[q][w[i]]);
            q = next[q][w[i]];
        }
        return s.toString();
    }

    /**
     * Returns the composition of the DFT <code>a</code> and <code>b</code>.
     * The method is a particular case of the composition of NFT.
     * The corresponding transduction applies first <code>a</code>
     * and then <code>b</code>.
     * The states are pairs <code>(q,p)</code> of a state of
     * <code>b</code> and a state of <code>a</code> coded by
     * the integer <code>q * a.nbStates + p</code>.
     */
    public static DFT compose(DFT a, DFT b) {
        DFT c = new DFT(a.nbStates * b.nbStates, a.alphabet);
        c.initialOutput = b.initialOutput +
                b.star(b.next(b.initial, a.initialOutput), a.initialOutput);
        for (int q = 0; q < b.nbStates; q++)
            for (int p = 0; p < a.nbStates; p++) {
                int i = q * a.nbStates + p;
                for (int u = 0; u < a.nbLetters; u++) {
                    c.next[i][u] = b.next(q, a.output[p][u])
                            * a.nbStates + a.next[p][u];
                    c.output[i][u] = b.star(q, a.output[p][u]);

                }
                String s = a.terminalOutput[p];
                if (s == null)
                    c.terminalOutput[i] = null;
                else
                    c.terminalOutput[i] = b.star(q, s)
                            + b.terminalOutput[b.next(q, s)];
            }
        return c;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("initial :" + initial + ", " + initialOutput + "\n");
        s.append("nbStates=").append(nbStates).append("\n");
        s.append("  ");
        for (int c = 0; c < nbLetters; c++)
            s.append("  ").append(alphabet.toChar(c)).append("    ");
        s.append("output\n");
        for (int i = 0; i < nbStates; i++) {
            s.append(i).append(" ");
            for (int c = 0; c < nbLetters; c++)
                s.append("(").append(output[i][c]).append(", ").append(next[i][c]).append(") ");
            s.append(terminalOutput[i]);
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Returns the longest common prefix of the
     * strings <code>s</code> and <code>t</code>.
     *
     * @param s a string
     * @param t a string
     * @return the longest common prefix of the
     * strings <code>s</code> and <code>t</code>.
     */
    public static String lcp(String s, String t) {
        int i = 0;
        if (s == null) return t;
        if (t == null) return s;
        while (i < s.length() && i < t.length()
                && s.charAt(i) == t.charAt(i))
            i++;
        if (i == 0)
            return "";
        else
            return s.substring(0, i);
    }

    /**
     * Realizes the iteration m=Um+v with operations
     * in the semiring <code>(lcp,.,0,\e)</code> where
     * <code>m</code> is a vector of strings, <code>U</code>
     * the matrix of outputs and <code>v</code> the vector
     * of terminal outputs.
     *
     * @param m an array of strings or 0 (=null)
     */
    public void refine(String[] m) { //m=Um+v
        for (int i = 0; i < nbStates; i++) {
            for (int c = 0; c < nbLetters; c++) {
                int p = next[i][c];
                String s = output[i][c];
                if (m[p] != null)
                    m[i] = lcp(m[i], s + m[p]);
            }
            m[i] = lcp(terminalOutput[i], m[i]);
        }
    }

    /**
     * Returns the vector of longest common prefixes of an DFT.
     * <code>lcp[p]</code> is the longest common prefix of the
     * strings <code>star(p,w)</code>
     *
     * @return the array of longest common prefixes
     */
    public String[] lcp() {
        String[] m = new String[nbStates];
        String[] n = new String[nbStates];
        do {
            for (int i = 0; i < nbStates; i++)
                n[i] = m[i];
            refine(m);
        } while (!(Arrays.equals(m, n)));
        return m;
    }

    /**
     * Normalizes the DFT, pushing the output to the left.
     */
    public void normalize() {
        String[] m = lcp();
        initialOutput = initialOutput + m[initial];
        for (int i = 0; i < nbStates; i++) {
            for (int c = 0; c < nbLetters; c++) {
                int p = next[i][c];
                String s = output[i][c];
                int n = m[i].length();
                output[i][c] = s.substring(n) + m[p];
            }
            if (terminalOutput[i] != null && terminalOutput[i].length() > 0)
                terminalOutput[i] = terminalOutput[i].substring(m[i].length());
        }
    }

    /**
     * Minimizes the DFT using the method m.
     */
    public static DFT minimize(DFT a, Minimizer m) throws Exception {
        a.normalize();
        return m.minimize(a);
    }

    public int[] initClass() {
        String[] label = new String[nbStates];
        int[] classnb = new int[nbStates];
        for (int i = 0; i < nbStates; i++)
            classnb[i] = 0;
        for (int c = 0; c < nbLetters; c++) {
            for (int i = 0; i < nbStates; i++)
                label[i] = output[i][c];
            classnb = intersection(classnb, label);
        }

        return intersection(classnb, terminalOutput);
    }

    public Partition initPartition() {
        return new Partition(initClass());
    }

    /**
     * returns the partition intersection of <code>table</code> and
     * <code>label</code>.
     *
     * @param table an array of integers
     * @param label an array of String of the same size
     * @return the array resulting of the partition intersection.
     */
    public static int[] intersection(int[] table, String[] label) {
        int n = table.length;
        int[] res = new int[n];
        int index = 0;
        for (int i = 0; i < n; i++) {
            int j;
            for (j = 0; j < i; j++)
                if (table[i] == table[j]
                        && (label[i] == null && label[j] == null ||
                        !(label[i] == null) && label[i].equals(label[j]))) {
                    res[i] = res[j];
                    break;
                }
            if (j == i)
                res[i] = index++;
        }
        return res;
    }

    /**
     * Returns the partition intersection of <code>table1 </code>
     * and <code>table2</code>.
     *
     * @param table1 an array of integers
     * @param table2 an array of integers of the same size
     * @return the array resulting of the partition intersection.
     */
    public static int[] intersection(int[] table1, int[] table2) {
        int n = table1.length;
        int index = 0;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int j;
            for (j = 0; j < i; j++)
                if (table1[i] == table1[j]
                        && table2[i] == table2[j]) {
                    res[i] = res[j];
                    break;
                }
            if (j == i)
                res[i] = index++;
        }
        return res;
    }

    public int index(int[] c) {
        int m = -1;
        for (int i = 0; i < c.length; i++)
            m = Math.max(m, c[i]);
        return 1 + m;
    }

    /**
     * Returns the quotient of  the DFT by the partition <code>c</code>
     *
     * @param c a partition of the state set
     * @return the new DFA
     */
    public DFT quotientDFT(int[] c) {
        int m = index(c);
        DFT s = new DFT(m, alphabet);
        s.initial = c[initial];
        s.initialOutput = initialOutput;
        for (int p = 0; p < nbStates; p++) {
            int q = c[p];
            for (int u = 0; u < nbLetters; u++) {
                s.next[q][u] = c[next[p][u]];
                s.output[q][u] = output[p][u];
            }
            s.terminalOutput[q] = terminalOutput[p];
        }
        s.initial = c[initial];
        return s;
    }

    public DFT quotientDFT(Partition p) {
        return quotientDFT(p.blockName);
    }
}
