package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.utils.HalfEdge;
import mx.edu.tecnm.itcm.automatons.minimizers.NMinimizer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements nondeterministic finite-state
 * transducers (NFT). The states are represented by integers
 * and the transitions by an array of sets of
 * ternary half-edges, i.e. triples
 * <code>(s, t, q)</code> of an input word,
 * an output word  and a state (class
 * <A href="HalfEdge.html"> HalfEdge</A>).
 * A set of half edges is represented by a TreeSet.
 * The sets of initial and terminal states are represented
 * by <code>TreeSet</code> objects.
 * The alphabet is given as an object of the class
 * <A href="Alphabet.html"> Alphabet</A>.
 */
public class NFT extends NFA {
    /**
     * Creates an NFT <code>n</code> with states.
     */
    public NFT(int n) {
        super(n);
    }

    /**
     * Creates an NFT with <code>n</code> states
     * and <code>k</code> letters.
     */
    public NFT(int n, int k) {
        this(n, new Alphabet(k));
    }

    /**
     * Creates an NFT with <code>n</code> states
     * on the alphabet <code>a</code>.
     */
    public NFT(int n, Alphabet a) {
        this(n);
        alphabet = a;
        nbLetters = alphabet.size();
    }

    /**
     * The circular right shift. A 4 states NFA (deterministic,
     * except for the two initial states.
     */
    public static NFT circularRightShift() {
        NFT s = new NFT(4, 2);
        s.initial.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(0));
        s.next[0].add(new HalfEdge("a", "a", 0));
        s.next[0].add(new HalfEdge("b", "a", 1));
        s.next[1].add(new HalfEdge("b", "b", 1));
        s.next[1].add(new HalfEdge("a", "b", 0));
        s.initial.add(new HalfEdge(3));
        s.terminal.add(new HalfEdge(3));
        s.next[2].add(new HalfEdge("a", "a", 2));
        s.next[2].add(new HalfEdge("b", "a", 3));
        s.next[3].add(new HalfEdge("b", "b", 3));
        s.next[3].add(new HalfEdge("a", "b", 2));
        return s;
    }

    /**
     * The literal NFT realizing the Fibonacci morphism
     * <code>a->ab, b->a</code>.
     */
    public static NFT fibonacci() { //a->ab b->a
        NFT s = new NFT(2, 2);
        s.initial.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(0));
        ;
        s.next[0].add(new HalfEdge("a", "a", 1));
        s.next[1].add(new HalfEdge("", "b", 0));
        s.next[0].add(new HalfEdge("b", "a", 0));
        return s;
    }

    /**
     * An NFT realizing <code>\e \times a^*</code>.
     */
    public static NFT epsilon() {
        NFT s = new NFT(1, 1);
        s.initial.add(new HalfEdge(0));
        s.next[0].add(new HalfEdge("", "a", 0));
        return s;
    }

    /**
     * A determinisable NFT.
     */
    public static NFT saka1() {
        NFT s = new NFT(3, 1);
        s.initial.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(1));
        s.next[0].add(new HalfEdge("a", "x", 1));
        s.next[0].add(new HalfEdge("a", "xx", 2));
        s.next[1].add(new HalfEdge("a", "x", 2));
        s.next[2].add(new HalfEdge("a", "x", 1));
        return s;
    }

    /**
     * A determinisable NFT.
     */
    public static NFT saka2() {
        NFT s = new NFT(3, 1);
        s.initial.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(1));
        s.next[0].add(new HalfEdge("a", "x", 1));
        s.next[0].add(new HalfEdge("a", "y", 2));
        s.next[1].add(new HalfEdge("a", "", 2));
        s.next[2].add(new HalfEdge("a", "", 1));
        return s;
    }

    /**
     * A nondeterminisable NFT.
     */
    public static NFT saka3() {
        NFT s = new NFT(5, 1);
        s.initial.add(new HalfEdge(2));
        s.terminal.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(3));
        s.next[0].add(new HalfEdge("a", "xx", 1));
        s.next[1].add(new HalfEdge("a", "xx", 0));
        s.next[2].add(new HalfEdge("a", "xx", 1));
        s.next[2].add(new HalfEdge("a", "x", 3));
        s.next[3].add(new HalfEdge("a", "x", 4));
        s.next[4].add(new HalfEdge("a", "x", 3));
        return s;
    }

    /**
     * A non-determinable NFT.
     */
    public static NFT saka4() {
        NFT s = new NFT(3, 1);
        s.initial.add(new HalfEdge(0));
        s.terminal.add(new HalfEdge(1));
        s.next[0].add(new HalfEdge("a", "x", 1));
        s.next[0].add(new HalfEdge("a", "yx", 2));
        s.next[1].add(new HalfEdge("a", "x", 2));
        s.next[2].add(new HalfEdge("a", "x", 1));
        return s;
    }

    /**
     * Returns the composition of the NFT <code>s</code>
     * and <code>t</code>, which are supposed to be literal.
     * The states are pairs <code>(p,q)</code> of a state
     * of <code>t</code> and
     * a state of <code>s</code> coded by the integer
     * <code>p * t.nbStates + q</code>.
     */
    public static NFT compose(NFT s, NFT t) {
        NFT u = new NFT(s.nbStates * t.nbStates, s.alphabet);
        for (Iterator i = s.initial.iterator(); i.hasNext(); )
            for (Iterator j = t.initial.iterator(); j.hasNext(); ) {
                HalfEdge e = (HalfEdge) i.next();
                HalfEdge f = (HalfEdge) j.next();
                u.initial.add(new HalfEdge(e.end * t.nbStates
                        + f.end));
            }
        for (Iterator i = s.terminal.iterator(); i.hasNext(); )
            for (Iterator j = t.terminal.iterator(); j.hasNext(); ) {
                HalfEdge e = (HalfEdge) i.next();
                HalfEdge f = (HalfEdge) j.next();
                u.terminal.add(new HalfEdge(e.end * t.nbStates
                        + f.end));
            }
        for (int p = 0; p < s.nbStates; p++)
            for (int q = 0; q < t.nbStates; q++) {
                int i = p * t.nbStates + q;
                for (Iterator iter = s.next[p].iterator(); iter.hasNext(); ) {
                    HalfEdge e = (HalfEdge) iter.next();
                    for (Iterator jter = t.next[q].iterator(); jter.hasNext(); ) {
                        HalfEdge f = (HalfEdge) jter.next();
                        if (e.label2.equals(f.label1)) {
                            int j = e.end * t.nbStates + f.end;
                            u.next[i].add(new HalfEdge(e.label1, f.label2, j));
                        }
                    }
                }
                for (Iterator iter = s.next[p].iterator(); iter.hasNext(); ) {
                    HalfEdge e = (HalfEdge) iter.next();
                    if (e.label2.length() == 0) {
                        int j = e.end * t.nbStates + q;
                        u.next[i].add(new HalfEdge(e.label1, "", j));
                    }
                }
                for (Iterator jter = t.next[q].iterator(); jter.hasNext(); ) {
                    HalfEdge f = (HalfEdge) jter.next();
                    if (f.label1.length() == 0) {
                        int j = p * t.nbStates + f.end;
                        u.next[i].add(new HalfEdge("", f.label2, j));
                    }
                }
            }
        return u;
    }

    /**
     * Returns the list of binary half-edges
     * of the form <code>p</code>
     * followed by a path with input  epsilon.
     *
     * @param p    a binary half edge
     * @param mark a boolean array used to mark the visited states
     * @return the set of binary half-edges obtained by following
     * an epsilon input path after <code>p</code>
     */
    public Set closure(HalfEdge p, boolean[] mark)
    //throws Exception
    {
        Set res = new TreeSet();
        res.add(p);
        mark[p.end] = true;
        for (Iterator i = next[p.end].iterator(); i.hasNext(); ) {
            HalfEdge e = (HalfEdge) i.next();
            if (e.label1.length() == 0) {
                HalfEdge q = new HalfEdge(p.label1 + e.label2, e.end);
                if (!mark[e.end])
                    res.addAll(closure(q, mark));
                else if (q.label1.length() != 0) {
                    System.out.println("epsilon input cycle");
                    System.exit(1);
                } else
                    res.add(q);
            }
        }
        return res;
    }

    /**
     * idem from the half edges of the set <code>s</code>
     */
    public Set closure(Set s) {
        Set t = new TreeSet();
        for (Iterator i = s.iterator(); i.hasNext(); ) {
            boolean[] mark = new boolean[nbStates];
            t.addAll(closure((HalfEdge) i.next(), mark));

        }
        return t;
    }

    /**
     * Computes a set transition in an input literal NFT.
     * The transition uses an edge labeled <code>c</code>
     * followed by a path with epsilon input.
     *
     * @param s a set of binary half edges
     * @param c a letter
     * @return the list of half edges obtained
     */
    public Set next(Set s, int c) {
        Set res = new TreeSet();
        for (Iterator i = s.iterator(); i.hasNext(); ) {
            HalfEdge p = (HalfEdge) i.next();
            int q = p.end;
            for (Iterator j = next[q].iterator(); j.hasNext(); ) {
                HalfEdge e = (HalfEdge) j.next();
                if (e.label1.length() == 1 &&
                        e.label1.charAt(0) == alphabet.toChar(c))
                    res.add(new HalfEdge(p.label1 + e.label2, e.end));
            }
        }
        return closure(res);

    }

    /**
     * Returns the longest common prefix of the half-edges forming
     * the set <code>s</code>.
     *
     * @param s a set of binary half-edges
     * @return the longest common prefix of the half-edges forming
     * the set <code>s</code>.
     */
    public static String longestCommonPrefix(Set s) {
        if (s == null) return "";
        Iterator i = s.iterator();
        if (!i.hasNext())
            return "";
        HalfEdge e = (HalfEdge) i.next();
        String res = e.label1;
        while (i.hasNext()) {
            HalfEdge f = (HalfEdge) i.next();
            String t = f.label1;
            res = DFT.lcp(res, t);
        }
        return res;
    }

    /**
     * Erases the lCP of all strings in a set of binary half-edges.
     */
    public void normalize(Set s) {
        int i = longestCommonPrefix(s).length();
        for (Iterator iter = s.iterator(); iter.hasNext(); ) {
            HalfEdge e = (HalfEdge) iter.next();
            e.label1 = e.label1.substring(i);
        }
    }

    /**
     * Implements the function Explore(t, s, b) which returns
     * the list of sets of half edges realizing the determinization
     * of the NFT. The third argument is the resulting DFT.
     * The exploration  starts at the element <code>s</code> of
     * <code>t</code> with order <code>p</code>.
     *
     * @param t a linked list of sets of half-edges.
     * @param p the order of the starting list.
     * @param b the resulting DFT.
     * @return the linked list of states of <code>b</code>.
     */
    public LinkedList explore(LinkedList t, int p, DFT b)
            throws Exception {
        for (int c = 0; c < nbLetters; c++) {
            Set s = (Set) t.get(p);
            Set sc = next(s, c);
            String o = longestCommonPrefix(sc);
            normalize(sc);
            if (tooLong(sc)) throw new Exception("Too long output\n");
            int q = t.indexOf(sc);
            if (q == -1) {                  /* sc is new */
                t.addLast(sc);
                int n = t.size() - 1;
                b.next[p][c] = n;
                b.output[p][c] = o;
                try {
                    b.terminalOutput[n] = output(sc);
                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(1);
                }
                t = explore(t, n, b);
            } else {
                b.next[p][c] = q;
                b.output[p][c] = o;
            }
        }
        return t;
    }

    /**
     * Returns the string <code>w</code> such that <code>(w,t)</code>
     * is in the set <code>s</code> for some terminal state <code>t</code>.
     * Throws an exception if there are several possible strings.
     */
    public String output(Set s) throws Exception {
        String w = null;
        for (Iterator j = s.iterator(); j.hasNext(); ) {
            HalfEdge e = (HalfEdge) j.next();
            if (terminal.contains(new HalfEdge(e.end)))
                if (w == null)
                    w = e.label1;
                else
                    throw new Exception("Multiple output");
        }
        return w;
    }

    /**
     * The maximal length of outputs in an NFT.
     */
    public int LmaxOutput() {
        int max = 0;
        for (int i = 0; i < nbStates; i++) {
            Set l = next[i];
            for (Iterator iter = l.iterator(); iter.hasNext(); ) {
                HalfEdge e = (HalfEdge) iter.next();
                int n = e.label2.length();
                if (n > max)
                    max = n;
            }
        }
        return max;
    }

    /**
     * Returns true if the label of a half-edge in the set <code>l</code>
     * exceeds the bound <code>2 * LmaxOutput() * n * n</code>.
     */
    public boolean tooLong(Set l) {
        int max = 2 * LmaxOutput() * nbStates * nbStates;
        for (Iterator iter = l.iterator(); iter.hasNext(); ) {
            HalfEdge e = (HalfEdge) iter.next();
            if (e.label1.length() > max)
                return true;
        }
        return false;
    }

    Set initial() {
        Set res = new TreeSet();
        for (Iterator i = initial.iterator(); i.hasNext(); ) {
            HalfEdge e = (HalfEdge) i.next();
            res.add(new HalfEdge("", e.end));
        }
        return res;
    }

    /**
     * Returns the determinization of the NFA.
     */
    public DFT toDFT() {
        LinkedList t = new LinkedList();
        DFT b = new DFT(Nmax, nbLetters);
        b.initial = 0;
        b.initialOutput = "";
        Set I = closure(initial());
        t.add(I);
        try {
            b.terminalOutput[0] = output(I);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        try {
            t = explore(t, 0, b);
        } catch (Exception e) {
            System.out.print(e);
            System.exit(1);
        }
        b.nbStates = t.size();
        return b;
    }
}
