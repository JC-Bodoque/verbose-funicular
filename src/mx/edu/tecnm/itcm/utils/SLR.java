package mx.edu.tecnm.itcm.utils;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.Grammar;
import mx.edu.tecnm.itcm.Production;
import mx.edu.tecnm.itcm.automatons.InfoDFA;
import mx.edu.tecnm.itcm.automatons.InfoNFA;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * This class implements the <code>SLR(0)</code> method for syntax analysis.
 * Usage: <br>
 * <code>java SLR "input" grammar</code>. <br>
 * For instance,<br>
 * <code>java SLR "(1+2)*3" ../Data/ETF.txt</code>
 * produces the successive stack contents during the analysis of the
 * expression (1+2)*3 using the ETF grammar. If the grammar is not
 * SLR, an error message "Grammar not SLR: S/R conflict" or
 * "Grammar not SLR: S/R conflict" is issued.
 * If the expression is not correct, an error message "syntax error"
 * or "end of input reached" is issued.
 */
public class SLR {
    /**
     * The expression to analyze.
     */

    public char[] expression;

    /**
     * The current index.
     */
    public int position;

    /**
     * The stack used to store the LR states.
     */
    public Stack stack;

    /**
     * The current context-free grammar.
     */
    public Grammar grammar;

    /**
     * The input alphabet;
     */
    public Alphabet alphabet;

    /**
     * The shift table.
     */
    public int[][] LRShiftTable;

    /**
     The reduce table.
     */
    int terminal;
    public int[][] LRReduceTable;

    /**
     * The accept state.
     */

    /**
     * Creates the fields necessary for the SLR analysis of expression
     * <code>e</code> using grammar <code>G</code>.
     */
    SLR(String exp, Grammar grammar) {
        this.grammar = grammar;
        alphabet = this.grammar.alphabet;
        try {
            InfoDFA a = LRAutomaton().toInfoDFA();
            LRShiftTable = LRShiftTable(a);
            LRReduceTable = LRReduceTable(a);
            terminal = a.terminal;
        } catch (Exception exception) {
            System.out.print(exception);
            System.exit(1);
        }
        expression = exp.toCharArray();
        stack = new Stack();
        stack.push(0);
    }

    /**
     * Advance one character to the right on input.
     */
    public void advance() {
        position++;
    }

    /**
     * Returns the current token of the input. Discards possible
     * space characters.
     */
    public short current() {
        while (expression[position] == ' ')
            advance();
        char c = expression[position];
        return (Character.isDigit(c)) ?
                alphabet.toShort('c') : alphabet.toShort(c);
    }

    /**
     * Push the character <code>c</code> on the stack. Actually push
     * the state reached in the LR automaton from the state <code>p</code> at top of
     * the stack under input <code>c</code>.
     *
     * @param c the input character
     */
    public void push(short c) {
        Integer p = (Integer) stack.peek();
        int q = LRShiftTable[p][c];
        stack.push(q);
    }

    /**
     * Implements the Reduce() function. Pops the right side of production <code>n</code>
     * and pushes the left side (actually the states of the LR automaton).
     *
     * @param n index of production to use
     */
    void reduce(int n) {
        Production[] P = grammar.productionsArray;
        short[] right = alphabet.toShort(P[n].right);
        int le = right.length;
        for (int i = 0; i < le; i++)
            stack.pop();
        short v = alphabet.toShort(P[n].left);
        push(v);
    }

    /**
     * Computes the LR(0) automaton.
     */
    public InfoNFA LRAutomaton() {
        InfoNFA a = new InfoNFA(grammar.lgProductions + grammar.nbProductions, grammar.alphabet);
        //name of state corresponding to the production
        int[] initState = new int[grammar.nbProductions];
        //initializes info to -1 (default)
        for (int i = 0; i < a.nbStates; i++)
            a.info[i] = -1;
        //create the paths along right sides
        for (int i = 0, p = 0; i < grammar.nbProductions; i++) {
            String s = grammar.productionsArray[i].right;
            initState[i] = p;
            int j;
            for (j = 0; j < s.length(); j++) {
                char[] c = {s.charAt(j)};
                a.next[p + j].add(new HalfEdge(new String(c), p + j + 1));
            }
            //the value of info indicates the rule to be used for reduction.
            a.info[p + j] = i;
            if (grammar.initial == i)
                a.terminal = p + j;
            p = p + j + 1;
        }
        //add the epsilon transitions
        for (int i = 0; i < grammar.nbProductions; i++) {
            short[] r = alphabet.toShort(grammar.productionsArray[i].right);
            int p = initState[i];
            for (int j = 0; j < r.length; j++) {
                Set s = grammar.derive[r[j]];
                for (Iterator k = s.iterator(); k.hasNext(); ) {
                    Integer I = (Integer) k.next();
                    a.next[p + j].add(new HalfEdge("", initState[I]));
                }
            }
        }
        a.initial.add(new HalfEdge(initState[grammar.initial]));
        return a;
    }

    /**
     * Computes the SLR(0) shift table. The positive values correspond
     * to the shift part, the negative ones to the reduce part.
     */
    public int[][] LRShiftTable(InfoDFA a) {
        int[][] table = new int[a.nbStates][a.nbLetters];
        for (int i = 0; i < a.nbStates; i++)
            for (char c = 0; c < a.nbLetters; c++) {
                int q = a.next[i][c];
                if (q == a.sink)
                    table[i][c] = -1;
                else
                    table[i][c] = q;
            }
        return table;
    }

    /**
     *
     */
    public int[][] LRReduceTable(InfoDFA a) throws Exception {
        int[][] table = new int[a.nbStates][a.nbLetters];
        for (int i = 0; i < a.nbStates; i++)
            for (short c = 0; c < a.nbLetters; c++) {
                int n = a.info[i];
                if (n >= 0) {   //  reduction by production n.
                    Production p = grammar.productionsArray[n];
                    short v = grammar.variables.toShort(p.left);
                    if (grammar.follow(v).contains(c)) {
                        if (LRShiftTable[i][c] != -1)
                            throw new Exception("Grammar not SLR: S/R conflict");
                        table[i][c] = n;
                    } else
                        table[i][c] = -1;
                } else
                    table[i][c] = -1;
            }
        return table;
    }

    void lRParse() throws Exception {
        System.out.println(stack);
        while (true) {
            short c = current();
            int p = (Integer) stack.peek();
            int q = LRShiftTable[p][c];
            int r = LRReduceTable[p][c];
            if (q != -1) {
                stack.push(q);
                advance();
                System.out.println(stack);
                if (c == alphabet.toShort('$'))
                    if (q == terminal) {
                        System.out.print("input accepted\n");
                        break;
                    } else
                        throw new Exception("end of input reached");
            } else if (r >= 0) {
                reduce(r);
                System.out.println(stack);
            } else
                throw new Exception("syntax error");
        }

    }

    static void printTable(String s, int[][] table) {
        System.out.println(s);
        for (int i = 0; i < table.length; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < table[i].length; j++)
                System.out.print(table[i][j] + " ");
            System.out.println();
        }
    }

    static void printTable(String s, int[] table) {
        System.out.println(s);
        for (int i = 0; i < table.length; i++)
            System.out.print(table[i] + " ");
        System.out.println();
    }
}
