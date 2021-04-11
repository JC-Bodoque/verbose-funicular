package mx.edu.tecnm.itcm.automatons;

/**
 * Implementation of  NFA by linked lists
 * adapted to Thomson's algorithm. Each state has at
 * most two outgoing edges.  If there is only one,
 * the first one is used. If there are two, they have
 * to be both epsilon edges (label = 0).
 */
public class State {
    int name;

    /**
     * A marker used for the exploration.
     */
    public boolean mark;

    /**
     * The Label of the first outgoing edge.
     */
    public char label;

    /**
     * The ends of the outgoing edges.
     */
    public State to1, to2;

    /**
     * Creates a new state using a name from LinkedNFA.
     */
    public State() {
        name = LinkedNFA.num++;
    }

    public String toString() {
        State p = this;
        String s = "";
        if (!p.mark) {
            p.mark = true;
            if (p.to1 == null)
                s = p.name + "\n";
            else if (p.to2 == null) {
                s = p.name + "(" + p.label + p.to1.name + ")\n";
                s = s + p.to1;
            } else {
                s = p.name + "(" + p.to1.name + ") (" + p.to2.name + ")\n";
                s = s + p.to1 + p.to2;
            }
        }
        return s;
    }

    /**
     * Resets the mark field.
     */
    public void reset() {
        State p = this;
        if (p.mark) {
            p.mark = false;
            if (p.to1 != null)
                p.to1.reset();
            if (p.to2 != null)
                p.to2.reset();
        }
    }

    /**
     * Renumbers the states of a NFA.
     */
    public void renumber() {
        State p = this;
        if (!p.mark) {
            p.mark = true;
            p.name = LinkedNFA.num++;
            if (p.to1 != null)
                p.to1.renumber();
            if (p.to2 != null)
                p.to2.renumber();
        }
    }
}