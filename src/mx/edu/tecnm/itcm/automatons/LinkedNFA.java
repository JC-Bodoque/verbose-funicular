package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.utils.HalfEdge;
import mx.edu.tecnm.itcm.automatons.minimizers.NMinimizer;

/**
 * Implementation of Thomson's algorithm
 * to perform the operations of union, product
 * and star on automata.
 * The automata have the properties that there are at most
 * two edges going out of a state, there is only one initial
 * state, only one terminal state, no edge comes into the
 * initial state, no edge goes out of the terminal state.
 */
public class LinkedNFA {
    public static int num;      //a global counter to assign names to states
    public State initial;
    public State terminal;

    /**
     * Creates a one-state NFA recognizing epsilon.
     */
    public LinkedNFA() {
        initial = terminal = new State();
    }

    /**
     * Implements BuildAutomaton(a). Creates a two-states NFA
     * recognizing <code>a</code>
     *
     * @param a a character
     */
    public LinkedNFA(char a) {
        initial = new State();
        terminal = new State();
        initial.label = a;
        initial.to1 = terminal;
    }

    /**
     * Implements NewAutomaton(i,t). Creates a NFA with <code>i</code>
     * as initial state and <code>t</code> as terminal state.
     *
     * @param i initial state
     * @param t terminal state
     */
    public LinkedNFA(State i, State t) {
        initial = i;
        terminal = t;
    }

    /**
     * Implements the function AutomataUnion(). Runs in constant time.
     *
     * @param a a NFA
     * @param b another NFA
     * @return the NFA recognizing the union of the sets
     * recognized by <code>a</code> and <code>b</code>.
     */
    public static LinkedNFA automataUnion(LinkedNFA a, LinkedNFA b) {
        if (a == null) return b;
        if (b == null) return a;
        State i = new State();
        State t = new State();
        LinkedNFA c = new LinkedNFA(i, t);
        i.to1 = a.initial;
        i.to2 = b.initial;
        a.terminal.to1 = t;
        b.terminal.to1 = t;
        return c;
    }

    /**
     * Implements the function AutomataProduct(). Runs in constant time.
     *
     * @param a a NFA
     * @param b another NFA
     * @return the NFA recognizing the product of the sets
     * recognized by <code>a</code> and <code>b</code>.
     */
    public static LinkedNFA automataProduct(LinkedNFA a, LinkedNFA b) {
        if (a.initial == a.terminal) return b;
        if (b.initial == b.terminal) return a;
        LinkedNFA c = new LinkedNFA(a.initial, b.terminal);
        a.terminal.to1 = b.initial.to1;
        a.terminal.label = b.initial.label;
        a.terminal.to2 = b.initial.to2;
        return c;
    }

    /**
     * Implements the function NFAStar(). Runs in constant time.
     *
     * @param a a NFA
     * @return the NFA recognizing the star of the set recognized
     * by <code>a</code>.
     */
    public static LinkedNFA automatonStar(LinkedNFA a) {
        State i = new State();
        State t = new State();
        LinkedNFA b = new LinkedNFA(i, t);
        i.to1 = a.initial;
        i.to2 = t;
        a.terminal.to1 = t;
        a.terminal.to2 = a.initial;
        return b;
    }

    public String toString() {  //non destructive
        String s = initial.toString();
        initial.reset();
        return s;
    }

    void reset() {        //restores  mark=false
        initial.reset();
    }

    void renumber() {
        num = 0;
        initial.renumber();
        reset();
    }

    /**
     * Converts a LinkedNFA into an NFA.
     *
     * @param alph the input alphabet
     * @return the equivalent NFA on the alphabet <code>alpha</code>
     */
    public NFA toNFA(Alphabet alph) {
        renumber();
        NFA a = new NFA(num + 1, alph);
        a.initial.add(new HalfEdge(initial.name));
        a.terminal.add(new HalfEdge(terminal.name));
        toNFA(a, initial);
        reset();
        return a;
    }

    /**
     * The recursive call to run <code>toNFA</code>.
     */
    public void toNFA(NFA a, State p) {
        if (p.mark == true) return;
        p.mark = true;
        if (p.to1 != null) {
            if (p.label == 0)
                a.next[p.name].add(new HalfEdge("", p.to1.name));
            else {
                char[] c = {p.label};
                a.next[p.name].add(new HalfEdge(new String(c), p.to1.name));
            }
            toNFA(a, p.to1);
        }
        if (p.to2 != null) {
            a.next[p.name].add(new HalfEdge("", p.to2.name));
            toNFA(a, p.to2);
        }
    }
}
