package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.automatons.ICFA;
import mx.edu.tecnm.itcm.automatons.IDFA;

/**
 * This class implements the minimization of deterministic finite automata by
 * double reverse (Brzozowski's algorithm). Only presently available for IDFA.
 */
public class BMinimizer implements Minimizer {

    public static boolean verbose;

    @Override
    public DFA minimize(DFA a) throws Exception {
        throw new Exception("not available");
    }


    @Override
    public DFT minimize(DFT a) throws Exception {
        throw new Exception("not available");
    }

    /**
     * Returns the minimal automaton equivalent to <code>a</code>.
     */
    public IDFA minimizeBis(IDFA a) throws Exception {
        ICFA b = a.reverse();
        IDFA c = b.toIDFA(a.nbStates);
        ICFA d = c.reverse();
        return d.toIDFA(c.nbStates);
    }

    /**
     * Returns the minimal automaton deterministic recognizing the reverse of the
     * words recognized by <code>a</code>.
     */
    public IDFA minimize(IDFA a) throws Exception {
        ICFA b = a.reverse();
        System.out.println("ok reverse");
        if (verbose) System.out.println("ok reverse");
        return b.toIDFA2(a.nbStates);
    }
}
