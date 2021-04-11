package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.IDFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.utils.PairIntList;
import mx.edu.tecnm.itcm.utils.Partition;

/**
 * This class implements a naive version of the Moore minimization
 * algorithm in <code>O(n^3)</code>.
 */
public class NMinimizer implements Minimizer {
    public static boolean verbose;

    /**
     * Returns the minimal automaton of <code>a</code>.
     */
    @Override
    public DFA minimize(DFA a) throws Exception {
        Partition x = partition(a, a.terminal);
        return a.quotient(x);
    }

    /**
     * Returns the minimal DFT of <code>a</code>.
     */
    @Override
    public DFT minimize(DFT a) throws Exception {
        Partition p = a.initPartition();
        Partition x = partition(a, p);
        return a.quotientDFT(x);
    }

    @Override
    public IDFA minimize(IDFA a) throws Exception {
        a.orderEdges();
        int[] x = partition(a, a.terminalArray());
        IDFA b = a.quotient(x);
        if (verbose) b.show("Minimal Automaton");
        return a.quotient(x);
    }

    /**
     * Computes the Nerode partition from the initial partition <code>p</code>.
     *
     * @param a a DFA
     * @param p the initial partition
     * @return the Nerode partition
     */
    public Partition partition(DFA a, Partition p) {
        Partition q = refine(a, p);
        int[] c = p.blockName;
        while (p.index != q.index) {
            p = q;
            q = refine(a, q);
        }
        return q;
    }

    /**
     * Refines the partition c.
     *
     * @param a    a complete DFA
     * @param part a partition of the state set
     * @return the refined partition
     */
    public Partition refine(DFA a, Partition part) { // refines the partition c
        int m = 0;
        int[] c = part.blockName;
        int[] d = new int[a.nbStates];
        for (int q = 0; q < a.nbStates; q++) {
            boolean found = false;
            for (int p = 0; p < q; p++)
                if (equiv(a, p, q, c)) {
                    d[q] = d[p];
                    found = true;
                    break;
                }
            if (!found) d[q] = m++;
        }
        return new Partition(d);
    }

    /**
     * Tests whether two states <code>p,q</code>
     * are equivalent in the sense that
     * <code>p=q mod c</code> and <code>p.a=q.a mod c</code>
     * for every letter <code>a</code> .
     *
     * @param a a complete DFA
     * @param p a state
     * @param q a state
     * @return true if p=q mod c and p.u=q.u mod c for every letter u
     */
    public boolean equiv(DFA a, int p, int q, int[] c) {
        // tests whether two states are equivalent
        if (c[p] != c[q])
            return false;
        for (int u = 0; u < a.nbLetters; u++)
            if (c[a.next[p][u]] != c[a.next[q][u]])
                return false;
        return true;
    }

    public Partition partition(IDFA a, Partition p) {
        Partition q = refine(a, p);
        while (p.index != q.index) {//System.out.println("while loop");
            p = q;
            q = refine(a, q);
        }
        return q;
    }

    public int[] partition(IDFA a, int[] p) {
        int[] q = refine(a, p);
        while (index(p) != index(q)) {
            //System.out.println("index  = " + index(q));
            p = q;
            q = refine(a, q);
        }
        return q;
    }

    public int[] refine(IDFA a, int[] c) { // refines the partition c
        int m = 0;
        int[] d = new int[a.nbStates];
        for (int q = 0; q < a.nbStates; q++) {
            //if(q % 1000 == 0) System.out.println("q = " + q);
            boolean found = false;
            for (int p = 0; p < q; p++)
                if (equiv(a, p, q, c)) {
                    d[q] = d[p];
                    found = true;
                    break;
                }
            if (!found)
                d[q] = m++;
        }
        return d;
    }

    public Partition refine(IDFA a, Partition c) { // refines the partition p
        int[] d = partition(a, c.blockName);
        return new Partition(d);
    }

    public boolean equiv(IDFA a, int p, int q, int[] c) {
        if (c[p] != c[q])
            return false;
        PairIntList l = a.edges[p].front;
        PairIntList m = a.edges[q].front;
        for (; l != null && m != null; l = l.next, m = m.next)
            if (m.val != l.val || c[l.elem] != c[m.elem])
                return false;
        return l == null && m == null;
    }

    int index(int[] c) {
        int m = -1;
        for (int i = 0; i < c.length; i++)
            m = Math.max(m, c[i]);
        return 1 + m;
    }

}


