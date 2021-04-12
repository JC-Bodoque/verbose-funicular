package mx.edu.tecnm.itcm.utils.tries;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.IDFA;
import mx.edu.tecnm.itcm.utils.Dawg;
import mx.edu.tecnm.itcm.utils.Pair;
import mx.edu.tecnm.itcm.utils.PairIntQueue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * An implementation of tries with arrays of variable size.
 * Each node is an array of pointers on its sons. The label
 * is carried by the following node. The size is bounded by
 * the sum of lengths of the nodes. The algorithms are linear
 * in the size of the word times the cardinality of the alphabet.
 * <p>
 * Performances: the trie of the delaf dictionnary (a french dictionnary
 * of all words at all forms -about 1M words)
 * can be computed without memory extension. The result is a trie
 * with about 2M nodes (which indicates that the arity of the nodes
 * is in the avarage close to 2). To transform the trie into an
 * IDFA requires an extension of the stack memory to 120M
 * (using the option -Xmx120m).
 */
public class VariableArrayTrie implements Trie {
    public boolean terminal;
    public char label;
    public int num;
    public VariableArrayTrie[] next;
    private static final VariableArrayTrie[] EMPTY_NODES = new VariableArrayTrie[0];

    /**
     * Creates a node with label <code>c</code>.
     */
    public VariableArrayTrie(char c) {
        label = c;
        next = EMPTY_NODES;
    }

    /**
     * Returns the node <code>Next(c)</code> if it exists
     * and <code>null</code> otherwise.
     */
    public VariableArrayTrie next(char c) {
        for (int k = 0; k < next.length; k++)
            if (next[k].label == c)
                return next[k];
        return null;
    }

    /**
     * Returns the index of a node with label <code>c</code> if
     * there is one and <code>-1</code> otherwise.
     */
    public int index(char c) {
        for (int k = 0; k < next.length; k++)
            if (next[k].label == c)
                return k;
        return -1;
    }

    /**
     * Adds an entry in the <code>next</code> array to allow a transition
     * by <code>c</code>.
     */
    public void add(char c) {
        VariableArrayTrie[] table = new VariableArrayTrie[next.length + 1];
        for (int i = 0; i < next.length; i++)
            table[i] = next[i];
        table[next.length] = new VariableArrayTrie(c);
        next = table;
    }

    /**
     * Removes the entry of index <code>k</code>
     * of the array <code>next</code>.
     */
    public void remove(int k) {
        VariableArrayTrie[] table = new VariableArrayTrie[next.length - 1];
        for (int i = 0; i < k; i++)
            table[i] = next[i];
        for (int i = k + 1; i < next.length; i++)
            table[i] = next[i];
        next = table;
    }

    /**
     * Computes the pair composed of the length of the longest prefix
     * of s[j..n-1] in the trie and the vertex reached by this prefix.
     * Implements the function
     * <code>LongestPrefixInTrie()</code> of Section 1.3.1.
     *
     * @param s the input string
     * @param j the starting index
     * @return the computed pair
     */
    public Pair longestPrefixInTrie(String s, int j) {
        int n = s.length();
        VariableArrayTrie p, q = this;
        for (int i = j; i < n; i++) {
            p = q;
            q = q.next(s.charAt(i));
            if (q == null)
                return new Pair(i - j, p);
        }
        return new Pair(n - j, q);
    }

    /**
     * Implements the function <code>IsInTrie()</code> of Section 1.3.1.
     */
    public boolean isInTrie(String s) {
        return longestPrefixInTrie(s, 0).length == s.length();
    }

    /**
     * Adds the word s to the trie. Implements
     * the function <code>AddToTrie()</code> of Section 1.3.1.
     *
     * @param s the string to be added
     */
    public void addToTrie(String s) {
        int n = s.length();
        Pair v = longestPrefixInTrie(s, 0);
        VariableArrayTrie p = (VariableArrayTrie) v.vertex;
        int j = v.length;
        for (; j < n; j++) {
            p.add(s.charAt(j));
            p = p.next[p.next.length - 1];
        }
        p.terminal = true;
    }

    /**
     * A variant of <code>addToTrie()</code>. Does not
     * use <code>longestPrefixInTrie()</code> and thus more economical in space (?).
     */
    public void addToTrieBis(String s) {
        int n = s.length();
        VariableArrayTrie p = this;
        int i;
        for (i = 0; i < n; i++) {
            char c = s.charAt(i);
            VariableArrayTrie q = p.next(c);
            if (q != null)
                p = q;
            else {
                p.add(c);
                p = p.next[p.next.length - 1];
            }
        }
        p.terminal = true;
    }

    /**
     * A variant of <code>addToTrie()</code>. Adds the reverse of the
     * word <code>s</code>
     */
    public void addToTrieTer(String s) {
        int n = s.length();
        VariableArrayTrie p = this;
        int i;
        for (i = n; i > 0; i--) {
            char c = s.charAt(i - 1);
            VariableArrayTrie q = p.next(c);
            if (q != null)
                p = q;
            else {
                p.add(c);
                p = p.next[p.next.length - 1];
            }
        }
        p.terminal = true;
    }

    /**
     * Returns true if the node <code>p</code> is a leaf of the trie.
     * Implements
     * the function <code>IsLeaf()</code> of Section 1.3.1.
     */
    public boolean isLeaf() {
        return next.length == 0;
    }

    /**
     * Removes the string s from the trie. Implements
     * the function <code>RemoveFromTrie()</code> of Section 1.3.1.
     *
     * @param s the string to be removed
     */
    public void removeFromTrie(String s) {
        int i, n = s.length();
        VariableArrayTrie p = this, q;
        int[] ord = new int[n];
        VariableArrayTrie[] father = new VariableArrayTrie[n];
        for (i = 0; i < n; i++) {
            q = p;
            int k = p.index(s.charAt(i));
            father[i] = q;
            if (k == -1) return;
            p = p.next[k];
            ord[i] = k;
        }
        p.terminal = false;
        while (p.isLeaf() && !p.terminal) {
            i--;
            p = father[i];
            p.remove(ord[i]);
        }
    }

    /**
     * Builds a trie representing the list of strings read
     * from a file line by line.
     *
     * @param name the name of the file
     */
    public void fromFile(String name) throws IOException {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        int count = 0;
        while ((line = r.readLine()) != null) {
            count++;
            if (count % 100000 == 0) {
                System.out.println("nbWords = " + count);
            }
            addToTrieBis(line);
        }
        fileIn.close();
    }

    /**
     * Builds a trie representing the list of the reverse of strings read
     * from a file line by line.
     *
     * @param name the name of the file
     */
    public void fromFileBis(String name) throws IOException {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        int count = 0;
        while ((line = r.readLine()) != null) {
            count++;
            if (count % 100000 == 0) {
                System.out.println("nbWords = " + count);
            }
            addToTrieTer(line);
        }
        fileIn.close();
    }

    String toWords(String w, VariableArrayTrie p) {
        //list of words in the trie
        String s = new String();
        if (p.terminal) s = w + '\n';
        for (int i = 0; i < p.next.length; i++) {
            VariableArrayTrie q = p.next[i];
            s = s + toWords(w + q.label, q);
        }
        return s;
    }

    /**
     * Returns the number of nodes of the trie.
     */
    public int size() {
        int r = 1;
        for (int i = 0; i < next.length; i++)
            r += next[i].size();
        return r;
    }

    /**
     * Numbers the nodes of a trie through an initilization of
     * the field <code>num</code>. All leaves get the <code>num = -1</code>.
     *
     * @param p the name of the root
     * @return <code>p</code> + the number of internal nodes of the trie.
     */
    public int enumer(int p) {
        if (next.length == 0)
            num = -1;
        else {
            num = p++;
            for (int i = 0; i < next.length; i++)
                p = next[i].enumer(p);
        }
        return p;
    }

    /**
     * Creates an IDFA from a trie. All leaves are merged in a unique state.
     */
    public IDFA toIDFAbis(Alphabet alph) {
        int s = enumer(0) + 1;
        IDFA a = new IDFA(s);
        a.initial = 0;
        a.alphabet = alph;
        copybis(a);
        for (int i = 0; i < a.nbStates; i++)
            if (a.edges[i] == null)
                a.edges[i] = new PairIntQueue();
        return a;
    }

    public void copybis(IDFA a) {
        if (num == -1)
            num = a.nbStates - 1;
        if (terminal)
            a.addTerminal(num);
        for (int i = 0; i < next.length; i++) {
            int q = next[i].num;
            if (q == -1)
                q = a.nbStates - 1;
            a.addEdgeFast(num, next[i].label, q);
            next[i].copybis(a);
            next[i] = null;
        }
    }

    public DFA toDFAbis(Alphabet alph) {
        int s = enumer(0) + 1;
        DFA a = new DFA(s, alph);
        a.initial = 0;
        copybis(a);
        return a;
    }

    public void copybis(DFA a) {
        if (num == -1)
            num = a.nbStates - 1;
        if (terminal)
            a.terminal.transfer(num, 0, 1);
        for (int i = 0; i < next.length; i++) {
            int q = next[i].num;
            if (q == -1)
                q = a.nbStates - 1;
            a.next[num][a.alphabet.toShort(next[i].label)] = q;
            next[i].copybis(a);
            next[i] = null;
        }
    }

    public IDFA toIDFA(Alphabet alph) {
        int s = size();//System.out.println("size of Trie = " + s);
        IDFA a = new IDFA(s);
        a.initial = 0;
        a.alphabet = alph;
        copy(0, a);
        for (int i = 0; i < a.nbStates; i++)
            if (a.edges[i] == null)
                a.edges[i] = new PairIntQueue();
        return a;
    }

    public int copy(int p, IDFA a) {
        int q = p + 1;
        if (terminal)
            a.addTerminal(p);
        for (int i = 0; i < next.length; i++) {
            if (a.edges[p] == null)
                a.edges[p] = new PairIntQueue();
            a.addEdge(p, next[i].label, q);
            q = next[i].copy(q, a);
            next[i] = null;
        }
        return q;
    }

    public DFA toDFA(Alphabet alph) {
        int s = size();
        DFA a = new DFA(s, alph);
        a.initial = 0;
        copy(0, a);
        return a;
    }

    public int copy(int p, DFA a) {
        int q = p + 1;
        if (terminal)
            a.terminal.transfer(p, 0, 1);
        for (int i = 0; i < next.length; i++) {
            a.next[p][a.alphabet.toShort(next[i].label)] = q;
            q = next[i].copy(q, a);
        }
        return q;
    }

    public Dawg toDawg() {
        Dawg g = new Dawg(label);
        g.next = new Dawg[next.length];
        g.terminal = terminal;
        g.size = 1;
        for (int i = 0; i < next.length; i++) {
            g.next[i] = next[i].toDawg();
            g.size += g.next[i].size;
        }
        return g;
    }

    public String toString() {
        return toWords("", this);
    }
}