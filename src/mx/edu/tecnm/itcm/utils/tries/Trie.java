package mx.edu.tecnm.itcm.utils.tries;

import mx.edu.tecnm.itcm.utils.Pair;

import java.io.IOException;

/**
 * Interface for the implementation of the Trie data
 * structure.
 */
public interface Trie {
    /**
     * Computes the pair composed of
     * the length of the longest prefix of s[j..n-1] in the trie
     * and the vertex reached by this prefix. Implements the function
     * <code>LongestPrefixInTrie()</code> of Section 1.3.1.
     *
     * @param s the input string
     * @param j the starting index
     * @return the computed pair
     */
    Pair longestPrefixInTrie(String s, int j);

    /**
     * Returns true if the trie contains <code>s</code>. Implements
     * the function <code>IsInTrie()</code> of Section 1.3.1.
     */
    boolean isInTrie(String s);

    /**
     * Adds the word s to the trie. Implements
     * the function <code>AddToTrie()</code> of Section 1.3.1.
     *
     * @param s the string to be added.
     */
    void addToTrie(String s);

    /**
     * Returns true if the node <code>p</code> is a leaf of the trie.
     * Implements
     * the function <code>IsLeaf()</code> of Section 1.3.1.
     */
    boolean isLeaf();

    /**
     * removes the string s from the trie. Implements
     * the function <code>RemoveFromTrie()</code> of Section 1.3.1.
     *
     * @param s the string to be removed
     */
    void removeFromTrie(String s);

    /**
     * Builds a trie representing the list of strings read
     * from a file line by line.
     *
     * @param name the name of the file
     */
    void fromFile(String name) throws IOException;
}
