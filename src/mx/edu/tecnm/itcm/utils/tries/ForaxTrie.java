package mx.edu.tecnm.itcm.utils.tries;

import mx.edu.tecnm.itcm.utils.Dawg;
import mx.edu.tecnm.itcm.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Implements Tries with ordered lists of sons for each node.
 * The complexity of all algorithms to process a word of length <code>n</code>
 * is <code>n*log(k)</code> where <code>k</code> is the size
 * of the alphabet.
 *
 * @author remi
 */
public class ForaxTrie implements Trie {
    final char letter;
    private ForaxTrie[] nodes;
    private boolean mark;

    private static final Comparator comparator = new Comparator() {
                public int compare(Object node1, Object node2) {
                    return ((ForaxTrie) node1).letter - ((ForaxTrie) node2).letter;
                }
            };
    private static final ForaxTrie[] EMPTY_NODES = new ForaxTrie[0];

    private ForaxTrie(char letter) {
        this.letter = letter;
        this.nodes = EMPTY_NODES;
    }

    /**
     * Computes the pair composed of
     * the length of the longest prefix of s[j..n-1] in the trie
     * and the vertex reached by this prefix. Implements the function
     * <code>LongestPrefixInTrie()</code> of Section 1.3.1.
     * Complexity: <code>O(|s|*log(k))</code> on an
     * alphabet of size <code>k<:code>.
     *
     * @param s the input string
     * @param j the starting index
     * @return the computed pair
     */
    @Override
    public Pair longestPrefixInTrie(String s, int j) {
        int n = s.length();
        ForaxTrie p = this;
        for (int i = j; i < n; i++) {
            ForaxTrie q = p.next(s.charAt(i));
            if (q == null)
                return new Pair(i - j, p);
            else
                p = q;
        }
        return new Pair(n - j, p);
    }

    /**
     * Returns the son of label <code>c</code> if it exists and
     * <code>null</code> otherwise.
     */
    public ForaxTrie next(char c) {
        ForaxTrie[] nodes = this.nodes;
        ForaxTrie fake = new ForaxTrie(c);
        int index = Arrays.binarySearch(nodes, fake, comparator);
        if (index >= 0)
            return nodes[index];
        else
            return null;
    }

    /**
     * Returns true if the trie contains <code>s</code>. Implements
     * the function <code>IsInTrie()</code> of Section 1.3.1.
     * Complexity: <code>O(|s|*log(k))</code> on an
     * alphabet of size <code>k<:code>.
     */
    @Override
    public boolean isInTrie(String s) {
        return longestPrefixInTrie(s, 0).length == s.length();
    }

    /**
     * Returns the son labeled <code>c</code> if there is one
     * and creates it otherwise. Complexity: <code>log(k)</code>
     * where <code>k</code> is the number of sons (uses a binary
     * search on the on the ordered array <code>nodes</code>).
     */
    public ForaxTrie add(char c) {
        ForaxTrie[] nodes = this.nodes;
        ForaxTrie fake = new ForaxTrie(c);
        int index = Arrays.binarySearch(nodes, fake, comparator);
        if (index < 0) {
            ForaxTrie[] newForaxTries = new ForaxTrie[nodes.length + 1];
            index = -index - 1;
            System.arraycopy(nodes, 0, newForaxTries, 0, index);
            newForaxTries[index] = fake;
            System.arraycopy(nodes, index, newForaxTries, index + 1, nodes.length - index);
            this.nodes = newForaxTries;
            return fake;
        }
        return nodes[index];
    }

    /**
     * Adds the word s to the trie. Implements
     * the function <code>AddToTrie()</code> of Section 1.3.1.
     * Complexity: <code>n*log(k)</code> where <code>n</code> is
     * the length of <code>text</code> and <code>k</code> is the size
     * of the alphabet.
     *
     * @param text the string to be added.
     */
    @Override
    public void addToTrie(String text) {
        ForaxTrie node = this;
        char[] array = text.toCharArray();
        for (int i = 0; i < array.length; i++)
            node = node.add(array[i]);
        node.mark = true;
    }

    /**
     * Returns true if the node <code>p</code> is a leaf of the trie.
     * Implements
     * the function <code>IsLeaf()</code> of Section 1.3.1.
     */
    @Override
    public boolean isLeaf() {
        return nodes.length == 0;
    }

    /**
     * Removes the string s from the trie. Implements
     * the function <code>RemoveFromTrie()</code> of Section 1.3.1.
     * To be written.
     *
     * @param s the string to be removed
     */
    @Override
    public void removeFromTrie(String s) {
        //to be written
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(letter).append(" [");
        for (ForaxTrie node : nodes) {
            stringBuilder.append(node.toString());
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    /**
     * Builds a trie representing the list of strings read
     * from a file line by line.
     *
     * @param name the name of the file
     */
    @Override
    public void fromFile(String name) throws IOException {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        //int count=0;
        while ((line = r.readLine()) != null) {
            addToTrie(line);
        }
        fileIn.close();
    }


    public void print(PrintStream output) {
        StringBuffer buffer = new StringBuffer(16);
        for (int i = 0; i < nodes.length; i++) {
            ForaxTrie node = nodes[i];
            node.print(buffer, output);
        }
    }

    private void print(StringBuffer buffer, PrintStream output) {
        buffer.append(letter);
        if (mark)
            output.println(buffer);
        for (int i = 0; i < nodes.length; i++) {
            ForaxTrie node = nodes[i];
            node.print(buffer, output);
        }
        buffer.setLength(buffer.length() - 1);
    }

    public Dawg toDawg() {
        Dawg g = new Dawg(letter);
        g.next = new Dawg[nodes.length];
        g.terminal = mark;
        for (int i = 0; i < nodes.length; i++)
            g.next[i] = nodes[i].toDawg();
        return g;
    }

    public static void main(String[] args) throws IOException {
        ForaxTrie t = new ForaxTrie(' ');
        t.fromFile(args[0]);
    }
}
