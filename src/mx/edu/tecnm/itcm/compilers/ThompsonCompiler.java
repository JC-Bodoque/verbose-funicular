package mx.edu.tecnm.itcm.compilers;

import mx.edu.tecnm.itcm.Alphabet;
import mx.edu.tecnm.itcm.automatons.LinkedNFA;
import mx.edu.tecnm.itcm.automatons.NFA;

/**
 * Compiles a rational expression into an automaton of the
 * class {@link NFA NFA}
 * by Thompson's algorithm. Uses a top-down analysis for the grammar<br>
 * <code> E -> E+T | T </code>,<br>
 * <code> T -> TF | F </code>,<br>
 * <code> F -> G | G* </code>, <br>
 * <code> G -> (E) | char </code>.<br>
 */
public class ThompsonCompiler implements Compiler {
    static int index = 0;
    static String expression;

    /**
     * Implements the function <code>Current()</code> of Section 1.6.1.
     * Returns the current character
     * of the input expression.
     */
    public char current() {
        while (expression.charAt(index) == ' ')
            advance();
        return expression.charAt(index);
    }

    /**
     * Implements the function <code>Advance()</code> of Section 1.6.1.
     * Advances to the next character in the
     * input expression.
     */
    public void advance() {
        index++;
    }

    /**
     * Implements  the function <code>EvalExp()</code> of Section 1.6.1.
     * Returns a LinkedNFA recognizing the
     * expression.
     */
    public LinkedNFA evalExp() {
        LinkedNFA a = evalTerm();
        while (current() == '+') {
            advance();
            a = LinkedNFA.automataUnion(a, evalTerm());
        }
        return a;
    }

    /**
     * Implements  the function <code>EvalTerm()</code> of Section 1.6.1.
     */
    public LinkedNFA evalTerm() {
        LinkedNFA a = evalFact();
        while (current() == '(' || Character.isLetter(current()))
            a = LinkedNFA.automataProduct(a, evalFact());
        return a;
    }

    /**
     * Implements the function <code>EvalFact()</code> of Section 1.6.1.
     */
    public LinkedNFA evalFact() {
        LinkedNFA a;
        if (current() == '(') {
            advance();
            a = evalExp();
        } else {
            a = new LinkedNFA(current());
        }
        advance();
        if (current() == '*') {
            advance();
            a = LinkedNFA.automatonStar(a);
        }
        return a;
    }

    public LinkedNFA toLinkedNFA() {
        return evalExp();
    }

    /**
     * Compiles the expression into a nondeterministic finite automaton.
     * Internally, the function computes an automaton of the class
     * {@link LinkedNFA LinkedNFA} and then converts it
     * into an {@link  NFA NFA}.
     */
    public NFA toNFA(String s) {
        expression = s + '$';
        Alphabet alph = Alphabet.fromExpression(expression);
        System.out.println("alphabet:" + alph);
        LinkedNFA la = toLinkedNFA();
        return la.toNFA(alph);
    }


}
