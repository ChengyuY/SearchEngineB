package Test;

import RegEx.DFAutomaton;
import RegEx.NDFAutomaton;
import RegEx.RegExTree;

import java.util.*;

public class TestNDFA {
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



        }
        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson.");
    }
}
