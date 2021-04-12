package mx.edu.tecnm.itcm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class implements context-free grammars.
 * The productions of the grammar are stored
 * in an array <code>productionsArray</code>. Each production is an
 * object of the class {@link Production Production}. There are separated
 * alphabets <code>terminals</code> for terminals and <code>variables</code>
 * for variables plus an alphabet <code>alphabet</code> for their union.
 * The usual functions <code>First()</code> and  <code>Follow()</code>
 * are implemented as methods of this class. The grammar can be read
 * from a file where the productions are listed one by line in the form
 * <code>l:r</code> with a ":" separating the two sides of each production.
 * The initial rule should be the last one. The following grammars are included
 * in the directory <code>Data</code>: <br>
 * <code>ETF.txt</code> the ordinary ETF grammar,
 * which is SLR but not ParserLL(1).<br>
 * <code>ETFPrime.txt</code> the variant of the ETF grammar which is ParserLL(1)
 * obtained by eliminating left recursion.<br>
 * <code>Dyck.txt</code> the grammar of the Dyck language, which is ParserLL(1).<br>
 *
 * @author Henry McElwaine
 */
public class Grammar {

    /**
     * The array of grammar productions.
     */
    public Production[] productionsArray;

    /**
     * The number of productions.
     */
    public int nbProductions;

    /**
     * The sum of lengths of productions .
     */
    public int lgProductions;

    /**
     * The erasable symbols.
     */
    public boolean[] epsilon;

    /**
     * The set of rules with given left symbol.
     */
    public Set[] derive;

    /**
     * The variables. The set of variables is supposed to coincide with the
     * set of left sides of productions.
     */
    public Alphabet variables;

    /**
     * The terminals. The terminal characters represent tokens of the
     * lexical analysis.
     */
    public Alphabet terminals;

    /**
     * The total alphabet. It is the union of the terminals
     * and variable sets.
     */
    public Alphabet alphabet;

    /**
     * The initial production. Usually of the form <code>I -> E$</code>.
     */
    public int initial;

    /**
     * Creates a grammar with <code>n</code> symbols.
     */
    public Grammar(int n) {
        productionsArray = new Production[n];
        nbProductions = n;
    }

    /**
     *
     */
    public static Grammar fromFile(String name) throws IOException {
        FileReader fileIn = new FileReader(name);
        BufferedReader r = new BufferedReader(fileIn);
        String line;
        int n = 0;
        // count the number of lines//
        while ((line = r.readLine()) != null) {
            n++;
        }
        fileIn.close();
        Grammar G = new Grammar(n);
        fileIn = new FileReader(name);
        r = new BufferedReader(fileIn);
        for (int i = 0; i < n; i++) {
            line = r.readLine();
            String[] rule = line.split(":", 2);
            G.productionsArray[i] = new Production(rule[0].charAt(0), rule[1]);
        }
        fileIn.close();
        G.initial = n - 1;
        G.initGrammar();
        return G;
    }

    /**
     * Implements <code>Epsilon()</code>. Initializes the boolean
     * array epsilon of erasable symbols.
     */
    public void epsilon() {
        int k;
        for (int j = 0; j < nbProductions; j++) {
            Production p = productionsArray[j];
            short[] r = alphabet.toShort(p.right);
            short l = alphabet.toShort(p.left);
            for (k = 0; k < r.length; k++)
                if (!epsilon[r[k]])
                    break;
            if (k == r.length)
                epsilon[l] = true;
        }
    }

    /**
     * Implements the method <code>FirstChild()</code>.
     *
     * @param v a letter (variable or terminal)
     * @return the successors of <code>v</code> in the graph First
     * (terminals and variables).
     */
    public Set firstChild(short v) {
        Set s = new HashSet();
        for (int i = 0; i < productionsArray.length; i++) {  //create the graph
            Production p = productionsArray[i];
            if (alphabet.toShort(p.left) == v) {
                short[] r = alphabet.toShort(p.right);
                if (r.length == 0) break;
                short d;
                int j = 0;
                do {
                    d = r[j];
                    s.add(d);
                    j++;
                } while (epsilon[d] && j < r.length);
            }
        }
        return s;
    }

    /**
     * Explores the graph of First.
     *
     * @param v    a letter (variable or terminal)
     * @param mark the array of marks used for the exploration.
     */
    public void exploreFirstChild(short v, boolean[] mark) {
        mark[v] = true;
        Set s = firstChild(v);
        for (Iterator i = s.iterator(); i.hasNext(); ) {
            short w = ((Short) i.next()).shortValue();
            if (!mark[w]) exploreFirstChild(w, mark);
        }
    }

    /**
     * Implements the function <code>First()</code>. Computes the closure of
     * <code>firstChild()</code>.
     *
     * @param v a letter (variable or terminal)
     * @return the set <code>First(v)</code> of terminal symbols.
     */
    Set first(short v) {
        Set s = new HashSet();
        boolean[] mark = new boolean[alphabet.size];
        exploreFirstChild(v, mark);
        for (short i = 0; i < terminals.size; i++) {
            short c = terminals.convert(alphabet, i);
            if (mark[c])
                s.add(i);
        }
        return s;
    }

    /**
     * Extends the function <code>First()</code> to Strings.
     */
    public Set first(short[] s) {
        Set f = new HashSet();
        for (int i = 0; i < s.length; i++) {
            short c = s[i];
            f.addAll(first(c));
            if (!epsilon[c]) break;
        }
        return f;
    }

    /**
     * Implements the function <code>Sibling()</code>.
     *
     * @param v a variable
     * @return the set <code>Sibling(v) of successors of the
     * variable <code>v</code> in the graph of <code>Follow()</code>.
     */
    public Set sibling(short v) {
        Set s = new HashSet();
        for (int i = 0; i < nbProductions; i++) {
            Production p = productionsArray[i];
            short c = alphabet.toShort(p.left);
            short[] r = alphabet.toShort(p.right);
            int n = r.length;
            for (int j = 0; j < n - 1; j++) {
                if (r[j] == variables.convert(alphabet, v)) {
                    short e;
                    int k = 1;
                    do {
                        e = r[j + k];
                        s.addAll(first(e));
                        k++;
                    } while (epsilon[e] && j + k < r.length);
                }
                int l = n;
                do {
                    if (l > 0 && r[l - 1] == variables.convert(alphabet, v))
                        s.add(c);
                    l--;
                } while (l > 0 && epsilon[r[l]]);
            }
        }
        return s;
    }

    /**
     * Explores the graph of <code>Follow()</code>.
     *
     * @param v a variable
     */
    public void exploreSibling(short v, boolean[] mark) {
        Set s = sibling(v);
        for (Iterator i = s.iterator(); i.hasNext(); ) {
            short w = ((Short) i.next()).shortValue();
            if (!mark[w]) {
                mark[w] = true;
                if (variables.isIn(alphabet.toChar(w)))
                    exploreSibling(alphabet.convert(variables, w), mark);
            }
        }
    }

    /**
     * Implements the function <code>Follow()</code>.
     *
     * @param v a variable
     * @return the set of terminals in the set <code>Follow(v)</code>
     */
    public Set follow(short v) {
        Set s = new HashSet();
        boolean mark[] = new boolean[alphabet.size];
        mark[variables.convert(alphabet, v)] = true;
        exploreSibling(v, mark);
        for (short i = 0; i < terminals.size; i++) {
            short c = terminals.convert(alphabet, i);
            if (mark[c])
                s.add(i);
        }
        return s;
    }

    /**
     * The Dyck grammar:<br>
     * <code>S -> (S)S | ""</code>.
     */
    public static Grammar Dyck() {
        Grammar G = new Grammar(3);
        //the productions
        G.productionsArray[0] = new Production('S', "(S)S");
        G.productionsArray[1] = new Production('S', "");
        G.productionsArray[2] = new Production('I', "S$");
        G.initial = 2;
        G.initGrammar();
        return G;
    }

    /**
     * The <code>ETF</code> grammar :<br>
     * <code> E -> E + T</code><br>
     * <code> T -> T * F</code><br>
     * <code> F -> (E) | char </code>.
     */
    public static Grammar ETF() {
        Grammar G = new Grammar(7);
        //the productions
        G.productionsArray[0] = new Production('E', "E+T");
        G.productionsArray[1] = new Production('E', "T");
        G.productionsArray[2] = new Production('T', "T*F");
        G.productionsArray[3] = new Production('T', "F");
        G.productionsArray[4] = new Production('F', "(E)");
        G.productionsArray[5] = new Production('F', "c");
        G.productionsArray[6] = new Production('I', "E$");
        G.initial = 6;
        G.initGrammar();
        return G;
    }

    /**
     * The modified version of the <code>ETF</code> grammar which
     * is ParserLL(1) :<br>
     * <code> E -> Te</code><br>
     * <code> e -> +Te | ""</code><br>
     * <code> T -> Ft</code><br>
     * <code> t -> *Ft | ""</code><br>
     * <code> F -> (E) | char </code>.
     */
    public static Grammar ETFPrime() {
        Grammar G = new Grammar(9);
        G.productionsArray[0] = new Production('E', "Te");
        G.productionsArray[1] = new Production('e', "+Te");
        G.productionsArray[2] = new Production('e', "");
        G.productionsArray[3] = new Production('T', "Ft");
        G.productionsArray[4] = new Production('t', "*Ft");
        G.productionsArray[5] = new Production('t', "");
        G.productionsArray[6] = new Production('F', "(E)");
        G.productionsArray[7] = new Production('F', "c");
        G.productionsArray[8] = new Production('I', "E$");
        G.initGrammar();
        G.initial = 8;
        return G;
    }


    /**
     * Initializes  alphabet,
     * variables and terminals.
     */
    public void initAlphabet() {
        int nbVariables = 0, nbTerminals = 0, nbLetters = 0;
        boolean[] isVariable = new boolean[256];
        boolean[] isTerminal = new boolean[256];
        boolean[] isLetter = new boolean[256];
        for (int i = 0; i < productionsArray.length; i++) {
            Production p = productionsArray[i];
            char x = p.left;
            String r = p.right;
            if (!isVariable[x]) nbVariables++;
            isVariable[x] = true;
            isLetter[x] = true;
            for (int j = 0; j < r.length(); j++) {
                isLetter[r.charAt(j)] = true;
            }
        }
        for (int i = 0; i < 256; i++)
            if (isLetter[i] && !isVariable[i]) {
                isTerminal[i] = true;
                nbTerminals++;
            }
        nbLetters = nbTerminals + nbVariables;
        char[] alph = new char[nbLetters];
        char[] term = new char[nbTerminals];
        char[] var = new char[nbVariables];
        int nl = 0, nv = 0, nt = 0;
        for (char i = 0; i < 256; i++) {
            if (isLetter[i]) alph[nl++] = i;
            if (isTerminal[i]) term[nt++] = i;
            if (isVariable[i]) var[nv++] = i;
        }
        alphabet = new Alphabet(alph);
        terminals = new Alphabet(term);
        variables = new Alphabet(var);
    }

    /**
     * Computes derive, alphabet and epsilon.
     */
    public void initGrammar() {
        initAlphabet();
        epsilon = new boolean[alphabet.size()];
        epsilon();
        derive = new HashSet[alphabet.size()];
        for (int i = 0; i < alphabet.size(); i++)
            derive[i] = new HashSet();
        for (int i = 0; i < nbProductions; i++) {
            Production p = productionsArray[i];
            derive[alphabet.toShort(p.left)].add(i);
            lgProductions += p.right.length();
        }
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < productionsArray.length; i++)
            s += productionsArray[i].toString() + "\n";
        s += variables.size() + " variables =" + variables + "\n";
        s += terminals.size() + " terminals =" + terminals + "\n";
        s += alphabet.size() + " alphabet =" + alphabet + "\n";
        for (short i = 0; i < variables.size; i++) {
            s += "Epsilon(" + variables.toChar(i) + ")="
                    + epsilon[variables.convert(alphabet, i)] + "\n";
        }
        for (short i = 0; i < variables.size; i++) {
            s += "First(" + variables.toChar(i) + ")=";
            s += terminals.toChar(first(variables.convert(alphabet, i))) + "\n";
        }
        for (short i = 0; i < variables.size; i++) {
            s += "Follow(" + variables.toChar(i) + ")=";
            s += terminals.toChar(follow(i)) + "\n";
        }
        return s;
    }

    public static void main(String[] args) throws IOException {
        Grammar grammar = fromFile(args[0]);
        System.out.println(grammar);
    }
}
