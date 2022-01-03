package RegEx;

import java.util.*;

public class DFAutomaton {
    protected ArrayList<EtatState> transitionTable;

    public DFAutomaton() {
        this.transitionTable=new ArrayList<>();
    }

    public ArrayList<EtatState> getTransitionTable() {
        return transitionTable;
    }

    public void setTransitionTable(ArrayList<EtatState> transitionTable) {
        this.transitionTable = transitionTable;
    }



    //  Set0 : [0, 1, 3]  
    //ajouter tous les states à partir de s passant par les eplislons transitions au set
    public static void getnextstates(Integer s,NDFAutomaton ndfa,Set<Integer> set){
        set.add(s);
        ArrayList<Integer>[] TT = ndfa.epsilonTransitionTable;
        ArrayList<Integer> nextstates = TT[s];
        if(nextstates.size() != 0){
            for(int i = 0; i < nextstates.size(); i++){
                getnextstates(nextstates.get(i),ndfa,set);
            }
        }
    }

	//  Set0 : [0, 1, 3]  
	//  Set1 : [1] 
	//  Set2 : [2, 9]  
	//  Set3 : [3]  ->a:[]  
	//  Set4 : [4, 5, 6, 8, 9] 
	//  Set5 : [5, 6, 8, 9] 
	//  Set6 : [6]  ->a:[]  
	//  Set7 : [6, 7, 8, 9] 
	//  Set8 : [8, 9] 
	//  Set9 : [9] 
	//closure[i] :  getnextstates(i)
    //applique getnextstates() à chaque states pour avoir un hashmap ci_dessus
    public static HashMap<Integer, ArrayList<Integer>> epsilonclosure(NDFAutomaton ndfa){
        HashMap<Integer, ArrayList<Integer>> closure = new HashMap<>();
        for(int i = 0;i<ndfa.epsilonTransitionTable.length;i++){
            Set<Integer> set = new TreeSet<>();
            getnextstates(i,ndfa,set);
            closure.put(i,new ArrayList<>(set));
        }
        return closure;
    }
    
    

    //[0, 1, 3]  ->a:[2, 9]    ou   ->b:[4, 5, 6, 8, 9] 
    //return tous les states à partir d'un emsemble de states passant par un charactere  c
    public static ArrayList<Integer> a_closure(int c,ArrayList<Integer> closure,NDFAutomaton ndfa){
        int[][] transitionTable = ndfa.transitionTable;
        ArrayList<Integer> res = new ArrayList<>();
        for(int i = 0; i<closure.size();i++){
            int next = transitionTable[closure.get(i)][c];
            if(next != -1){
                res.add(next);
            }
        }
        return res;
    }


	//    Set0 : [0, 1, 3]  ->a:[2, 9]  ->b:[4, 5, 6, 8, 9]  ->c:[]
	//    Set1 : [1]  ->a:[2, 9]  ->b:[]  ->c:[]
	//    Set2 : [2, 9]  ->a:[]  ->b:[]  ->c:[]
	//    Set3 : [3]  ->a:[]  ->b:[4, 5, 6, 8, 9]  ->c:[]
	//    Set4 : [4, 5, 6, 8, 9]  ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
	//    Set5 : [5, 6, 8, 9]  ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
	//    Set6 : [6]  ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
	//    Set7 : [6, 7, 8, 9]  ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
	//    Set8 : [8, 9]  ->a:[]  ->b:[]  ->c:[]
	//    Set9 : [9]  ->a:[]  ->b:[]  ->c:[]
	//return un ArrayList<Integer>[]   ex: res[97] = [2,9]
    public static ArrayList<Integer>[] abc_closure(ArrayList<Integer> closure, NDFAutomaton ndfa){
        ArrayList<Integer>[] res = new ArrayList[256];
        for(int i = 0; i < 256; i++){
            Set<Integer> set = null;
            ArrayList<Integer> stateparcaractere = a_closure(i,closure,ndfa);
            int nbspc = stateparcaractere.size();
            if(nbspc > 0) {
                set = new TreeSet<>();
                for (int j = 0; j < nbspc;j++) {
                    getnextstates(stateparcaractere.get(j),ndfa,set);
                }
            }
            if(set == null) {
                res[i] = new ArrayList<>();
            }else{
                res[i] = new ArrayList<>(set);
            }
        }
        return res;
    }


//    Set[0, 1, 3] : ->a:[2, 9]  ->b:[4, 5, 6, 8, 9]  ->c:[]
//    Set[6, 7, 8, 9] : ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
//    Set[2, 9] : ->a:[]  ->b:[]  a->c:[]
//    Set[4, 5, 6, 8, 9] : ->a:[]  ->b:[]  ->c:[6, 7, 8, 9]
//    déterminer les nouvelles states  (émiliner des states non passant)
    public static void traiter (ArrayList<Integer> start,HashMap<ArrayList<Integer>, ArrayList<Integer>[]> closuretable,HashMap<ArrayList<Integer>, ArrayList<Integer>[]> res){

        ArrayList<Integer>[] transition = closuretable.get(start);
        res.put(start,transition);
        boolean exist = false;
        for(int i = 0; i < 256; i++){
            ArrayList<Integer> transition_i = transition[i];
            Character caractere = (char)i;
            if(transition_i.size() > 0){
                if(!res.containsKey(transition_i)){
                    traiter(transition_i,closuretable,res);
                }
            }
        }
    }




    public static DFAutomaton transforme(NDFAutomaton ndfa){
        Integer init = 0;
        Integer end = ndfa.transitionTable.length-1;

        HashMap<Integer, ArrayList<Integer>> epsilonclosure = epsilonclosure(ndfa);
        HashMap<ArrayList<Integer>, ArrayList<Integer>[]> closuretable= new HashMap<>();

        for(int i = 0;i<epsilonclosure.size();i++) {
            ArrayList<Integer>[] testtable1 = abc_closure(epsilonclosure.get(i), ndfa);
            closuretable.put(epsilonclosure.get(i),testtable1);
        }

        HashMap<ArrayList<Integer>, ArrayList<Integer>[]> res= new HashMap<>();
        traiter(epsilonclosure.get(0),closuretable,res);

        
        //res.forEach((node,sousstate) -> System.out.println("Set" + node + " : ->" +'a'+ ":"+ sousstate[(int)'a']  +"  ->" +'b'+ ":"+ sousstate[(int)'b'] +"  ->" +'c'+ ":"+ sousstate[(int)'c']));

        Iterator< ArrayList<Integer> > iterator = res.keySet().iterator();

        
        // à partir de nos Hashmap res, nous pouvons réunir les closures en organisant un nouvel type EtatState
        //Exemple a|bc*-> 
        //EtatState{numstate=0, sfinal=false, sousstate={a=2, b=4}}
        //EtatState{numstate=6, sfinal=true, sousstate={c=6}}
        //EtatState{numstate=2, sfinal=true, sousstate={}}
        //EtatState{numstate=4, sfinal=true, sousstate={c=6}}
        ArrayList<EtatState> resultat = new ArrayList<>();
        while(iterator.hasNext()){
            ArrayList<Integer> key = iterator.next();
            EtatState e = new EtatState();
            e.setNumstate(key.get(0));
            if(key.contains(end)){
                e.setSfinal(true);
            }
            ArrayList<Integer>[] values = res.get(key);
            for(int i = 0; i < 256; i++){
                ArrayList<Integer> value = values[i];
                if(value.size()>0){
                    e.addSousState((char)i,value.get(0));
                }
            }
            resultat.add(e);
        }

        DFAutomaton dfa = new DFAutomaton();
        dfa.setTransitionTable(resultat);
        return dfa;
    }


}
