package mx.edu.tecnm.itcm.automatons;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.automatons.minimizers.*;
import mx.edu.tecnm.itcm.utils.tries.VariableArrayTrie;

/**
 * This class implements a command computing the minimal automaton of a set of words
 * given in a text file using one of several possible minimization algorithms.
 * The minimization algorithms are contained in the classes implementing the
 * {@link Minimizer Minimizer} interface.
 * Usage: MinimizedAutomaton method file  (option
 * The method can be <UL>
 * <li> N (naive method using {@link NMinimizer NMinimizer})
 * <li> Nbis (a variant of the previous using {@link NBisMinimizer NbisMinimizer})
 * <li> H (Hopcroft's algorithm using {@link HopcroftMinimizer HopcroftMinimizer})
 * <li> R (Revuz algorithm using {@link RMinimizer RMinimizer} to be used only with acyclic
 * automata represented as {@link IDFA}.
 * </UL>
 * The option can be v (verbose mode).
 */
public class MinimizedAutomaton {

    public MinimizedAutomaton() {

    }

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        String option = args[0];
        if (option.equals("I")) {   //incremental
            Alphabet alph = Alphabet.fromFile(args[1]);
            System.out.println("alphabet size = " + alph.size);
            if (args.length == 3) RMinimizer.verbose = true;
            IDFA a = IDFA.fromFile(args[1]);
            System.out.println("size of minimal IDFA = " + a.nbStates);
        }
        if (option.equals("N")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            System.out.println("size of Trie = " + t.size());
            Alphabet alph = Alphabet.fromFile(args[1]);
            System.out.println("alphabet size = " + alph.size);
            Minimizer m = new NMinimizer();
            if (args.length == 3) NMinimizer.verbose = true;
            IDFA a = t.toIDFAbis(alph);
            System.out.println("size of IDFA = " + a.nbStates);
            IDFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        if (option.equals("NBis")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            Alphabet alph = Alphabet.fromFile(args[1]);
            Minimizer m = new NBisMinimizer();
            if (args.length == 3) NBisMinimizer.verbose = true;
            IDFA a = t.toIDFAbis(alph);
            System.out.println("size of Trie = " + t.size());
            System.out.println("size of IDFA = " + a.nbStates);
            IDFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        if (option.equals("H")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            System.out.println("size of Trie = " + t.size());
            Alphabet alph = Alphabet.fromFile(args[1]);
            System.out.println("alphabet size = " + alph.size);
            Minimizer m = new HopcroftMinimizer();
            if (args.length == 3) HopcroftMinimizer.verbose = true;
            DFA a = t.toDFAbis(alph);
            System.out.println("size of DFA = " + a.nbStates);
            DFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        if (option.equals("R")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            System.out.println("size of Trie = " + t.size());
            Alphabet alph = Alphabet.fromFile(args[1]);
            System.out.println("alphabet size = " + alph.size);
            Minimizer m = new RMinimizer();
            if (args.length == 3) RMinimizer.verbose = true;
            IDFA a = t.toIDFAbis(alph);
            System.out.println("size of IDFA = " + a.nbStates);
            IDFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        if (option.equals("B")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            System.out.println("size of Trie = " + t.size());
            Alphabet alph = Alphabet.fromFile(args[1]);
            System.out.println("alphabet size = " + alph.size);
            Minimizer m = new BMinimizer();
            if (args.length == 3) RMinimizer.verbose = true;
            IDFA a = t.toIDFAbis(alph);
            System.out.println("size of IDFA = " + a.nbStates);
            IDFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        if (option.equals("F")) {
            VariableArrayTrie t = new VariableArrayTrie('$');
            t.fromFileBis(args[1]);
            Alphabet alph = Alphabet.fromFile(args[1]);
            Minimizer m = new FMinimizer();
            if (args.length == 3) RMinimizer.verbose = true;
            IDFA a = t.toIDFAbis(alph);
            System.out.println("size of Trie = " + t.size());
            System.out.println("size of IDFA = " + a.nbStates);
            IDFA b = a.minimize(m);
            System.out.println("size of minimal IDFA = " + b.nbStates);
        }
        System.out.println("Time = " + (System.currentTimeMillis() - time) + " ms");
    }
}
