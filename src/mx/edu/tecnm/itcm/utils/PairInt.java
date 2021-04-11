package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.Alphabet;

/**
 * Ths class is used to handle  pairs of integers, usually
 * interpreted as half-edges (letter, state) in an automaton..
 */
public class PairInt {
    int val;   // first
    int elem;  // second

    PairInt(int c, int a) {
        val = c;
        elem = a;
    }

    PairInt(PairIntList r) {
        val = r.val;
        elem = r.elem;
    }

    int getFirst() {
        return val;
    }

    int getSecond() {
        return elem;
    }

    void setFirst(int x) {
        val = x;
    }

    void setSecond(int x) {
        elem = x;
    }

    public String showAI(String name, Alphabet a) {
        return "\n" + name + " : " + showAI(a) + "\n";
    }

    public String showAI(Alphabet a) {
        return "[" + a.toChar(val) + "," + elem + "] ";
    }

    public String showIA(String name, Alphabet a) {
        return "\n" + name + " : " + showIA(a) + "\n";
    }

    public String showIA(Alphabet a) {
        return "[" + val + "," + a.toChar(elem) + "] ";
    }

    public String showWithoutln(String name) {
        return name + " : " + this;
    }

    public static String show(PairInt p, String name) {
        return "\n" + name + ":" + p + "\n";
    }

    public String show(String name) {
        return "\n" + name + ":" + this + "\n";
    }

    public String toString() {
        return "[" + val + "," + elem + "] ";
    }
}

