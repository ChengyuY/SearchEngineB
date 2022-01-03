package com.backend.db.algo;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Moteur {
    private DFAutomaton dfa;
    private EtatState startS;

    public Moteur(DFAutomaton dfa) {
        this.dfa = dfa;
        ArrayList<EtatState> listES = this.dfa.getTransitionTable();
        for(int i = 0;i<listES.size();i++){
            EtatState e = listES.get(i);
            if(e.getNumstate() == 0){
                this.startS = e;
            }
        }
    }

    public DFAutomaton getDfa() {
        return dfa;
    }

    public EtatState getStartS() {
        return startS;
    }

    public static int getSousState(ArrayList<EtatState> es, int s){
        for(int i=0;i< es.size();i++){
            if(es.get(i).getNumstate() == s){
                return i;
            }
        }
        return -1;
    }

    public boolean matchOneLineAtIndex(String line,Integer index){
        if(this.startS.getSfinal()){
            return true;
        }
        int position = index;
        EtatState currentState = this.startS;
        while(position < line.length()){
            Integer statesuivant = currentState.getSousstate().get(Character.valueOf(line.charAt(position)));
            if(statesuivant != null){
                int ids = getSousState(dfa.getTransitionTable(), statesuivant);
                if(ids != -1) {
                    currentState = dfa.getTransitionTable().get(getSousState(dfa.getTransitionTable(), statesuivant));
                }else {
                    return false;
                }
                if (currentState.getSfinal()) {
                    return true;
                }
            }else{
                return false;
            }
            position ++;
        }
        return false;
    }

    public boolean matchOneLine(String line){
        int index = 0;
        while(index < line.length()){
            Boolean trouve = matchOneLineAtIndex(line,index);
            if(trouve){
                return true;
            }else{
                index++;
            }
        }
        return false;
    }

    public ArrayList<Integer> matchText(String path){
        ArrayList<Integer> egrepCloneLinesNumbers = new ArrayList<Integer>();
        int lineCount = 1;
        try {
            System.out.println(path);
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            System.out.println("file open");
            while(myReader.hasNextLine()){
                String l = myReader.nextLine();
                if(matchOneLine(l)){
                    System.out.println(l);
                    egrepCloneLinesNumbers.add(lineCount);
                }
                //System.out.println("line "+lineCount);
                lineCount++;
            }
        } catch (Exception e){
            System.out.println("Error IO");
            e.printStackTrace();
        }
        return egrepCloneLinesNumbers;

    }


}
