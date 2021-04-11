package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.IDFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.utils.IntList;
import mx.edu.tecnm.itcm.utils.IntQueue;
import mx.edu.tecnm.itcm.utils.PairIntList;
import mx.edu.tecnm.itcm.utils.PartitionS;

/**
 * Minimization  of an acyclic trim automaton in linear time.
 * This algorithm, due to D. Revuz, minimizes an acyclic automaton in time
 * <code> O(q + n + e)</code>, where
 * <ul>
 * <li> <code>q</code> = size of alphabet
 * <li> <code>n</code> = number of states
 * <li> <code>e</code> = number of edges.
 * </ul>
 * Revuz algorithm.
 */
public class RMinimizer implements Minimizer {
    public static boolean verbose = false;
    /**
     * An array used for conversion from global to local  of names in signatures.
     */
    public static int[] g2l;
    /**
     * An array used for conversion from local to global of names in signatures.
     */
    public static int[] l2g;


    /**
     * Transforms the global arry of signatures into a local one.
     *
     * @param sig the global array of signatures of size <code>N</code>
     *            by <code>K>/code>.
     * @param sil the local array of signatures.
     * @return the number of distinct letters appearing.
     **/
    public int g2l(int[][] sig, int[][] sil) {
        int j = 0;
        int K = sig[0].length;
        int N = sig.length;
        for (int k = 0; k < K; k++)
            for (int n = 0; n < N; n++) {
                int c = sig[n][k];
                if (g2l[c] == -1) {
                    g2l[c] = j;
                    l2g[j] = c;
                    j++;
                }
            }
        for (int k = 0; k < K; k++)
            for (int n = 0; n < N; n++)
                sil[n][k] = g2l[sig[n][k]];
        return j;
    }

    /**
     * Reinitializes the arrays <code>g2l</code> and <code>l2g</code>
     */
    public void resetg2l(int J) {
        for (int j = 0; j < J; j++) {
            int c = l2g[j];
            g2l[c] = -1;
            l2g[j] = -1;
        }
    }

    /**
     * Lexicographic sort of the array <code>s</code>. Complexity <code>O(NK)</code>.
     *
     * @param J the number of letters appearing (they run from <code>0</code> to <code>J - 1</code>).
     *          We have <code>J <= N K</code>.
     * @param s a <code>K</code> by <code>N</code> array.
     **/
    public IntQueue radixSort(int[][] s, int J) {
        int N = s.length;
        int K = s[0].length;
        if (verbose) {
            System.out.println("Signatures after g2l");
            for (int n = 0; n < N; n++) {
                System.out.print(n + " : ");
                for (int k = 0; k < K; k++)
                    System.out.print(s[n][k]);
                System.out.println();
            }
        }

        // 1. position of letters
        // pdl = positions of letters: pdl[c] is
        // the list of k such that c appears in column k
        IntQueue[] pdl = positionLetters(s, J);

        // 2. letters at position
        // lap = letters at position
        // lap[k] is the list of letters which appear at position  k
        IntQueue[] lap = letterPositions(pdl, J, K);

        // 3. sorting of s
        // The queue d is initialized to (0,..., N-1), then permuted by K bucket sorts
        IntQueue d = new IntQueue();
        // bucket[c] is the queue of indices n which contain c in position k
        IntQueue[] bucket = new IntQueue[J];
        for (int c = 0; c < J; c++)
            bucket[c] = new IntQueue();
        for (int n = 0; n < N; n++)
            d.add(n);
        for (int k = K - 1; k >= 0; k--)
            d = bucketSort(k, bucket, lap, s, d);
        return d;
    }

    /**
     * Realizes the bucket sort of the queue of integers <code>d</code> according
     * to the value of column <code>k</code> in the array of signatures <code>s</code>.
     *
     * @param k      the column index
     * @param bucket the array of buckets
     * @param lap    the array of lists of letters by position
     * @param s      the array of signatures
     * @param d      the list to be sorted
     * @return the sorted list.
     */
    public IntQueue bucketSort(int k, IntQueue[] bucket, IntQueue[] lap,
                               int[][] s, IntQueue d) {
        if (verbose)
            System.out.println("Sort of column " + k);
        // a: distribute d
        while (!d.isEmpty()) {
            IntList ll = d.remove();
            int n = ll.val; // n is the index of an element of  s
            int c = s[n][k]; // the character at position k in sig[n]
            bucket[c].add(ll);
        }
        // b: reconstruct d
        for (IntList ll = lap[k].front; ll != null; ll = ll.next) {
            // c is a letter which appears at position k
            // bucket[c] is not empty: it is the list of those s which have this letter at position k
            int c = ll.val;
            if (verbose)
                System.out.print(bucket[c].show("bucket[" + c + "] ="));
            d.seizeAll(bucket[c]);
            if (verbose) {
                System.out.print(d.show(" Queue "));
                System.out.println();
            }

        }
        return d;
    }

    /**
     * Returns the array list  of positions of letters.
     *
     * @param s the array of signatures.
     * @param J the size of the returned array.
     * @return the array <code>pdl</code> of lists of positions of letters.
     * <code>pdl[c]</code> is the list of indices <code>k</code> such that <code>s[n][k] = c</code>
     * for some index <code>n</code>.
     */
    public IntQueue[] positionLetters(int[][] s, int J) {
        int N = s.length;
        int K = s[0].length;
        IntQueue[] pdl = new IntQueue[J];
        for (int c = 0; c < J; c++)
            pdl[c] = new IntQueue();
        for (int k = 0; k < K; k++)
            for (int n = 0; n < N; n++) {
                int c = s[n][k]; // c is the letter at position n,k in s
                if (pdl[c].isEmpty() || pdl[c].lastVal() != k)
                    pdl[c].add(k);
            }
        if (verbose) {
            System.out.print("Columns containing the letter");
            for (int c = 0; c < J; c++)
                System.out.print(pdl[c].show("\nc = " + c + " : "));
            System.out.println();
        }
        return pdl;
    }

    /**
     * Returns the array of lists of letters at a position.
     *
     * @param pdl the array of lists of positions of letters.
     * @param J   the size of <code>pdl</code>.
     * @param K   the size of the result.
     * @return the array <code>lap</code> of lists of letters at a position.
     * <code>lap[k]</code> is the list of letters <code>c</code> such that <code>s[n][k] = c</code>
     * for some index <code>n</code>.
     */
    public IntQueue[] letterPositions(IntQueue[] pdl, int J,
                                      int K) {
        IntQueue[] lap = new IntQueue[K];
        for (int k = 0; k < K; k++)
            lap[k] = new IntQueue();
        for (int c = 0; c < J; c++)
            for (IntList ll = pdl[c].front; ll != null; ll = ll.next) {
                int k = ll.val; // at position <code>k</code> there is a c
                lap[k].add(c);
            }
        if (verbose) {
            System.out.print("Letters appearing at column");
            for (int k = 0; k < K; k++)
                System.out.print(lap[k].show("\n" + k + " : "));
            System.out.println();
        }
        return lap;
    }

    /**
     * Adds the state <code>nn</code> to the automaton <code>b</code>
     * (the minimal automaton in construction) as the class of state <code>p</code>
     * in the original automaton <code>a</code>.
     *
     * @param p   a state of <code>a</code>.
     * @param nn  a state of <code>b</code>
     * @param num the array of class numbers
     * @return <code>nn + 1</code>.
     */
    public int addStateMin(int p, int nn, int[] num, IDFA a, IDFA b) {
        num[p] = nn;
        if (a.isTerminal(p)) b.addTerminal(nn);
        for (PairIntList ll = a.edges[p].front; ll != null; ll = ll.next)
            b.edges[nn].add(ll.val, num[ll.elem]);
        b.nbStates = ++nn;
        return nn;
    }

    /**
     * Adds states to the minimal automaton in construction <code>b</code>.
     *
     * @param e   the array of states of <code>a</code>
     * @param sil the array of signatures
     * @param nn  the current number of states of <code>b</code>
     * @param num the array of class numbers
     * @return the current number of states of <code>b</code>.
     */
    public int renumberStates(int[] e, int[][] sil, int nn,
                              int[] num, IDFA a, IDFA b) {
        int p = e[0], q = -1;
        nn = addStateMin(p, nn, num, a, b);
        for (int k = 1; k < e.length; k++) {
            q = e[k];
            if (!equalsig(sil[k], sil[k - 1])) {
                nn = addStateMin(q, nn, num, a, b);
                p = q;   //save previous value
            } else {
                num[q] = num[p];
                p = q;
            }
        }
        return nn;
    }

    /**
     * Computes the array of signatures of a list <code>shl</code> of states.
     *
     * @param som the array of names of states. <code>som[i]</code> is the name of the
     *            i-th state.
     * @param sig the array of signatures.
     * @param num the array of class numbers
     * @param shl the list of states
     */
    public void computeSignatures(int[] som, int[][] sig, int[] num,
                                  IntQueue shl, IDFA a) {
        int i = 0;
        for (IntList l = shl.front; l != null; l = l.next) {
            int p = l.val; // p is the name of the state
            som[i] = p;
            int[] c = sig[i];
            if (a.isTerminal(p))
                c[0] = 1;
            int j = 1;
            for (PairIntList ll = a.edges[p].front; ll != null; ll = ll.next) {
                c[j++] = ll.val; // the letter
                c[j++] = num[ll.elem]; // the name here
            }
            if (verbose) {
                System.out.print("signature of " + som[i] + ":   ");
                for (int uu = 0; uu < sig[0].length; uu++)
                    System.out.print(sig[i][uu] + " ");
                System.out.println();
            }
            i++;
        }
    }

    /**
     * Returns true if the arrays <code>a</code> and <code>b</code> are equal.
     */
    public boolean equalsig(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) if (a[i] != b[i]) return false;
        return true;
    }


    /**
     * Returns the array <code>sh</code> of queues such that <code>sh[r]</code>
     * is the queue of states at heigth <code>r</code>.
     *
     * @param h the array of heigths
     * @return the array of lists of states at given heigth.
     **/
    public IntQueue[] triParHauteur(int[] h, IDFA a) {
        IntQueue[] sh = new IntQueue[1 + h[a.initial]];
        for (int p = 0; p < sh.length; p++)
            sh[p] = new IntQueue();
        for (int p = 0; p < a.nbStates; p++)
            sh[h[p]].add(p);
        return sh;
    }

    /**
     * The method called to minimize an acyclic trim automaton.
     */
    public IDFA minimize(IDFA a) throws Exception {
        a.orderEdges();
        int mm = Math.max(a.alphabet.size, a.nbStates);
        //System.out.println("okmm");
        g2l = new int[mm];//System.out.println("okg2l");
        l2g = new int[mm];//System.out.println("okl2g");
        for (int i = 0; i < mm; i++)
            g2l[i] = l2g[i] = -1; // undefined
        // num[p] contains the name of p in the minimal automaton
        // nn is the current number of states of the minimal automaton
        int nn = 0;
        int[] num = new int[a.nbStates];
        for (int p = 0; p < a.nbStates; p++) num[p] = -1;
        // heigth[p] is the heigth of p
        // wid[p] is the number of edges going out of p
        // pi [r] is the list of states at heigth r
        /* 1. Computation of heigths and partition by heigths */
        int[] heigth = a.heigths();//System.out.println("okheigths");
        //a.show("heigths",heigth);
        IntQueue[] pi = new IntQueue[1 + heigth[a.initial]];
        for (int r = 0; r < pi.length; r++)
            pi[r] = new IntQueue();
        for (int p = 0; p < a.nbStates; p++) {
            int h = heigth[p];
            pi[h].add(p);
        }
        if (verbose)
            a.show("Table of states by heigths", pi);
        ////////////////////
        /* 2. Computation of widths */
        int[] wid = a.width();//System.out.println("okwidths");
        if (verbose)
            a.show("Widths of states", wid);
        //used later
        PartitionS lambda;
        lambda = new PartitionS(a.alphabet.size + 1);
        //the width w is such that 0 <= w <= alphabet size
        /* 3. Preparation for the minimal automaton */
        IDFA b = new IDFA(a.nbStates, a.alphabet);
        /* 4  Fusion of states at heigth 0 */
        for (IntList l = pi[0].front; l != null; l = l.next) {
            num[l.val] = 0;
        }
        b.addTerminal(0);
        nn = 1;
        /*  5 Minimization of states at heigth r > 0 */
        for (int r = 1; r < pi.length; r++) {
            if (verbose)
                System.out.println("=========== Heigth " + r + " ===============");
            // 5. a Partition of states at heigth r by widths
            lambda.removeAll(); // erase previous partition
            lambda.parLargeur(pi[r], wid, verbose); // compute
            // 5. b expore the partition
            for (IntList ld = lambda.dans.front; ld != null; ld = ld.next) {
                // for each w such that there are states of width w
                int w = ld.val; // w = width
                // states with heigth $r$ and width $w$
                IntQueue states = lambda.classe[w];
                if (verbose) {
                    System.out.print(states.show("Treatment of states of width "
                            + w + " :  "));
                    System.out.println();
                }
                // if there is 0 or 1 state, easy
                if (states.size() == 0) // contradiction
                    throw new IllegalArgumentException("Nul");
                // 5. c Singleton classes are treated apart
                if (states.size() == 1) {
                    // a new state of the minimal auomaton
                    int p = states.front.val;
                    nn = addStateMin(p, nn, num, a, b);
                } else {
                    // m = number of states at heigth  r with width w
                    int m = states.size();
                    // Prepare for sorting
                    int[] som = new int[m]; // som[i] = name of i-th state of s
                    // Calcul des signatures
                    int[][] signature = new int[m][1 + 2 * w]; // table of signatures
                    // 5. d Table of signatures
                    computeSignatures(som, signature, num, states, a);
                    // Sorting
                    // 5. e Renumber the "letters" in signatures
                    int[][] sil = new int[m][1 + 2 * w];
                    int J = g2l(signature, sil);
                    // ss is the sorted sequence of indices of states of s
                    // 5. f Sorting the signatures
                    IntQueue ss = radixSort(sil, J);
                    resetg2l(J);
                    // 5. g Transposition to the actual names of states
                    int[] e = new int[m];
                    int k = 0;
                    for (IntList l = ss.front; l != null; l = l.next) {
                        int i = l.val;
                        e[k] = som[i];
                        sil[k] = signature[i];
                        k++;
                    }
                    if (verbose)
                        a.show("Table of states to be compared ", e);
                    // 6. Back to the equivalence classes.
                    nn = renumberStates(e, sil, nn, num, a, b);
                }
            }

            if (verbose) {
                System.out.println();
                a.show("Table of class numbers", num);
                System.out.println("-------------");
            }
        }
        b.initial = num[a.initial];
        if (verbose) a.show("Minimal Automaton");
        return b;
    } //----- end of minimization ----------

    public DFA minimize(DFA a) throws Exception {
        throw new Exception("Illegal minimization method");
    }

    public DFT minimize(DFT a) throws Exception {
        throw new Exception("Illegal minimization method");
    }
}
