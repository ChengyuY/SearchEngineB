package com.backend.db.algo;

public class RegEx {

    public static boolean match(String text,String regEx){
        RegExTree ret = null;
        try {
            ret = RegExTree.parse(regEx);
        } catch (Exception var8) {
            return false;
        }
        NDFAutomaton ndfa = NDFAutomaton.step2_AhoUllman(ret);
        DFAutomaton dfa = DFAutomaton.transforme(ndfa);
        Minimisation.minimisation(dfa);
        Moteur m = new Moteur(dfa);
        return m.matchOneLine(text);
    }
}
