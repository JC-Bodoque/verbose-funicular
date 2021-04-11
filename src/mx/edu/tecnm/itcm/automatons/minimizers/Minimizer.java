package mx.edu.tecnm.itcm.automatons.minimizers;

import mx.edu.tecnm.itcm.automatons.DFA;
import mx.edu.tecnm.itcm.automatons.DFT;
import mx.edu.tecnm.itcm.automatons.IDFA;

public interface Minimizer {
    DFA minimize(DFA a) throws Exception;

    DFT minimize(DFT a) throws Exception;

    IDFA minimize(IDFA a) throws Exception;
}
