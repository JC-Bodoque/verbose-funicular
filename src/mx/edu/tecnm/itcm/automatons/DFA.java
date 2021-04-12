package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.utils.Partition;
import mx.edu.tecnm.itcm.automatons.minimizers.Minimizer;

/**
 * This class implements deterministic complete finite automata.
 * The transition function is represented by a double array
 * <code>next[][]</code>. The set of terminal states is given
 * by a partition of the set of states
 * (class {@link Partition Partition}).
 * A state <code>q</code> is terminal if
 * <code>terminal.blockName[p] = 1</code>
 * (the default value is <code>0</code>)
 */
public class DFA {
    public int[][] next;          // the next state function
    public int initial;           // the initial state
    public Partition terminal;    // the partition of terminal states
    public Alphabet alphabet;     // the alphabet
    public int nbStates;
    public int nbLetters;
    public int sink;              // the sink (default -1)

    /**
     * Creates a DFA with <code>n</code> states
     * and <code>k</code> letters.
     */
    public DFA(int n, int k) {
        this(n, new Alphabet(k));
    }

    /**
     * creates alphabet DFA with n states and alphabet alphabet.
     */
    public DFA(int n, Alphabet alphabet) {
        nbStates = n;
        this.alphabet = alphabet;
        nbLetters = this.alphabet.size();
        next = new int[nbStates][nbLetters];
        terminal = new Partition(nbStates);
        sink = -1;
    }

    /**
     * returns the state reached from state <code>p</code> after
     * reading the word <code>w</code>.
     *
     * @param p starting state
     * @param w input word (w is not <code>null</code>
     * @return state reached
     */
    public int next(int p, String w) {
        return next(p, alphabet.toShort(w));
    }

    public int next(int p, short[] w) {
        //transition by a word   w in a DFA
        for (int i = 0; i < w.length; i++) {
            p = next[p][w[i]];
            if (p == -1) break;
        }
        return p;
    }

    /**
     * Minimizes the automaton using the method m.
     */
    public static DFA minimize(DFA a, Minimizer m) throws Exception {
        return m.minimize(a);
    }

    public DFA minimize(Minimizer m) throws Exception {
        return m.minimize(this);
    }

    public int index(int[] c) {
        int m = -1;
        for (int i = 0; i < c.length; i++)
            m = Math.max(m, c[i]);
        return 1 + m;
    }

    /**
     * Returns the quotient of the DFA by the partition <code>c</code>
     *
     * @param c a partition of the state set compatible with the DFA
     * @return the new DFA.
     */
    public DFA quotient(int[] c) {
        int m = index(c);
        int[] t = new int[m];
        DFA s = new DFA(m, alphabet);
        s.initial = c[initial];
        for (int p = 0; p < nbStates; p++) {
            int q = c[p];
            for (int u = 0; u < nbLetters; u++)
                s.next[q][u] = c[next[p][u]];
            t[q] = terminal.blockName[p];
        }
        s.terminal = new Partition(t);
        return s;
    }

    public DFA quotient(Partition p) {
        return quotient(p.blockName);
    }

    public static DFA market(int choice) {
        Alphabet al = new Alphabet(2);
        DFA a = new DFA(7, al);
        if (choice == 1) {
            int[] t = new int[]{0, 1, 1, 1, 1, 0, 1};
            a.next[0][0] = 0;
            a.next[0][1] = 0;
            a.next[1][0] = 2;
            a.next[1][1] = 5;
            a.next[2][0] = 3;
            a.next[2][1] = 0;
            a.next[3][0] = 3;
            a.next[3][1] = 4;
            a.next[4][0] = 3;
            a.next[4][1] = 0;
            a.next[5][0] = 6;
            a.next[5][1] = 0;
            a.next[6][0] = 0;
            a.next[6][1] = 5;
            a.terminal = new Partition(t);
        } else {
            a.next[0][0] = 1;
            a.next[0][1] = 2;
            a.next[1][0] = 3;
            a.next[1][1] = 5;
            a.next[2][0] = 5;
            a.next[2][1] = 4;
            a.next[3][0] = 6;
            a.next[3][1] = 6;
            a.next[4][0] = 6;
            a.next[4][1] = 4;
            a.next[5][0] = 6;
            a.next[5][1] = 6;
            a.next[6][0] = 6;
            a.next[6][1] = 6;
        }
        return a;
    }

    public String toString() {
        String s = "initial=" + initial + "\n";
        s += "nbStates=" + nbStates + "\n";
        s += "  ";
        s += "\n";
        StringBuffer u = new StringBuffer(s);
        for (int i = 0; i < nbStates; i++) {
            u.append(i + " ");
            for (int c = 0; c < nbLetters; c++)
                u.append(next[i][c] + " ");
            u.append("\n");
        }
        s = u + "terminals = " + terminal.blockList[1];
        return s;
    }
}
