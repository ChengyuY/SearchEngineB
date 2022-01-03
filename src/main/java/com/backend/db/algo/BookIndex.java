package com.backend.db.algo;

import java.util.HashMap;
import java.util.Map;

public class BookIndex {

    public static Map<String,Integer> Trans_Index(String text){
        Map<String,Integer> map = new HashMap<>();
        String t = text.toLowerCase();
        String t2 = t.replaceAll("[^a-zA-Z]"," ");
        String[] lines = t2.split("\\s+");

        for(String line:lines){
            String[] words = line.split(" ");
            for(String word:words){
                if(word.length()> 2){
                    if(map.containsKey(word)){
                        map.put(word,map.get(word)+1);
                    }else{
                        map.put(word,1);
                    }
                }
            }
        }
        return map;
    }
}
