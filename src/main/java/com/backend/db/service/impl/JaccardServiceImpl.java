package com.backend.db.service.impl;





import com.backend.db.bean.Book;
import com.backend.db.bean.Classment;
import com.backend.db.bean.Index;
import com.backend.db.bean.Jaccard;
import com.backend.db.mapper.IndexMapper;
import com.backend.db.mapper.JaccardMapper;
import com.backend.db.service.JaccardService;
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
public class JaccardServiceImpl extends ServiceImpl<JaccardMapper, Jaccard> implements JaccardService {


    @Autowired
    IndexMapper indexMapper;

    @Autowired
    JaccardMapper jaccardMapper;

    @Autowired
    BookServiceImpl bookService;

    @Autowired
    ClassmentServiceImpl classmentService;


    private ExecutorService executor = Executors.newCachedThreadPool();



    public Boolean saveIndex(Jaccard jaccard){
        Boolean b = saveOrUpdate(jaccard);

        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<Jaccard> getAll(){
        return list();
    }

    //order by jaccard distance
    public List<Integer> get_neighbor(Integer id){
        QueryWrapper<Jaccard> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("distance");
        List<Jaccard> list = jaccardMapper.selectList(queryWrapper);
        List<Integer> res = new ArrayList<>();
        List<Jaccard> jacs = new ArrayList<>();

        for(Jaccard j : list){
            if(j.getBook1() == id){
                jacs.add(j);
            }
            if(j.getBook2() == id){
                jacs.add(j);
            }
        }

        Collections.sort(jacs, new Comparator<Jaccard>() {
            @Override
            public int compare(Jaccard o1, Jaccard o2) {
                Double c1 = o1.getDistance();
                Double c2 = o2.getDistance();
                return Double.compare(c2,c1);
            }
        });

        for(Jaccard j : jacs){
            if(j.getBook1() == id){
                res.add(j.getBook2());
            }
            if(j.getBook2() == id){
                res.add(j.getBook1());
            }
        }

        return res;
    }



    public Boolean loadJaccard(List<Index> indexs) throws ExecutionException, InterruptedException{
        List<Future<Boolean>> futures = new ArrayList<>();
        List<Integer> idbooks = bookService.books_id_int();
        int nb = idbooks.size();

//        List<List<Index>> indexs_by_id = new ArrayList<>();
//        for(int i = 0; i < nb ; i++){
//            QueryWrapper<Index> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("bookid",idbooks.get(i));
//            indexs_by_id.add(indexMapper.selectList(queryWrapper));
//        }

        for(int i = 0 ; i < nb-1; i++){
            QueryWrapper<Index> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("bookid",idbooks.get(i));
            List<Index> i1 = indexMapper.selectList(queryWrapper);

            for(int j = i+1; j < nb ; j++){
                QueryWrapper<Index> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("bookid",idbooks.get(j));
                List<Index> i2 = indexMapper.selectList(queryWrapper2);
                Future<Boolean> future = calculate(i1,i2,idbooks,i,j);
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



    public Future<Boolean> calculate(List<Index> i1, List<Index> i2,List<Integer> idbooks,int i,int j) {
        return executor.submit(() -> {
            System.out.println(i+ " "+j+" start");
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
            System.out.println(i+ " "+j+" finish");
            return saveOrUpdate(jac);

        });
    }

}
