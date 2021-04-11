package mx.edu.tecnm.itcm.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * This class implements operations on k-tuples needed for
 * the computation of the entropy of a text.
 */
public class Buffer {
    public String word;
    /**
     * The last character of <code>word</code>.
     */
    public int head;

    /**
     * Initializes a buffer composed of the <code>k</code> first characters
     * of a text.
     */
    public Buffer(Reader in, int k) throws IOException {
        int i, v = -1;
        word = "";
        for (i = 0; i < k; i++) {
            v = in.read();
            word = word + (char) v;
        }
        head = v;
    }

    /**
     * Shifts one character to the right the sliding window.
     */
    public void next(Reader in, int k) throws IOException {
        int i, v;
        char[] w = word.toCharArray();
        for (i = 0; i < k - 1; i++)
            w[i] = w[i + 1];
        v = in.read();
        w[i] = (char) v;
        head = v;
        word = new String(w);
    }
}