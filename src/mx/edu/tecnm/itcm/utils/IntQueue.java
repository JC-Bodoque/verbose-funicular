package mx.edu.tecnm.itcm.utils;

/**
 * This class implements queues of integers as linked lists
 * from the class {@link IntList IntList}. Insertions are
 * made at the rear and deletions at the front. The link
 * is from front to rear.
 */
public class IntQueue {
    public IntList front = null, rear = null;
    public int size = 0;

    /**
     * Adds the integer <code>val</code> to the queue.
     *
     * @param val an integer.
     */
    public void add(int val) {
        size++;
        if (front == null)
            front = rear = new IntList(val, null);
        else
            rear = rear.next = new IntList(val, null);
    }

    /**
     * Adds the list <code>l</code> at the rear of the queue.
     *
     * @param l a linked list of integers.
     */
    public void add(IntList l) {
        size++;
        l.next = null;
        if (front == null)
            front = rear = l;
        else
            rear = rear.next = l;
    }

    /**
     * Removes the first element of the queue.
     * Returns <code>null</code> if the queue is empty.
     *
     * @return the resulting queue.
     */
    public IntList remove() {
        if (front == null) return null;
        IntList result = front;
        if (front == rear)
            front = rear = null;
        else
            front = front.next;
        size--;
        return result;
    }

    /**
     * Empties the queue.
     */
    public void removeAll() {
        front = rear = null;
        size = 0;
    }

    /**
     * Returns true if the queue is empty.
     */
    public boolean isEmpty() {
        return front == null;
    }

    /**
     * Returns the size of the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the last element of the queue.
     */
    public int lastVal() {
        return rear.val;
    }

    /**
     * Returns the penultimate value.
     */
    public int removeVal() {
        return remove().val;
    }

    /**
     * Adds all the elements of the queue <code>s</code>
     * and empties <code>s</code>.
     */
    public void seizeAll(IntQueue s) {
        if (front == null)
            front = s.front;
        else
            rear.next = s.front;
        rear = s.rear;
        size += s.size;
        s.removeAll();
    }

    public String toString() {
        return front.toString();
    }

    public String show(String name) {
        return name + " " + front;
    }

    /**
     * Concatenates the current queue and the queue <code>s</code>.
     *
     * @return the resulting queue.
     */
    public IntQueue append(IntQueue x) {
        if (x.isEmpty())
            return this;
        if (this.isEmpty()) {
            front = x.front;
            rear = x.rear;
            size = x.size;
        } else {
            rear.next = x.front;
            rear = x.rear;
            size += x.size;
        }
        return this;
    }

    /**
     * A variant of <code>append()</code>.
     */
    public IntQueue concat(IntQueue x) {
        while (!x.isEmpty())
            add(x.removeVal());
        return this;
    }
}
