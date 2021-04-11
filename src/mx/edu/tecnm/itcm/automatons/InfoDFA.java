package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;

/**
 * An extension of the class DFA allowing to
 * store an information in a state as an integer
 * and to identify a state as being the sink
 * (used for LR analysis).
 */
public class InfoDFA extends DFA {
    public int[] info;
    public int sink;
    public int terminal;

    public InfoDFA(int n, Alphabet a) {
        super(n, a);
        info = new int[n];
        sink = -1;
    }
}
