package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.Alphabet;

/**
 * This class implements linked lists of pairs of integers.
 */
public class PairIntList {
    public int val; // first
    public int elem;  // second
    public PairIntList next;

    PairIntList(int c, int a, PairIntList s) {
        val = c;
        elem = a;
        next = s;
    }

    /**
     * Conversions PairInt <--> PairIntList
     */
    public PairIntList(PairInt d, PairIntList s) {
        this(d.val, d.elem, s);
    }

    /**
     * Returns the first element of the list.
     */
    public PairInt head() { // nonempty list !
        return new PairInt(val, elem);
    }

    /**
     * Returns the first element of the first pair.
     */
    public int getFirst() {
        return val;
    }

    /**
     * Returns the second element of the first pair.
     */
    public int getSecond() {
        return elem;
    }

    public static String showAI(PairIntList l, Alphabet a) {
        if (l == null)
            return ". ";
        else
            return "[" + a.toChar(l.val) + "," + l.elem + "] "
                    + showAI(l.next, a);
    }

    public String show(Alphabet a) {
        String s = "[" + val + "," + a.toChar(elem) + "] ";
        if (next == null)
            return s + ". ";
        else
            return s + next.show(a);
    }

    public String showII(String name) {
        return "\n" + name + " : " + showII() + "\n";
    }

    public String showII() {
        String s = "[" + val + "," + elem + "] ";
        if (next == null)
            return s + ". ";
        else
            return s + next.showII();
    }

    public String showAI(String name, Alphabet a) {
        return "\n" + name + " : " + showAI(this, a) + "\n";
    }

    public String show(String name, Alphabet a) {
        return "\n" + name + " : " + show(a) + "\n";
    }

    public String showWithoutln(String name, Alphabet a) {
        return "\n" + name + " : " + show(a);
    }
}
