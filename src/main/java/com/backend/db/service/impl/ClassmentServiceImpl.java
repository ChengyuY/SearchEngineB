package com.backend.db.service.impl;



import com.backend.db.algo.Floyd;
import com.backend.db.algo.RegEx;
import com.backend.db.bean.Book;
import com.backend.db.bean.Classment;
import com.backend.db.bean.Index;
import com.backend.db.bean.Jaccard;
import com.backend.db.mapper.ClassmentMapper;
import com.backend.db.mapper.JaccardMapper;
import com.backend.db.service.ClassmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClassmentServiceImpl extends ServiceImpl<ClassmentMapper, Classment> implements ClassmentService {

    @Autowired
    IndexServiceImpl indexService;

    public Boolean saveClassment(Classment c){
        Boolean b = saveOrUpdate(c);
        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Classment get_by_id(Integer id){
        return getById(id);
    }

    public List<Index> seach_kmp_classment(String word){
        List<Index> list = indexService.search_kmp(word);

        Collections.sort(list, new Comparator<Index>() {
            @Override
            public int compare(Index o1, Index o2) {
                int book1 = o1.getBookid();
                int book2 = o2.getBookid();
                Classment c1 = get_by_id(book1);
                Classment c2 = get_by_id(book2);
                return Double.compare(c2.getScore(),c1.getScore());
            }
        });
        return list;
    }

    public List<Index> search_regex_classment(String regex){
        List<Index> list = indexService.search_regex(regex);

        Collections.sort(list, new Comparator<Index>() {
            @Override
            public int compare(Index o1, Index o2) {
                int book1 = o1.getBookid();
                int book2 = o2.getBookid();
                Classment c1 = get_by_id(book1);
                Classment c2 = get_by_id(book2);
                return Double.compare(c2.getScore(),c1.getScore());
            }
        });
        return list;

    }



    public Boolean load(List<Jaccard> graph, List<Integer> books){
        double weight [][] = new double[books.size()][books.size()];


        for(int i = 0 ; i < books.size(); i++){
            Arrays.fill(weight[i], Double.MAX_VALUE);
        }

        for(int i = 0 ; i < books.size(); i++){
            weight[i][i] = 0;
        }

        for(Jaccard jac : graph){
            if(jac.getDistance() > 0.7) {
                int vet1 = books.indexOf(jac.getBook1());
                int vet2 = books.indexOf(jac.getBook2());
                weight[vet1][vet2] = jac.getDistance();
                weight[vet2][vet1] = jac.getDistance();
            }
        }


        int [] b = new int[books.size()];
        for(int i = 0; i < books.size();i++){
            b[i] = books.get(i);
        }

        Floyd floydGraph = new Floyd(b, weight);
        floydGraph.floydAlgorithm();
        double [][] dis = floydGraph.getDis();


        int n = books.size();
        for(int i = 0 ; i < n ; i++){
            double crank = (n-1) / Arrays.stream(dis[i]).sum();
            Classment c = new Classment();
            c.setId(books.get(i));
            c.setScore(crank);
            boolean bool = saveClassment(c);
            if(bool == false){
                return false;
            }
        }

        return true;
    }



}
