package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.utils.tries.VariableArrayTrie;

import java.io.IOException;

public class Dawg {
    public char label;
    public boolean terminal;
    public Dawg[] next;
    public int size;

    public Dawg(final char label) {
        this.label = label;
        this.next = new Dawg[0];
    }

    public int size() {
        if (this.next.length == 0) {
            return 1;
        }
        int n = 1;
        for (int i = 0; i < this.next.length; ++i) {
            n += this.next[i].size();
        }
        return n;
    }

    public int[] terminal() {
        final int[] array = new int[this.size];
        final Dawg[] array2 = this.toArray();
        for (int i = 0; i < this.size; ++i) {
            if (array2[i].terminal) {
                array[i] = 1;
            }
        }
        return array;
    }

    public int toArray(final Dawg dawg, final Dawg[] array, final int n) {
        int array2 = n;
        array[array2++] = dawg;
        for (int i = 0; i < dawg.next.length; ++i) {
            array2 = this.toArray(dawg.next[i], array, array2);
        }
        return array2;
    }

    public Dawg[] toArray() {
        final Dawg[] array = new Dawg[this.size];
        this.toArray(this, array, 0);
        return array;
    }
}
