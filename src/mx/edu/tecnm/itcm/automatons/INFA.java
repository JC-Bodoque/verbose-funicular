package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.utils.PairIntList;
import mx.edu.tecnm.itcm.utils.PairIntQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements incomplete nondeterministic finite automata
 * (without epsilon transitions).
 * The edges going out of a state are implemented in a Queue
 * (class {@link PairIntQueue PairIntQueue}). The automaton itself
 * is an array of Queues.
 */
public class INFA {
    /**
     * The number of states.
     */
    public int nbStates;
    public int nbLetters;
    /**
     * The set of edges going out of a state.
     */
    public PairIntQueue[] edges;
    /**
     * The set of initial states.
     */
    public Set initial;
    /**
     * The set of terminal states.
     */
    public Set terminal;
    /**
     * The alphabet.
     */
    public Alphabet alphabet;

    public INFA() {
    }

    public INFA(int n) {
        nbStates = n;
        edges = new PairIntQueue[n];
        for (int p = 0; p < n; p++)
            edges[p] = new PairIntQueue();
        initial = new TreeSet();
        terminal = new TreeSet();
    }

    public INFA(int n, Alphabet a) {
        this(n);
        alphabet = a;
        nbLetters = a.size;
    }

    public boolean isTerminal(int p) {
        return terminal.contains(p);
    }

    public void addTerminal(int p) {
        terminal.add(p);
    }

    public void addEdge(int p, char a, int q) {
        edges[p].add(alphabet.toShort(a), q);
    }

    public void addEdge(int p, int a, int q) {
        edges[p].add(a, q);
    }

    public String toString() {
        String s = "nbStates = " + nbStates + "\n initial states = " + initial + "\n ";
        s += "terminals = " + terminal;
        s += "\n Edges : \n";
        for (int p = 0; p < nbStates; p++)
            s += p + " : " + edges[p].showAI(alphabet) + "\n";
        return s;
    }

    public static INFA ex() {
        INFA a = new INFA(4, new Alphabet(2));
        a.addEdge(0, 'a', 1);
        a.addEdge(1, 'a', 2);
        a.addEdge(0, 'b', 3);
        a.addEdge(3, 'a', 2);
        a.addEdge(3, 'b', 2);
        a.addTerminal(2);
        return a;
    }

    /**
     * Computes a set transition in an INFA.
     *
     * @param s the original set of states
     * @return the array of sets of states reachable from s under input c.
     */
    public Set[] next(Set s) {
        int u = alphabet.size;
        Set[] res = new TreeSet[u];
        for (int a = 0; a < u; a++)  //O(u)
            res[a] = new TreeSet();
        for (Iterator i = s.iterator(); i.hasNext(); ) { //O(n+e)
            Integer p = (Integer) i.next();
            PairIntQueue f = edges[p.intValue()];
            for (PairIntList l = edges[p.intValue()].front; l != null; l = l.next) {
                Integer q = l.elem;
                res[l.val].add(q);
            }
        }
        return res;
    }

    /**
     * Implements the function Explore(t, s, b) which returns
     * the list of sets of half edges realizing the determinization
     * of the NFA. The third argument is the resulting DFA. The exploration
     * starts at the element <code>s</code> of <code>t</code>
     * with order <code>p</code>.
     *
     * @param t a linked list of sets of states (implemented as TreeSet).
     * @param p the order of the starting set.
     * @param b the resulting DFA.
     * @return the linked list of states of <code>b</code>.
     */
    public LinkedList explore(LinkedList t, int p, IDFA b) {
        Set l = (Set) t.get(p);
        Set[] next = next(l);
        for (int c = 0; c < nbLetters; c++) {
            Set sc = next[c];
            if (!sc.isEmpty()) {
                int q = t.indexOf(sc);
                if (q == -1) {                        /* sc is new */
                    t.addLast(sc);
                    int n = t.size() - 1;
                    b.addEdge(p, c, n);
                    if (n % 10000 == 0) System.out.println(n);
                    t = explore(t, n, b);
                } else
                    b.addEdge(p, c, q);
            }
        }
        return t;
    }

    /**
     * Implements the determinization algorithm. The sets of states
     * created are represented by an object of the class
     * TreeSet</a>.
     * The set of subsets created
     * is stored as a list using the class
     * LinkedList </a>
     */
    public IDFA toIDFA(int Nmax) {
        LinkedList t = new LinkedList();       /* table of sets of states */
        IDFA b = new IDFA(Nmax, alphabet);
        t.add(initial);             /* add the set of initial states to t */
        t = explore(t, 0, b);
        b.nbStates = t.size();
        b.alphabet = alphabet;
        b.initial = 0;
        for (Iterator i = t.iterator(); i.hasNext(); ) {
            Set p = (Set) i.next();
            for (Iterator j = p.iterator(); j.hasNext(); ) {
                Integer e = (Integer) j.next();
                if (terminal.contains(e))
                    b.addTerminal(t.indexOf(p));
            }
        }
        return b;
    }
}
