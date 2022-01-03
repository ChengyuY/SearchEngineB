package Test;

import RegEx.DFAutomaton;
import RegEx.Minimisation;
import RegEx.NDFAutomaton;
import RegEx.RegExTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TestDFA {
    public static void main(String arg[]) {
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

//            HashMap<Integer, ArrayList<Integer>> epsilonclosure = DFAutomaton.epsilonclosure(ndfa);
//            for(int i = 0;i<epsilonclosure.size();i++) {
//                System.out.println("Set" + i + " : " + epsilonclosure.get(i));
//            }
//
//            ArrayList<Integer>[] testtable = DFAutomaton.abc_closure(epsilonclosure.get(0),ndfa);
//            //System.out.println("013 ->"+(int)'a'+ ":"+ testtable[(int)'a']);
//
//            for(int i = 0;i<epsilonclosure.size();i++) {
//                ArrayList<Integer>[] testtable1 = DFAutomaton.abc_closure(epsilonclosure.get(i),ndfa);
//                System.out.println("Set" + i + " : " + epsilonclosure.get(i) +"  ->" +'a'+ ":"+ testtable1[(int)'a']  +"  ->" +'b'+ ":"+ testtable1[(int)'b'] +"  a->" +'c'+ ":"+ testtable1[(int)'c']);
//            }

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


        }


        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson.");
    }
}
