package com.backend.db.service.impl;





import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.bean.Jaccard;
import com.backend.db.mapper.IndexMapper;
import com.backend.db.mapper.JaccardMapper;
import com.backend.db.service.JaccardService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class JaccardServiceImpl extends ServiceImpl<JaccardMapper, Jaccard> implements JaccardService {

    @Autowired
    IndexMapper indexMapper;


    private ExecutorService executor = Executors.newCachedThreadPool();


    public Boolean saveIndex(Jaccard jaccard){
        Boolean b = saveOrUpdate(jaccard);
        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



    public Boolean loadJaccard(List<Index> indexs,List<Book> books) throws ExecutionException, InterruptedException{
        List<Future<Boolean>> futures = new ArrayList<>();
        List<Integer> idbooks = new ArrayList<>();
        int nb = books.size();
        for(Book b: books){
            idbooks.add(b.getId());
        }

        List<List<Index>> indexs_by_id = new ArrayList<>();
        for(int i = 0; i < nb ; i++){
            QueryWrapper<Index> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("bookid",idbooks.get(i));
            queryWrapper.orderByDesc("cpt");
            indexs_by_id.add(indexMapper.selectList(queryWrapper));
        }

        for(int i = 0 ; i < nb-1; i++){
            for(int j = i+1; j < nb ; j++){
//                List<Index> i1 = indexs_by_id.get(i);
//                List<Index> i2 = indexs_by_id.get(j);
//                Set<String> str = new HashSet<>();
//                for(int k = 0; k < i1.size();k++){
//                    str.add(i1.get(k).getWord());
//                }
//                for(int k = 0; k < i2.size();k++){
//                    str.add(i2.get(k).getWord());
//                }
//                double distance = jaccard_distance(str,i1,i2);
//                Jaccard jac = new Jaccard();
//                jac.setBook1(idbooks.get(i));
//                jac.setBook2(idbooks.get(j));
//                jac.setDistance(distance);
//                saveOrUpdate(jac);
                  Future<Boolean> future = calculate(i,j,indexs_by_id,idbooks);
                  futures.add(future);
            }
        }

        for(Future<Boolean> f : futures){
            if(f.get() == false){
                return false;
            }
        }


        return true;
    }

    public double jaccard_distance(Set<String> strings, List<Index> i1, List<Index> i2){
        double somme = 0;
        double res = 0;
        for(String s: strings){
            double cpt1 = cpt_search(i1,s);
            double cpt2 = cpt_search(i2,s);
            somme += Math.max(cpt1,cpt2);
            res +=  Math.max(cpt1,cpt2) - Math.min(cpt1,cpt2);
        }

        return res/somme;
    }


    public Integer cpt_search(List<Index> indexs, String str){
        for(Index i: indexs){
            if(i.getWord().equals(str)){
                return i.getCpt();
            }
        }
        return 0;
    }



    public Future<Boolean> calculate(Integer i, Integer j, List<List<Index>> indexs_by_id,List<Integer> idbooks ) {
        return executor.submit(() -> {
            System.out.println(i+ " "+j+" start");
            List<Index> i1 = indexs_by_id.get(i);
            List<Index> i2 = indexs_by_id.get(j);
            Set<String> str = new HashSet<>();
            for(int k = 0; k < i1.size();k++){
                str.add(i1.get(k).getWord());
            }
            for(int k = 0; k < i2.size();k++){
                str.add(i2.get(k).getWord());
            }
            double distance = jaccard_distance(str,i1,i2);
            Jaccard jac = new Jaccard();
            jac.setBook1(idbooks.get(i));
            jac.setBook2(idbooks.get(j));
            jac.setDistance(distance);
            return saveOrUpdate(jac);
        });
    }

}
