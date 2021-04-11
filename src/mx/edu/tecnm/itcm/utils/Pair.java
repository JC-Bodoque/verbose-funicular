package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.utils.tries.Trie;

public class Pair {
    public int length;
    public Trie vertex;

    public Pair(int l, Trie v) {
        length = l;
        vertex = v;
    }
}
