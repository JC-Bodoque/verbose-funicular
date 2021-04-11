package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.IDFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.utils.*;

/**
 * A class implementing Hopcroft's minimization algorithm to minimize a DFA.
 * The time complexity is <code>O(n log(n))</code>.
 *
 * HopcroftMinimizer.java
 *
 *  @author Henry McElwaine
 *  @version $Revision: 2021.1 $
 */
public class HopcroftMinimizer implements Minimizer {
    /**
     * A boolean flag allowing to set a verbose mode.
     * Useful for tutorial purpose and also for debugging.
     * Not convenient when the automaton is too large.
     */
    public static boolean verbose = false;

    /**
     * Returns the minimal DFA equivalent to <code>a</code> computed by Hopcroft's
     * algorithm.
     */
    public DFA minimize(DFA a) throws Exception {
        Partition p = stabilize(a, a.terminal);
        return a.quotient(p);
    }

    /**
     * Returns the minimal DFT equivalent to a normalized DFT <code>a</code> computed by Hopcroft's
     * algorithm. The same as the above except for the initial partition which
     * takes into account the output labels.
     */
    public DFT minimize(DFT a) throws Exception {
        Partition p = a.initPartition();
        p = stabilize(a, p);
        return a.quotientDFT(p);
    }

    public IDFA minimize(IDFA a) throws Exception {
        throw new Exception("Illegal minimization method");
    }

    /**
     * This method computes the partition refining <code>partition</code> which
     * is compatible with the DFA <code>a</code>.
     */
    public Partition stabilize(DFA a, Partition partition) {
        // automaton with reverse arrows
        IntList[][] inv = new IntList[a.nbStates][a.nbLetters];
        for (int p = 0; p < a.nbStates; p++)
            for (int c = 0; c < a.nbLetters; c++) {
                int q = a.next[p][c];
                inv[q][c] = new IntList(p, inv[q][c]);
            }
        PairIntQueue anvilQueue = new PairIntQueue(); // the list of anvils
        int[][] anvilTable = new int[a.nbStates][a.nbLetters]; // its characteristic table
        // Initialization of anvils
        int ccc = (partition.blockSize[0] < partition.blockSize[1]) ? 0 : 1;
        // the index of the smallest class
        for (int c = 0; c < a.nbLetters; c++) {
            anvilQueue.add(ccc, c);
            anvilTable[ccc][c] = 1;
        }
        if (verbose) {
            // show inv
            System.out.println("Automaton with reverse arrows. ");
            for (int c = 0; c < a.nbLetters; c++) {
                System.out.print(" " + (char) (c + 'a') + " ");
                for (int q = 0; q < a.nbStates; q++) {
                    System.out.print(" " + q + " <- " + inv[q][c]);
                }
                System.out.println();
            }
            System.out.println(anvilQueue.show("Initial list of anvils.", a.alphabet));
            System.out.println("Initial classes of the partition\n" + partition);
        }
        int[] part = new int[a.nbStates];   // the number of common elements
        int[] sibling = new int[a.nbStates];   // to break classes
        // contains the elements enumerated in part
        IntList[] intersection = new IntList[a.nbStates];
        int etape = 0;
        while (!anvilQueue.isEmpty()) {
            if (etape % 100 == 0) System.out.println(etape);
            etape++;
            if (verbose)
                System.out.print(anvilQueue.show("List of anvils", a.alphabet));
            PairIntList anvil = anvilQueue.remove();
            int E = anvil.val;  // bloc
            int c = anvil.elem; // letter
            anvilTable[E][c] = 0;
            if (verbose) {
                System.out.println();
                System.out.println("------------\n Current anvil : class = " + E +
                        " letter = " + (char) (c + 'a'));
            }
            // the table of classes intersected
            IntList inverse = null;  // the states in a^{-1}E
            for (DListInt l = partition.blockList[E]; l != null; l = l.next) {
                int p = l.element;
                for (IntList m = inv[p][c]; m != null; m = m.next) {
                    int q = m.val;
                    inverse = new IntList(q, inverse);
                }
            }
            if (verbose)
                System.out.println("States in inverse " + inverse);
            IntList meet = null; // the classes B that meet a^{-1}E
            for (IntList m = inverse; m != null; m = m.next) {
                int q = m.val;
                int cc = partition.blockName[q];
                if (part[cc] == 0)
                    meet = new IntList(cc, meet);
                part[cc]++;
                intersection[cc] = new IntList(q, intersection[cc]);
            }
            if (verbose) {
                System.out.println();
                System.out.println("Classes met by inverses");
                for (IntList l = meet; l != null; l = l.next) {
                    int cc = l.val;
                    System.out.print("" + cc + " : ");
                    System.out.print(intersection[cc]);
                    System.out.println();
                }
                System.out.println("Partition of inverses");
                show(part, partition.index);
            }
            int indexP = partition.index; // before breaking
            for (IntList l = meet; l != null; l = l.next) {
                int cc = l.val;
                if (part[cc] < partition.blockSize[cc]) {
                    int dd = sibling[cc] = partition.index++; // new block
                    partition.transfer(intersection[cc], cc, dd);
                    for (int b = 0; b < a.nbLetters; b++) {
                        if (anvilTable[cc][b] == 1) {
                            anvilQueue.add(dd, b);
                            anvilTable[dd][b] = 1;
                        } else {
                            int ee = (partition.blockSize[cc] <= partition.blockSize[dd]) ? cc : dd;
                            anvilQueue.add(ee, b);
                            anvilTable[ee][b] = 1;
                        }
                    }
                }
            }
            if (verbose) {
                System.out.println("Sibling");
                show(sibling, partition.index);
            }
            // nettoyage
            for (IntList l = meet; l != null; l = l.next) {
                int cc = l.val;
                part[cc] = sibling[cc] = 0;
                intersection[cc] = null;
            }
            if (verbose)
                System.out.println(partition);
        }
        return partition;
    }

    static void show(int[] c) {
        for (int i = 0; i < c.length; i++)
            System.out.print(" " + i);
        System.out.println();
        for (int i = 0; i < c.length; i++)
            System.out.print(" " + c[i]);
        System.out.println();
    }

    static void show(int[] c, int dim) {
        for (int i = 0; i < dim; i++)
            System.out.print(" " + i);
        System.out.println();
        for (int i = 0; i < dim; i++)
            System.out.print(" " + c[i]);
        System.out.println();
    }
}
