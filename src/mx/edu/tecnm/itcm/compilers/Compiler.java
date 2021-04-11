package mx.edu.tecnm.itcm.compilers;

import mx.edu.tecnm.itcm.automatons.NFA;

/**
 * The interface to classes that compile a rational expression into an automaton (of class NFA)
 */
public interface Compiler {

    /**
     * The function that returns an automaton recognizing the language described by the
     * input expression.
     */
    NFA toNFA(String expression);
}
