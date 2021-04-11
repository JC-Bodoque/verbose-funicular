package mx.edu.tecnm.itcm.utils;

/**
 * This class implements partitions of the set of integers
 * <code>0,...,size</code>. The array <code>blockName</code>
 * gives the class of an integer. The array <code>blockList</code>
 * gives each class  as a doubly linked list
 * (class DListInt). The array <code>address</code> gives the location
 * of an element in its class.
 */
public class Partition implements Cloneable {
    public int size;       // size of the set
    public int index;
    public int[] blockName;  // class of an element (from 0 to n-1)
    public int[] blockSize;   // size of the block   (from 0 to index - 1)
    public DListInt[] blockList;    // list of elements of a block
    public DListInt[] address; // address of an element

    /**
     * Creates the partition with one class (with name 0)
     * of <code>0,...,n</code>
     */
    public Partition(int n) {
        size = n;
        index = 1;
        blockName = new int[n];
        blockSize = new int[n];
        blockList = new DListInt[n];
        address = new DListInt[n];
        for (int i = 0; i < n; i++) {
            DListInt c = new DListInt(i);
            address[i] = c;
            blockList[0] = c.insert(blockList[0]);
            blockName[i] = 0;
        }
        blockSize[0] = n;
    }

    /**
     * Creates a partition according to the class names given
     * in the array <codet</code>.
     */
    public Partition(int[] t) {
        this(t.length);
        int u = 0;
        for (int i = 0; i < t.length; i++)
            u = Math.max(u, t[i]);
        index = u + 1;
        for (int i = 0; i < t.length; i++)
            if (t[i] > 0)
                transfer(i, 0, t[i]);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    /**
     * Transfers <code>q</code> from the class <code>src</code>
     * to the class <code>dest</code>.
     */
    public void transfer(int q, int src, int dest) {
        DListInt dl = address[q];
        blockSize[src]--;
        blockList[src] = dl.remove(blockList[src]);
        blockList[dest] = dl.insert(blockList[dest]);
        blockSize[dest]++;
        blockName[q] = dest;
    }

    /**
     * Transfers the elements of the list
     * <code>list</code> from the class <code>src</code>
     * to the class <code>dest</code>.
     */
    public void transfer(DListInt list, int src, int dest) {
        for (; list != null; list = list.next)
            transfer(list.element, src, dest);
    }

    public void transfer(IntList list, int src, int dest) {
        for (; list != null; list = list.next)
            transfer(list.val, src, dest);
    }

    /**
     * breaks the class <code>src</code> using the
     * block <code>list</code>.
     */
    int splitBlock(DListInt list, int src) {
        transfer(list, src, index);
        return index++;
    }

    /**
     * returns the partition with each class on a line.
     */
    public String toString() {
        String s = "";
        for (int c = 0; c < index; c++)
            s += c + " : " + blockList[c] + "\n";
        return s;
    }
}
