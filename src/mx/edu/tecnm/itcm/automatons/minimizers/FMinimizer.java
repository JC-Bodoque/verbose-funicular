package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.automatons.IDFA;
import mx.edu.tecnm.itcm.utils.PairIntList;

public class FMinimizer implements Minimizer {
    static int N = 20000;
    public static boolean verbose;

    @Override
    public DFA minimize(DFA automaton) throws Exception {
        throw new Exception("not available");
    }

    public DFT minimize(DFT automaton) throws Exception {
        throw new Exception("not available");
    }

    @Override
    public IDFA minimize(IDFA automaton) throws Exception {
        IDFA b = automaton;
        do {
            automaton = b;
            b.orderEdges();
            int[] x = fusion(b);
            b = b.quotient(x);
            System.out.println(b.nbStates);
        } while (b.nbStates < automaton.nbStates);
        return b;
    }

    /**
     * Returns the smallest partition pi of states such that pi < t
     */
    public static int[] fusion(IDFA automaton) {
        int[] d = new int[automaton.nbStates];
        int m = 0;
        int count = 0;
        for (int q = 0; q < automaton.nbStates; q++) {
            if (q % 1000 == 0) System.out.println("q = " + q);
            boolean found = false;
            for (int p = 0; p < q && count < N; p++)
                if (equiv(automaton, p, q)) {
                    d[q] = d[p];
                    found = true;
                    count++;
                    break;
                }
            if (!found)
                d[q] = m++;
        }
        System.out.println("endFusion");
        return d;
    }

    public static int[] fusionRandom(IDFA a) {
        int[] d = new int[a.nbStates];
        int m = 0;
        int count = 0, loop = 0;
        for (int q = 0; q < a.nbStates; q++) {
            if (q % 1000 == 0) System.out.println("q = " + q);
            boolean found = false;
            while (loop < q && count < N) {
                int p = (int) (Math.random() * q);
                if (equiv(a, p, q)) {
                    d[q] = d[p];
                    found = true;
                    count++;
                    break;
                }
                loop++;
            }
            if (!found)
                d[q] = m++;
        }
        System.out.println("endFusion");
        return d;
    }

    public static boolean equiv(IDFA a, int p, int q) {
        if (a.isTerminal(p) != a.isTerminal(q)) return false;
        PairIntList l = a.edges[p].front;
        PairIntList m = a.edges[q].front;
        for (; l != null && m != null; l = l.next, m = m.next)
            if (m.val != l.val || l.elem != m.elem)
                return false;
        if (l != null || m != null)
            return false;
        return true;
    }
}
