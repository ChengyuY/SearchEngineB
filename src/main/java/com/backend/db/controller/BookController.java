package com.backend.db.controller;

import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.service.impl.BookServiceImpl;
import com.backend.db.service.impl.ClassmentServiceImpl;
import com.backend.db.service.impl.IndexServiceImpl;
import com.backend.db.service.impl.JaccardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
public class BookController {

    int NB_book = 100;


    @Autowired
    BookServiceImpl bookService;
    @Autowired
    IndexServiceImpl indexService;
    @Autowired
    JaccardServiceImpl jaccardService;
    @Autowired
    ClassmentServiceImpl classmentService;


    @ResponseBody
    @PostMapping("/library/load")
    public Boolean load(){
        Boolean b = bookService.load(NB_book);
        if(b == false){
            return  false;
        }

        try{
            indexService.trans_index_multithead(bookService.list());
        }catch (Exception e){
            return  false;
        }
        return true;
    }

    @ResponseBody
    @PostMapping("/library/loadgraph")
    public Boolean loadgraph(){
        try{
            jaccardService.loadJaccard(indexService.list());
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @ResponseBody
    @PostMapping("/library/loadrank")
    public Boolean loadrank(){
          return  classmentService.load(jaccardService.getAll(),bookService.books_id_int());
    }

    @ResponseBody
    @GetMapping("/library/searchbyword")
    public List<Book> searchbyword(@RequestParam("word") String word){
        List<Integer> idList = indexService.search_par_mot_cle(word);
        List<Book> books = new ArrayList<>();
        for (Integer id: idList) {
            books.add(bookService.Bookbyid(id));
        }
        return books;
    }

    @ResponseBody
    @RequestMapping("/library/searchbyid")
    public List<Book> searchbyid(@RequestParam("id") Integer id){
        List<Book> books = new ArrayList<>();
        books.add(bookService.Bookbyid(id));
        return books;
    }


    @ResponseBody
    @GetMapping("/library/searchbywordkmp")
    public List<Index> searchbywordkmp(@RequestParam("word") String word){
        List<Index> res = indexService.search_par_mot_kmp(word);
        if (res.size() > 100) return res.subList(0,100);
        else return res;
    }

    @ResponseBody
    @GetMapping("/library/searchbywordregex")
    public List<Index> searchbywordregex(@RequestParam("regex") String regex){
        List<Index> res = indexService.search_par_mot_regex(regex);
        if (res.size() > 100) return res.subList(0,100);
        else return res;
    }

    @ResponseBody
    @GetMapping("/library/searchbywordregex_classment")
    public List<Index> searchbywordregex_classment(@RequestParam("regex") String regex){
        List<Index> res = classmentService.search_regex_classment(regex);
        if (res.size() > 100) return res.subList(0,100);
        else return res;
    }

    @ResponseBody
    @GetMapping("/library/searchbywordkmp_classment")
    public List<Index> searchbywordkmp_classment(@RequestParam("word") String word){
        List<Index> res = classmentService.seach_kmp_classment(word);
        if (res.size() > 100) return res.subList(0,100);
        else return res;
    }

    @ResponseBody
    @GetMapping("/library/neighbor")
    public List<Integer> get_suggestions(@RequestParam("book") Integer id){
        return jaccardService.get_neighbor(id);
    }


}
