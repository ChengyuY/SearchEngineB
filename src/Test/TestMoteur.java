package Test;

import RegEx.*;

import java.util.Scanner;

public class TestMoteur {
    public static void main(String[] arg) {
        String regEx;
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        if (arg.length!=0) {
            regEx = arg[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regEx = scanner.next();
        }
        System.out.println("  >> Parsing regEx \""+regEx+"\".");
        System.out.println("  >> ...");

        if (regEx.length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
            for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
            System.out.println("].");
            RegExTree ret=null;
            try {
                ret = RegExTree.parse(regEx);
                System.out.println("  >> Tree result: "+ret.toString()+".");
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
            }
            NDFAutomaton ndfa = NDFAutomaton.step2_AhoUllman(ret);
            System.out.println("  >> NDFA construction:\n\nBEGIN NDFA\n"+ndfa.toString()+"END NDFA.\n");

            DFAutomaton dfa = DFAutomaton.transforme(ndfa);
            System.out.println("DFA");
            for(int i=0; i<dfa.getTransitionTable().size();i++) {
                System.out.println(dfa.getTransitionTable().get(i).toString());
            }

            Minimisation.minimisation(dfa);
            System.out.println("minimisation");
            for(int i=0; i<dfa.getTransitionTable().size();i++) {
                System.out.println(dfa.getTransitionTable().get(i).toString());
            }

            Moteur m = new Moteur(dfa);
            String s = "under the Sargonids--The policies of encouragement and";
            boolean trouve = m.matchOneLine(s);
            System.out.println(s+"  :  "+trouve);

            System.out.println("match with"+regEx);
            m.matchText("C:\\Users\\11369\\Desktop\\M2\\DAAR\\DaarProjet1\\src\\56667-0.txt");


        }

    }
}


