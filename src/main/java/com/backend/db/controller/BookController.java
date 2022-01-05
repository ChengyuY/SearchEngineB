package com.backend.db.controller;

import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.service.impl.BookServiceImpl;
import com.backend.db.service.impl.IndexServiceImpl;
import com.backend.db.service.impl.JaccardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Validated
@RestController
public class BookController {

    int NB_book = 5;


    @Autowired
    BookServiceImpl bookService;
    @Autowired
    IndexServiceImpl indexService;

    @Autowired
    JaccardServiceImpl jaccardService;


    @ResponseBody
    @PostMapping("/library/load")
    public Boolean load(){
        Boolean b = bookService.load(5);
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
            jaccardService.loadJaccard(indexService.list(),bookService.books_id());
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @ResponseBody
    @GetMapping("/library/searchbyword")
    public List<Integer> searchbyword(@RequestParam("word") String word){
        return indexService.search_par_mot_cle(word);
    }

    @ResponseBody
    @GetMapping("/library/searchbywordkmp")
    public List<Index> searchbywordkmp(@RequestParam("word") String word){
        return indexService.search_par_mot_kmp(word);
    }

    @ResponseBody
    @GetMapping("/library/searchbywordregex")
    public List<Index> searchbywordregex(@RequestParam("regex") String regex){
        return indexService.search_par_mot_regex(regex);
    }

}
