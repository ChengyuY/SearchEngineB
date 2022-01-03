import RegEx.*;

import java.util.Scanner;

public class Egrep {
	
	public static void main(String[] arg) {
        String regEx;
        String path;
        if (arg.length!=0) {
            regEx = arg[0];
            path = arg[1];
        }else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regEx = scanner.next();
            System.out.print("  >> Please enter a path for the text: ");
            path = scanner.next();
        }

            if (regEx.length() < 1) {
                System.err.println(" ERROR: empty regEx !");
            } else {
                RegExTree ret = null;
                try {
                    ret = RegExTree.parse(regEx);
                } catch (Exception e) {
                    System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
                }
                NDFAutomaton ndfa = NDFAutomaton.step2_AhoUllman(ret);
                DFAutomaton dfa = DFAutomaton.transforme(ndfa);
                Minimisation.minimisation(dfa);
                Moteur m = new Moteur(dfa);

                try {
                    m.matchText(path);
                } catch (Exception e) {
                    System.out.println("ERROR : path non valide");
                }

            }
        }




}
