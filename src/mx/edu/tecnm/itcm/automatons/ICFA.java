package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.utils.PairIntList;
import mx.edu.tecnm.itcm.utils.PairIntQueue;

import java.util.*;

/**
 * This class implements incomplete co-deterministic finite automata (ICFA).
 * It is used mainly as a companion to the class {@link IDFA IDFA}. The
 * method <code>reverse</code> applied to an <code>IDFA</code> produces an
 * <code>ICFA</code> and conversely.
 */
public class ICFA {
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
     * The initial set of states.
     */
    public Set initial;

    /**
     * The terminal state.
     */
    public int terminal;

    /**
     * The alphabet.
     */
    public Alphabet alphabet;

    public ICFA() {
    }

    public ICFA(int n) {
        nbStates = n;
        edges = new PairIntQueue[n];
        for (int p = 0; p < n; p++)
            edges[p] = new PairIntQueue();
        initial = new TreeSet();
    }

    public ICFA(int n, Alphabet a) {
        this(n);
        alphabet = a;
        nbLetters = a.size;
    }

    public void addEdge(int p, char a, int q) {
        edges[p].add(alphabet.toShort(a), q);
    }

    public void addEdge(int p, int a, int q) {
        edges[p].add(a, q);
    }

    public void addEdgeFast(int p, int a, int q) {
        edges[p].addFast(a, q);
    }

    public String toString() {
        String s = "nbStates = " + nbStates + "\n initial states = " + initial + "\n ";
        s += "terminal = " + terminal;
        s += "\n Edges : \n";
        for (int p = 0; p < nbStates; p++)
            s += p + " : " + edges[p].showAI(alphabet) + "\n";
        return s;
    }

    public static ICFA ex() {
        ICFA a = new ICFA(4, new Alphabet(2));
        a.initial.add(0);
        a.addEdge(0, 'a', 1);
        a.addEdge(0, 'a', 2);
        a.addEdge(0, 'b', 1);
        a.addEdge(1, 'b', 3);
        a.addEdge(2, 'a', 3);
        a.terminal = 2;
        return a;
    }

    /**
     * Computes a set transition in an ICFA as an array of sets indexed
     * by the letters. The complexity
     * is <code>O(e log(n))</code> for an <code>NFA</code> with <code>e</code>
     * edges and <code>n</code>
     * states. Indeed, the set <code>s</code> has <code>O(n)</code> elements
     * and each insertion costs time <code>log(n)</code> using a <code>TreeSet</code>
     * to represent the sets <code>s</code> and <code>next(s, c)</code>.
     *
     * @param s the original set of states
     * @return the array of sets of states reachable from s under each input.
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
     * Implements the function Explore(t, s, b) of Section 1.3.3 which returns
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
        if (p % 1000 == 0) System.out.println(p);
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
                    t = explore(t, n, b);
                } else
                    b.addEdge(p, c, q);
            }
        }
        return t;
    }

    /**
     * The same as <code>explore</code> but with a transmission of the
     * index of the set <code>s</code> in the list <code>t</code>.
     */
    public LinkedList exploreBis(LinkedList t, Set s, int p, IDFA b) {
        if (p % 1000 == 0) System.out.println(p);
        Set[] next = next(s);
        for (int c = 0; c < nbLetters; c++) {
            Set sc = next[c];
            if (!sc.isEmpty()) {
                int q = t.indexOf(sc);
                if (q == -1) {                        /* sc is new */
                    t.addLast(sc);
                    int n = t.size() - 1;
                    b.addEdge(p, c, n);
                    t = exploreBis(t, sc, n, b);
                } else
                    b.addEdge(p, c, q);
            }
        }
        return t;
    }

    /**
     * The same as <code>explore</code> but with an implementation of the
     * set of states of the resulting <code>DFA</code> via a <code>HashMap</code>.
     */
    public int explore2(HashMap t, Set s, int nn, IDFA b) {
        if (nn % 10000 == 0) System.out.println("nn = " + nn);
        Set[] next = next(s);
        for (int c = 0; c < nbLetters; c++) {
            Set sc = next[c];
            if (!t.containsKey(sc)) {
                Integer in = nn;
                t.put(sc, in);
                b.addEdgeFast((Integer) t.get(s), c, nn);
                nn = explore2(t, sc, 1 + nn, b);
            } else
                b.addEdgeFast((Integer) t.get(s), c, (Integer) t.get(sc));
        }
        return nn;
    }

    /**
     * The same as <code>toIDFA</code> but with an implementation of the
     * set of states of the resulting <code>IDFA</code> via a <code>HashMap</code>.
     * The keys are the sets of half-edges (with the method <code>hashCode</code>
     * overridden in the class <code>HalfEdge</code>) and the value is the name
     * of the state. Assuming constant time performance for the functions
     * <code>get</code> and<code>put</code>, the complexity is <code>O(m n log(n))</code>
     * on an <code>NFA</code> of size <code>n</code> resulting in a <code>IDFA</code>
     * with <code>m</code> states.
     */
    public IDFA toIDFA2(int Nmax) {
        int nn = 0;
        IDFA b = new IDFA(Nmax, alphabet);
        HashMap t = new HashMap();       /* table of sets of states */
        t.put(initial, nn);             /* add I to t */
        nn = explore2(t, initial, 1 + nn, b);
        b.nbStates = t.size();
        b.alphabet = alphabet;
        b.initial = 0;
        Set tv = t.keySet();
        for (Iterator i = tv.iterator(); i.hasNext(); ) {
            Set p = (Set) i.next();
            if (p.contains(terminal))
                b.addTerminal((Integer) t.get(p));
        }
        return b;
    }

    /**
     * Computes the deterministic automaton (IDFA) obtained by reversing the edges
     * of a codeterministic automaton (ICFA). Complexity <code>O(e)</code>
     * on an <code>ICFA</code> with <code>e</code> edges.
     */
    public IDFA reverse() {
        IDFA r = new IDFA(nbStates, alphabet);
        r.initial = terminal;
        r.terminal.addAll(initial);
        for (int p = nbStates - 1; p >= 0; p--) {
            PairIntList l = edges[p].front;
            edges[p] = new PairIntQueue();
            for (; l != null; l = l.next)
                r.addEdge(l.elem, l.val, p);
        }
        return r;
    }

    /**
     * Implements the determinization algorithm. The sets of states
     * created are represented by an object of the class
     * <a href=  "/usr/local/j2sdk1.4.2_03/docs/api/java/util/TreeSet.html">  TreeSet</a>.
     * The set of subsets created
     * is stored as a list using the class
     * <a href=  "/usr/local/j2sdk1.4.2_03/docs/api/java/util/LinkedList.html"> LinkedList </a>.
     * Implements the function <code>NFAtoDFA</code> of Section 1.3.3.
     */
    public IDFA toIDFA(int Nmax) {
        LinkedList t = new LinkedList();       /* table of sets of states */
        IDFA b = new IDFA(Nmax, alphabet);
        t.add(initial);             /* add the set of initial states to t */
        t = exploreBis(t, initial, 0, b);
        b.nbStates = t.size();
        b.alphabet = alphabet;
        b.initial = 0;
        for (Iterator i = t.iterator(); i.hasNext(); ) {
            Set p = (Set) i.next();
            if (p.contains(terminal))
                b.addTerminal(t.indexOf(p));
        }
        return b;
    }
}
