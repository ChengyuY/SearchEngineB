package com.backend.db.service.impl;




import com.backend.db.algo.BookIndex;
import com.backend.db.algo.KMP;
import com.backend.db.algo.RegEx;
import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.bean.Jaccard;
import com.backend.db.mapper.IndexMapper;
import com.backend.db.service.IndexService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class IndexServiceImpl  extends ServiceImpl<IndexMapper, Index> implements IndexService {

    @Autowired
    IndexMapper indexMapper;


    private ExecutorService executor = Executors.newCachedThreadPool();

    public Boolean saveIndex(Index index){
        Boolean b = saveOrUpdate(index);
        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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

    public List<Index> search_regex(String regex){
        List<Index> l = list();
        List<Index> indexs = new ArrayList<>();

        for(Index i : l) {
            if(RegEx.match(i.getWord(),regex) == true){
                indexs.add(i);
            }
        }

        return indexs;
    }

    public List<Index> search_kmp(String word){
        String word1 = word.toLowerCase();

        List<Index> l = list();
        List<Index> indexs = new ArrayList<>();

        for(Index i : l) {
            if(KMP.match(i.getWord(),word) != -1){
                indexs.add(i);
            }
        }

        return indexs;
    }

    public Future<Boolean> trans(String text,Integer id){
        return executor.submit(() -> {
            System.out.println("index: "+ id);
            Map<String,Integer> map = BookIndex.Trans_Index(text);
            List<Index> res = new ArrayList<>();
            for(String word:map.keySet()){
                Index index = new Index();
                index.setBookid(id);
                index.setCpt(map.get(word));
                index.setWord(word);
                boolean b = saveIndex(index);
                if(b == false){
                    return false;
                }
            }
            System.out.println("index: "+ id+" fini");
            return true;
        });
    }



    public Boolean trans_index_multithead(List<Book> books) throws ExecutionException, InterruptedException{
        List<Future<Boolean>> futures = new ArrayList<>();
        for(Book book: books){
            Future<Boolean> future = trans(book.getContents(), book.getId());
            futures.add(future);
        }

        for(Future<Boolean> f: futures){
            if(f.get() == false){
                return false;
            }
        }
        return true;
    }

}
