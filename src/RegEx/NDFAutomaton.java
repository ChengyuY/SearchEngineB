package RegEx;

import java.util.ArrayList;

public class NDFAutomaton {
    protected int[][] transitionTable; //ASCII transition
    protected ArrayList<Integer>[] epsilonTransitionTable; //epsilon transition list

    public NDFAutomaton(int[][] transitionTable, ArrayList<Integer>[] epsilonTransitionTable) {
        this.transitionTable=transitionTable;
        this.epsilonTransitionTable=epsilonTransitionTable;
    }
    //PRINT THE AUTOMATON TRANSITION TABLE
    public String toString() {
        String result="Initial state: 0\nFinal state: "+(transitionTable.length-1)+"\nTransition list:\n";
        for (int i=0;i<epsilonTransitionTable.length;i++) for (int state: epsilonTransitionTable[i])
            result+="  "+i+" -- epsilon --> "+state+"\n";
        for (int i=0;i<transitionTable.length;i++) for (int col=0;col<256;col++)
            if (transitionTable[i][col]!=-1) result+="  "+i+" -- "+(char)col+" --> "+transitionTable[i][col]+"\n";
        return result;
    }

    public static NDFAutomaton step2_AhoUllman(RegExTree ret) {

        if (ret.subTrees.isEmpty()) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            int[][] tTab = new int[2][256];
            ArrayList<Integer>[] eTab = new ArrayList[2];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            if (ret.root!=Constante.DOT) tTab[0][ret.root]=1; //transition ret.root from initial state "0" to final state "1"
            else for (int i=0;i<256;i++) tTab[0][i]=1; //transition DOT from initial state "0" to final state "1"

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==Constante.CONCAT) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton gauche = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = step2_AhoUllman(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int lg=tTab_g.length;
            int ld=tTab_d.length;
            int[][] tTab = new int[lg+ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[lg+ld];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[lg-1].add(lg); //epsilon transition from old final state "left" to old initial state "right"

            for (int i=0;i<lg;i++) for (int col=0;col<256;col++) tTab[i][col]=tTab_g[i][col]; //copy old transitions
            for (int i=0;i<lg;i++) eTab[i].addAll(eTab_g[i]); //copy old transitions
            for (int i=lg;i<lg+ld-1;i++) for (int col=0;col<256;col++) if (tTab_d[i-lg][col]!=-1) tTab[i][col]=tTab_d[i-lg][col]+lg; //copy old transitions
            for (int i=lg;i<lg+ld-1;i++) for (int s: eTab_d[i-lg]) eTab[i].add(s+lg); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==Constante.ALTERN) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton gauche = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = step2_AhoUllman(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int lg=tTab_g.length;
            int ld=tTab_d.length;
            int[][] tTab = new int[2+lg+ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[2+lg+ld];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(1+lg); //epsilon transition from new initial state to old initial state
            eTab[1+lg-1].add(2+lg+ld-1); //epsilon transition from old final state to new final state
            eTab[1+lg+ld-1].add(2+lg+ld-1); //epsilon transition from old final state to new final state

            for (int i=1;i<1+lg;i++) for (int col=0;col<256;col++) if (tTab_g[i-1][col]!=-1) tTab[i][col]=tTab_g[i-1][col]+1; //copy old transitions
            for (int i=1;i<1+lg;i++) for (int s: eTab_g[i-1]) eTab[i].add(s+1); //copy old transitions
            for (int i=1+lg;i<1+lg+ld-1;i++) for (int col=0;col<256;col++) if (tTab_d[i-1-lg][col]!=-1) tTab[i][col]=tTab_d[i-1-lg][col]+1+lg; //copy old transitions
            for (int i=1+lg;i<1+lg+ld;i++) for (int s: eTab_d[i-1-lg]) eTab[i].add(s+1+lg); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==Constante.ETOILE) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton fils = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_fils = fils.transitionTable;
            ArrayList<Integer>[] eTab_fils = fils.epsilonTransitionTable;
            int l=tTab_fils.length;
            int[][] tTab = new int[2+l][256];
            ArrayList<Integer>[] eTab = new ArrayList[2+l];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(2+l-1); //epsilon transition from new initial state to new final state
            eTab[2+l-2].add(2+l-1); //epsilon transition from old final state to new final state
            eTab[2+l-2].add(1); //epsilon transition from old final state to old initial state

            for (int i=1;i<2+l-1;i++) for (int col=0;col<256;col++) if (tTab_fils[i-1][col]!=-1) tTab[i][col]=tTab_fils[i-1][col]+1; //copy old transitions
            for (int i=1;i<2+l-1;i++) for (int s: eTab_fils[i-1]) eTab[i].add(s+1); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        return null;
    }
}

