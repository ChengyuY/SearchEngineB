package com.backend.db.service.impl;




import com.backend.db.algo.BookIndex;
import com.backend.db.algo.KMP;
import com.backend.db.algo.RegEx;
import com.backend.db.bean.Index;
import com.backend.db.mapper.IndexMapper;
import com.backend.db.service.IndexService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexServiceImpl  extends ServiceImpl<IndexMapper, Index> implements IndexService {

    @Autowired
    IndexMapper indexMapper;

    public Boolean saveIndex(Index index){
        Boolean b = saveOrUpdate(index);
        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



    public List<Index> trans_index(String text,Integer id){
        Map<String,Integer> map = BookIndex.Trans_Index(text);
        List<Index> res = new ArrayList<>();
        for(String word:map.keySet()){
            Index index = new Index();
            index.setBookid(id);
            index.setCpt(map.get(word));
            index.setWord(word);
            res.add(index);
        }
        return res;
    }

    public List<Integer> search_par_mot_cle(String word){
        String word1 = word.toLowerCase();
        QueryWrapper<Index> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word",word1);
        queryWrapper.orderByDesc("cpt");
        List<Index> list = indexMapper.selectList(queryWrapper);
        List<Integer> res = new ArrayList<>();
        for(Index i:list){
            res.add(i.getBookid());
        }
        return res;
    }

    public List<Index> search_par_mot_kmp(String word){
        String word1 = word.toLowerCase();

        List<Index> l = list();
        List<Index> indexs = new ArrayList<>();

        for(Index i : l) {
            if(KMP.match(i.getWord(),word) != -1){
                indexs.add(i);
            }
        }


        Collections.sort(indexs, new Comparator<Index>() {
            @Override
            public int compare(Index o1, Index o2) {
                return o2.getCpt().compareTo(o1.getCpt());
            }
        });
        return indexs;
//        List<Integer> res = new ArrayList<>();
//        for(Index i:list){
//            res.add(i.getBookid());
//        }
//        return res;
    }

    public List<Index> search_par_mot_regex(String regex){
        List<Index> l = list();
        List<Index> indexs = new ArrayList<>();

        for(Index i : l) {
            if(RegEx.match(i.getWord(),regex) == true){
                indexs.add(i);
            }
        }
        Collections.sort(indexs, new Comparator<Index>() {
            @Override
            public int compare(Index o1, Index o2) {
                return o2.getCpt().compareTo(o1.getCpt());
            }
        });
        return indexs;

    }
}
