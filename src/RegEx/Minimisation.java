package RegEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Minimisation {

	
    //Exemple a|bc*-> 
    //EtatState{numstate=0, sfinal=false, sousstate={a=2, b=4}}
    //EtatState{numstate=6, sfinal=true, sousstate={c=6}}
    //EtatState{numstate=2, sfinal=true, sousstate={}}
    //EtatState{numstate=4, sfinal=true, sousstate={c=6}}
	
	//après minimisation
    //EtatState{numstate=0, sfinal=false, sousstate={a=2, b=6}}
    //EtatState{numstate=6, sfinal=true, sousstate={c=6}}
    //EtatState{numstate=2, sfinal=true, sousstate={}}

	
    public static void minimisation(DFAutomaton dfa) {
        ArrayList<EtatState> listE = dfa.getTransitionTable();
        ArrayList<EtatState> reslist = new ArrayList<>();
        //chercher les state qui ont des sous states identiques
        for (int i = 0; i < listE.size() - 1; i++) {
            EtatState e1 = listE.get(i);
            for (int j = i+1; j < listE.size(); j++) {
                EtatState e2 = listE.get(j);
                //à la fois on trouve state1.sousstate = state2.soustate,  on va delete state2,  et parcours ensembles d'etatState en remplacant state2 par state1 
                if ((e1.getSousstate().equals(e2.getSousstate())) && (e1.getSfinal() == e2.getSfinal())) {
                    int nume1 = e1.getNumstate();
                    int nume2 = e2.getNumstate();
                    System.out.println(nume1+" "+i+","+j+"remove: "+nume2);
                    listE.remove(j);
                    for (int k = 0; k < listE.size(); k++) {
                        //if(listE.get(k).getSousstate());
                        EtatState courant = listE.get(k);
                        HashMap<Character, Integer> ss = courant.getSousstate();
                        Iterator<Character> iterator = ss.keySet().iterator();
                        while (iterator.hasNext()) {
                            Character key = iterator.next();
                            if (ss.get(key) == nume2) {
                                ss.put(key, nume1);
                            }
                        }
                    }
                }
            }
        }
        dfa.setTransitionTable(listE);
    }

}
