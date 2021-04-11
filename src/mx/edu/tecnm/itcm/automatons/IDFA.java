package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.automatons.minimizers.*;
import mx.edu.tecnm.itcm.utils.IntQueue;
import mx.edu.tecnm.itcm.utils.PairIntList;
import mx.edu.tecnm.itcm.utils.PairIntQueue;
import mx.edu.tecnm.itcm.utils.Partition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class implements incomplete deterministic automata.
 * The edges going out of a state are implemented in a Queue
 * (class {@link PairIntQueue PairIntQueue}). The automaton itself
 * is an array of Queues. The space used by this implementation
 * is <code>O(n + k + e)</code> for a deterministic automaton
 * on <code>k</code> letters, <code>n</code> states and <code>e</code> edges,
 * instead of <code>O(k n)</code> for the class {@link DFA DFA}. This representation
 * is preferable when <code>k</code> is large.
 */
public class IDFA {

    /**
     * The number of states.
     */
    public int nbStates;

    /**
     * The set of edges going out of a state.
     */
    public PairIntQueue[] edges;

    /**
     * The initial state.
     */
    public int initial;

    /**
     * The array of terminal states. State <code>p</code> is terminal if <code>terminal[p] = 1</code>.
     */
    public Set terminal;

    /**
     * The alphabet.
     */
    public Alphabet alphabet;

    /**
     * Computes the direct product of the IDFA <code>a</code>
     * and <code>b</code>. Both IDFA share the same alphabet.
     */
    public static IDFA product(IDFA a, IDFA b) {
        int n = a.nbStates;
        int m = b.nbStates;
        IDFA c = new IDFA((n + 1) * (m + 1), a.alphabet);
        c.initial = a.initial * (m + 1) + b.initial;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                int k = i * (m + 1) + j;
                if (a.terminal.contains(i) || b.terminal.contains(j))
                    c.terminal.add(k);
                PairIntList l = a.edges[i].front;
                PairIntList ll = b.edges[j].front;
                while (l != null || ll != null) {
                    if (ll == null || (l != null && l.val < ll.val)) {
                        c.addEdge(k, l.val, l.elem * (m + 1) + m);
                        l = l.next;
                    } else if (l == null || l.val > ll.val) {
                        c.addEdge(k, ll.val, n * (m + 1) + ll.elem);
                        ll = ll.next;
                    } else {
                        c.addEdge(k, l.val, l.elem * (m + 1) + ll.elem);
                        l = l.next;
                        ll = ll.next;
                    }
                }
            }
        for (int i = 0; i < n; i++) {
            int k = i * (m + 1) + m;
            if (a.terminal.contains(i))
                c.terminal.add(k);
            for (PairIntList l = a.edges[i].front; l != null; l = l.next)
                c.addEdge(k, l.val, l.elem * (m + 1) + m);
        }
        for (int j = 0; j < m; j++) {
            int k = n * (m + 1) + j;
            if (b.terminal.contains(j))
                c.terminal.add(k);
            for (PairIntList l = b.edges[j].front; l != null; l = l.next)
                c.addEdge(k, l.val, n * (m + 1) + l.elem);
        }
        return c;
    }

    /**
     * True if the automaton is a DAWG, i.e. if the graph of the automaton
     * is acyclic.
     */
    public boolean isAcyclic() {
        int[] mark = new int[nbStates];
        return explore(initial, mark);
    }

    /**
     * A classical depth-first search. Complexity <code>O(m)</code>
     * where <code>m</code> is the number of edges.
     *
     * @param p    the starting state.
     * @param mark an array of marks interpreted as
     *             0 = unmarked 1 = active 2 = terminated.
     * @return true if the graph reachable from state <code>p>/code> is
     * acyclic.
     */
    public boolean explore(int p, int[] mark) {
        mark[p] = 1;
        boolean b = true;
        for (PairIntList f = edges[p].front; f != null; f = f.next) {
            if (mark[f.elem] == 1)
                return false;
            b = b && explore(f.elem, mark);
        }
        mark[p] = 2;
        return b;
    }

    /**
     * Tests wheter the states are on a successful path of the automaton.
     * Uses a depth first search from state <code>p</code> to fill the boolean
     * array <code>isOn</code>.
     *
     * @param p    a state
     * @param isOn A boolean array with <code>isOn[p] = true</code> if
     *             there is a path to a terminal state from state <code>p</code>.
     */
    public void isOnPath(int p, boolean[] isOn) {
        if (terminal.contains(p))
            isOn[p] = true;
        for (PairIntList f = edges[p].front; f != null; f = f.next) {
            isOnPath(f.elem, isOn);
            if (isOn[f.elem])
                isOn[p] = true;
        }
    }

    /**
     * Gives the new names of the states after eliminating those
     * such that <code>b[i] = false</code>
     *
     * @return the integer array <code>index</code> where
     * <code>index[i] = n</code> if <code>i</code> is the nth
     * index such that <code>b[i] = true</code>.
     */
    public int[] renumber(boolean[] b) {
        int[] index = new int[nbStates];
        int n = 0;
        for (int i = 0; i < b.length; i++)
            if (b[i])
                index[i] = n++;
        return index;
    }

    /**
     * A call to <code>enumerate(isOn)</code> returns the number
     * of states of the trimmed automaton.
     *
     * @param b a boolean array
     * @return the number of indices <code>i</code> such that
     * <code>b[i] = true</code>.
     */
    public int enumerate(boolean[] b) {
        int n = 0;
        for (int i = 0; i < b.length; i++)
            if (b[i]) n++;
        return n;
    }

    /**
     * Computes the trimmed automaton equivalent to a given acyclic
     * automaton.
     */
    public IDFA trim() {
        boolean[] isOn = new boolean[nbStates];
        isOnPath(initial, isOn);
        int[] newNames = renumber(isOn);
        int n = enumerate(isOn);
        IDFA b = new IDFA(n, alphabet);
        b.initial = newNames[initial];
        n = 0;
        for (int p = 0; p < nbStates; p++)
            if (isOn[p]) {
                if (terminal.contains(p))
                    b.terminal.add(n);
                for (PairIntList f = edges[p].front; f != null; f = f.next)
                    if (isOn[f.elem])
                        b.addEdge(n, alphabet.toChar(f.val), newNames[f.elem]);
                n++;
            }
        return b;
    }

    /**
     * Adds to the automaton an edge from <code>p</code> to <code>q</code>
     * labeled <code>a</code> if it does not exist already. Complexity
     * <code>O(k n)</code> for an <code>IDFA</code> with <code>k</code>
     * letters and <code>n</code> states.
     */
    public void addEdge(int p, char a, int q) {
        edges[p].add(alphabet.toShort(a), q);
    }

    /**
     * Adds to the automaton an edge from <code>p</code> to <code>q</code>
     * labeled <code>alphabet.toChar(a)</code> if it does not exist already. Complexity
     * <code>O(k n)</code> for an <code>IDFA</code> with <code>k</code>
     * letters and <code>n</code> states.
     */
    public void addEdge(int p, int a, int q) {
        edges[p].add(a, q);
    }

    /**
     * Adds to the automaton an edge from <code>p</code> to <code>q</code>
     * labeled <code>a</code>. Complexity
     * <code>O(1)</code>.
     */
    public void addEdgeFast(int p, char a, int q) {
        edges[p].addFast(alphabet.toShort(a), q);
    }

    /**
     * Adds to the automaton an edge from <code>p</code> to <code>q</code>
     * labeled <code>alphabet.toChar(a)</code>. Complexity
     * <code>O(1)</code>.
     */
    public void addEdgeFast(int p, int a, int q) {
        edges[p].addFast(a, q);
    }

    /**
     * Sorts the outgoing edges in alphabetic order.
     * Complexity <code> O(u+n+e)</code>
     */
    public void orderEdges() {
        int u = alphabet.size;
        PairIntQueue[] v = new PairIntQueue[u];
        for (int a = 0; a < u; a++)  //O(u)
            v[a] = new PairIntQueue();
        for (int p = 0; p < nbStates; p++) { //O(n+e)
            PairIntQueue f = edges[p];
            while (!f.isEmpty()) {
                PairIntList l = f.remove();
                int a = l.val;
                l.val = p;
                v[a].add(l);
            }
        }
        for (int a = 0; a < u; a++) { // O(u+e)
            PairIntQueue h = v[a];
            while (!h.isEmpty()) {
                PairIntList l = h.remove();
                int p = l.val;
                l.val = a;
                edges[p].add(l);
            }
        }
    }

    /**
     * Computes the width (or out degree) of each state.
     * Complexity <code>O(n + e)</code>.
     *
     * @return the array <code>wid</code> of widths. The value of
     * <code>wid[p]</code>
     * is the number of edges going out of <code>p</code>.
     */
    public int[] width() { //O(n+e)
        int[] wid = new int[nbStates];
        for (int p = 0; p < nbStates; p++) { //O(n+e)
            PairIntQueue f = edges[p];
            for (PairIntList l = edges[p].front; l != null; l = l.next)
                wid[p]++;
        }
        return wid;
    }

    /**
     * A recursive method to compute the array of heights from a state
     * <code>p</code>.
     *
     * @param p the start state.
     */
    public int heigthsRecursive(int p, int[] rgs) {
        if (rgs[p] >= 0) return rgs[p];
        int m = -1;
        for (PairIntList f = edges[p].front; f != null; f = f.next)
            m = Math.max(m, heigthsRecursive(f.elem, rgs));
        rgs[p] = ++m;
        return m;
    }

    /**
     * Returns the array of heights. The height of a state <code>p</code>
     * is the maximal
     * length of a path starting at <code>p</code>.
     *
     * @return the array <code>height</code> of heights.
     */
    public int[] heigths() {
        int[] height = new int[nbStates]; // height[p] = height of p
        for (int p = 0; p < nbStates; p++)
            height[p] = -1;
        heigthsRecursive(initial, height);
        return height;
    }

    /**
     * Returns the array of numbers of states by height.
     *
     * @param h the array of heights
     * @return the array <code>hh</code> of numbers of states by heigth.
     * One has <code>hh[p]=k</code> if there are <code>k</code>
     * states at height <code>k</code>.
     */
    public int[] nbByHeight(int[] h) {
        int hh = h[initial]; // height of the automaton
        int[] nh = new int[1 + hh];  // nh[r] is the nb of states at height r
        for (int p = 0; p < nbStates; p++)
            nh[h[p]]++;
        return nh;
    }

    public IDFA mergeLeaves() {
        int count = 0;
        int[] num = new int[nbStates];
        int[] heigth = heigths();
        for (int p = 0; p < nbStates; p++)
            if (heigth[p] != 0)
                num[p] = count++;
            else
                num[p] = -1;
        System.out.println("count = " + count);
        IDFA b = new IDFA(count + 1, alphabet);
        b.initial = num[initial];
        for (int p = 0; p < nbStates; p++) {
            if (isTerminal(p))
                b.terminal.add(num[p]);
            for (PairIntList l = edges[p].front; l != null; l = l.next) {
                int q = num[l.elem];
                if (q == -1)
                    q = count;
                b.edges[num[p]].add(l.val, q);
            }
        }
        return b;
    }

    public IDFA randomFMinimize() {
        orderEdges();
        return RandomFMinimizer.minimize(this);
    }

    static int N = 20;

    public IDFA mixMinimize() throws Exception {
        IDFA a = this;
        int[] x;
        for (int i = 0; i < N; i++) {
            a.orderEdges();
            x = FMinimizer.fusionRandom(a);
            System.out.println("nbStates = " + a.nbStates);
            a.ecoQuotient(x);
        }
        //return RMinimizer.minimize(a);
        return a.minimize(new NMinimizer());
    }

    /**
     * Minimization in linear time using Revuz algorithm.
     */

    public IDFA ecoMinimize() throws Exception {
        orderEdges();
        return EcoRMinimizer.minimize(this);
    }

    public IDFA minimize(Minimizer m) throws Exception {
        return m.minimize(this);
    }

    public void removeEdge(int p, char c) {
        short cc = alphabet.toShort(c);
        PairIntList l = edges[p].front;
        if (l == null) return;
        if (l.val == cc) {
            edges[p].front = l.next;
            return;
        }
        for (l = edges[p].front; l.next != null; l = l.next)
            if (l.next.val == cc) {
                l.next = l.next.next;
                if (l.next == null)
                    edges[p].rear = l;
                return;
            }
    }

    /**
     * Returns the state <code>next(p,c)</code>.
     */
    public int next(int p, char c) {
        for (PairIntList l = edges[p].front; l != null; l = l.next)
            if (l.val == alphabet.toShort(c))
                return l.elem;
        return -1;
    }

    /**
     * Adds a word to the set recognized by an IDFA.
     */
    public IDFA addWord(String s) {
        IDFA a = new IDFA(nbStates + s.length(), alphabet);
        a.initial = initial;
        for (int i = 0; i < nbStates; i++) {
            a.edges[i] = edges[i];
            if (isTerminal(i))
                a.terminal.add(i);
        }
        int p = next(initial, s.charAt(0));
        int q = nbStates;
        a.removeEdge(a.initial, s.charAt(0));
        a.addEdge(a.initial, s.charAt(0), q);
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            a.addEdge(q, c, q + 1);
            if (p != -1) {
                for (PairIntList l = edges[p].front; l != null; l = l.next)
                    if (l.val != alphabet.toShort(c))
                        a.addEdge(q, l.val, l.elem);
                if (terminal.contains(p))
                    a.terminal.add(q);
                p = next(p, c);
            }
            q++;
        }
        a.terminal.add(q);
        return a;
    }

    /**
     * Computes the co-deterministic automaton (ICFA) obtained by reversing the edges
     * of a deterministic automaton (IDFA). Complexity <code>O(e)</code>
     * on an <code>IDFA</code> with <code>e</code> edges.
     */
    public ICFA reverse() {
        ICFA r = new ICFA(nbStates, alphabet);
        r.initial.addAll(terminal);
        r.terminal = initial;
        for (int p = nbStates - 1; p >= 0; p--) {//if(p%10000 == 0)System.out.println(p);
            PairIntList l = edges[p].front;
            edges[p] = new PairIntQueue();
            for (; l != null; l = l.next)
                r.addEdgeFast(l.elem, l.val, p);
        }
        return r;
    }

    public static void main(String[] args) throws Exception {
        IDFA a, b;
        a = mkIDFA();
        a.show("Initial automaton");
        a.orderEdges();

        b = a.minimize(new NMinimizer());
        b.show("Minimal automaton");

    }

    Partition terminalPartition() {
        return new Partition(terminalArray());
    }

    public int[] terminalArray() {
        int[] t = new int[nbStates];
        for (int p = 0; p < nbStates; p++)
            t[p] = (isTerminal(p)) ? 1 : 0;
        return t;
    }

    public int index(int[] c) {
        int m = -1;
        for (int i = 0; i < c.length; i++)
            m = Math.max(m, c[i]);
        return 1 + m;
    }

    public void ecoQuotient(int[] c) {
        int m = index(c);
        initial = c[initial];
        for (int p = 0; p < nbStates; p++) {
            int q = c[p];
            PairIntList l = edges[p].front;
            edges[q] = new PairIntQueue();
            for (; l != null; l = l.next)
                addEdge(q, l.val, c[l.elem]);
            if (isTerminal(p))
                terminal.add(q);
        }
        nbStates = m;
    }

    public boolean isTerminal(int p) {
        return terminal.contains(p);
    }

    public void addTerminal(int p) {
        terminal.add(p);
    }

    public IDFA quotient(int[] c) {
        int m = index(c);
        IDFA s = new IDFA(m, alphabet);
        s.initial = c[initial];
        for (int p = 0; p < nbStates; p++) {
            int q = c[p];
            for (PairIntList l = edges[p].front; l != null; l = l.next)
                s.addEdge(q, l.val, c[l.elem]);
            if (isTerminal(p))
                s.addTerminal(q);
        }
        return s;
    }

    public IDFA quotient(Partition part) {
        int m = part.index;
        int[] c = part.blockName;
        IDFA s = new IDFA(m, alphabet);
        s.initial = c[initial];
        for (int p = 0; p < nbStates; p++) {
            int q = c[p];
            for (PairIntList l = edges[p].front; l != null; l = l.next)
                s.addEdge(q, l.val, c[l.elem]);
            if (isTerminal(p))
                s.addTerminal(q);
        }
        return s;
    }


    //-------- constructors
    public IDFA() {
    }

    public IDFA(int nn) {
        nbStates = nn;
        edges = new PairIntQueue[nn];
        for (int i = 0; i < nn; i++)
            edges[i] = new PairIntQueue();
        terminal = new TreeSet();
    }

    public IDFA(int nn, Alphabet a) {
        this(nn);
        alphabet = a;
    }

    public IDFA(int nn, int q) {
        this(nn, new Alphabet(q));
    }

    public IDFA(IDFA b) {
        nbStates = b.nbStates;
        initial = b.initial;
        alphabet = b.alphabet;
        edges = new PairIntQueue[nbStates];
        System.arraycopy(b.edges, 0, edges, 0, nbStates);
        terminal = b.terminal;
    }

    public String toString() {
        StringBuilder s = new StringBuilder("nbStates = " + nbStates + "\n initial states = " + initial + "\n ");
        s.append("terminal = ").append(terminal);
        s.append("\n Edges : \n");
        for (int p = 0; p < nbStates; p++)
            s.append(p).append(" : ").append(edges[p].showAI(alphabet)).append("\n");
        return s.toString();
    }

    // print the automaton*/
    public void show(String nom) {
        System.out.println(nom);
        System.out.println("Initial state = " + initial);
        System.out.println("Number of states = " + nbStates);
        System.out.print("Terminal states: " + terminal);
        System.out.println();
        System.out.println("Transitions ");
        for (int i = 0; i < nbStates; i++) {
            System.out.print(i + " : ");
            if (edges[i] != null)
                System.out.print(edges[i].showAI(alphabet));
            System.out.println();
        }
    }

    // 2 functions d'affichage
    public void show(String nom, int[] t) {
        System.out.println(nom);
        for (int i = 0; i < t.length; i++)
            System.out.print(i + ":" + t[i] + ", ");
        System.out.println();
    }

    public void show(String nom, IntQueue[] t) {
        System.out.println(nom);
        for (int i = 0; i < t.length; i++) {
            System.out.print(i + " -> ");
            System.out.print(t[i]);
            System.out.println();
        }
    }

    public static IDFA ex() {
        IDFA a = new IDFA(4, new Alphabet(2));
        a.addEdge(0, 'a', 1);
        a.addEdge(1, 'a', 3);
        a.addEdge(0, 'b', 2);
        a.addEdge(2, 'a', 3);
        a.addEdge(2, 'b', 3);
        a.addTerminal(3);
        return a;
    }

    public static IDFA reset() {
        IDFA a = new IDFA(2, 3);
        a.nbStates = 2;
        a.initial = 0;
        a.addTerminal(1);
        a.addEdge(0, 'a', 0);
        a.addEdge(0, 'b', 1);
        a.addEdge(1, 'a', 0);
        a.addEdge(1, 'b', 1);
        return a;
    }

    public static IDFA mkIDFA() {
        IDFA a = new IDFA(7, 3);
        a.nbStates = 7;
        a.initial = 0;
        a.addTerminal(a.nbStates - 1);
        a.addEdge(0, 'b', 3);
        a.addEdge(0, 'c', 2);
        a.addEdge(0, 'a', 1);
        a.addEdge(1, 'a', 3);
        a.addEdge(1, 'b', 4);
        a.addEdge(2, 'b', 4);
        a.addEdge(2, 'a', 5);
        a.addEdge(3, 'a', 6);
        a.addEdge(4, 'b', 6);
        a.addEdge(4, 'a', 6);
        a.addEdge(5, 'a', 6);
        return a;
    }

    public static IDFA mkIDFA(int u) {
        IDFA a = new IDFA(12, 4);
        a.nbStates = 12;
        a.initial = 0;
        a.addTerminal(4);
        a.addTerminal(8);
        a.addTerminal(9);
        a.addEdge(0, 'b', 5);
        a.addEdge(0, 'c', 9);
        a.addEdge(0, 'a', 1);
        a.addEdge(1, 'a', 2);
        a.addEdge(1, 'b', 11);
        a.addEdge(2, 'b', 3);
        a.addEdge(2, 'a', 3);
        a.addEdge(3, 'a', 4);
        a.addEdge(3, 'b', 8);
        a.addEdge(5, 'a', 6);
        a.addEdge(6, 'b', 7);
        a.addEdge(6, 'a', 3);
        a.addEdge(7, 'b', 8);
        a.addEdge(7, 'a', 4);
        a.addEdge(9, 'b', 11);
        a.addEdge(9, 'a', 6);
        a.addEdge(9, 'c', 10);
        a.addEdge(10, 'b', 8);
        a.addEdge(10, 'a', 7);
        a.addEdge(11, 'b', 4);
        a.addEdge(11, 'a', 3);
        return a;
    }

    public static IDFA fromFile(String name) throws IOException, Exception {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        int count = 0;
        IDFA a = new IDFA(1, new Alphabet('\0', 256));
        IDFA b = new IDFA(1, new Alphabet('\0', 256));
        while ((line = r.readLine()) != null) {
            if (count++ % 50 == 0) {
                System.out.println("nbWords = " + count);
                b = b.minimize(new NMinimizer());
                System.out.println("nbStates local = " + b.nbStates);
                a = product(a, b);
                a = a.minimize(new NMinimizer());
                System.out.println("nbStates global = " + a.nbStates);
                //b.show("b = ");
                b = new IDFA(1, new Alphabet('\0', 256));
                //a.show("a = ");
            }
            b = b.addWord(line);
        }
        fileIn.close();
        a = product(a, b);
        a = a.minimize(new NMinimizer());
        System.out.print(a);
        return a;
    }
}
