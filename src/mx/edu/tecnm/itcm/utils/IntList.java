package mx.edu.tecnm.itcm.utils;

import java.util.LinkedList;

/**
 * This class implements linked lists of integers.
 * Created: Mon Nov 10 11:36:19 2003
 *
 * @author <a href="mailto:berstel@univ-mlv.fr">Jean Berstel</a>
 * @version $Revision: 1.17 $
 */
public class IntList {
    public int val;
    public IntList next;

    public IntList(int p) {
        this(p, null);
    }

    public IntList(int p, IntList s) {
        val = p;
        next = s;
    }

    public IntList() {
    }

    static IntList add(int p, IntList s) {
        return new IntList(p, s);
    }

    public boolean equals(IntList l) {
        if (val != l.val) return false;
        if (next == null) return l.next == null;
        return next.equals(l.next);
    }

    public String toString() {
        String s = " " + val;
        if (next == null)

            return s + ".";
        else
            return s + next;
    }

    public String show(String name) {
        return name + this;
    }
}
