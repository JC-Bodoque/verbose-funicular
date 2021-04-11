package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.Alphabet;

/**
 * This class implements queues of pairs of integers
 * as linked lists from the class {@link PairIntList PairIntList}.
 * Insertions are made at the rear and deletions at the front. The link
 * is from front to rear (i.e. the front is the head of the linked list.
 * The queue is represented by two pointers on {@link PairIntList PairIntList}
 * objects <code>front</code> and <code>rear</code>. An empty queue
 * is a pair <code>(front, rear)</code> such that <code>front</code>
 * is null.
 */
public class PairIntQueue {
    public PairIntList front = null, rear = null;

    /**
     * Returns true if the pair <code>(val, elem)</code> is in the queue.
     */
    public boolean isIn(int val, int elem) {
        for (PairIntList l = front; l != null; l = l.next) {
            if (l.val == val && l.elem == elem)
                return true;
        }
        return false;
    }

    /**
     * Adds the pair <code>(val, elem)</code> to the queue.
     */
    public void add(int val, int elem) {
        if (!isIn(val, elem))
            add(new PairIntList(val, elem, null));
    }

    public void addFast(int val, int elem) {
        add(new PairIntList(val, elem, null));
    }

    /**
     * Adds the head of the list <code>l</code> to the queue.
     */
    public void add(PairIntList l) {
        l.next = null;
        if (front == null)
            front = rear = l;
        else
            rear = rear.next = l;
    }

    /**
     * Removes the first element of the queue.
     *
     * @return the resulting queue (<code>null</code> if the queue
     * is empty).
     */
    public PairIntList remove() {
        if (front == null) return null;
        PairIntList result = front;
        if (front == rear)
            front = rear = null;
        else
            front = front.next;
        return result;
    }

    /**
     * Returns the first element of the queue and removes it.
     */
    public PairInt removeHead() {
        if (front == null) return null;
        PairIntList result = front;
        if (front == rear)
            front = rear = null;
        else
            front = front.next;
        return new PairInt(result);
    }

    /**
     * Returns <code>true</code> if the queue is empty, which means
     * that <code>front</code> is <code>null</code>
     */
    public boolean isEmpty() {
        return front == null;
    }

    public String showAI(String name, Alphabet a) {
        return name + " " + PairIntList.showAI(front, a);
    }

    public String showAI(Alphabet a) {
        return showAI("", a);
    }

    public String show(String name, Alphabet a) {
        return name + " " + front.show(a);
    }

    public String show(Alphabet a) {
        return show("", a);
    }
}
