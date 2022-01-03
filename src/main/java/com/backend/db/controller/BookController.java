package com.backend.db.controller;

import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.service.impl.BookServiceImpl;
import com.backend.db.service.impl.IndexServiceImpl;
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


    @ResponseBody
    @PostMapping("/library/load")
    public Boolean load(){
        int nb = NB_book;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));

        List<Book> books = new ArrayList<>();
        int i = 100;
        while(nb>0){
            System.out.println(i);
            String s = "https://www.gutenberg.org/files/"+i+"/"+i+"-0.txt";
            String res = null;
            try {
                res = restTemplate.getForObject(s, String.class);
            }catch (Exception e){
                i++;
                continue;
            }
            String title = null;
            String author = null;
            Integer id = null;

            Scanner scan = new Scanner(res);
            while(scan.hasNextLine()){
                String courant = scan.nextLine();
                if(courant.matches("^Title: .*")){
                    title = courant.substring(6).trim();
                }
                if(courant.matches("^Author: .*")){
                    author = courant.substring(7).trim();
                }
                if(courant.matches("^Release Date: .*")){
                    String t = courant.split("#")[1];
                    String t2 = t.substring(0,t.length()-1);
                    id = Integer.valueOf(t2);
                }
                if(courant.matches("Contents")){
                    break;
                }
            }
            StringBuilder contents = new StringBuilder();
            while(scan.hasNextLine()){
                contents.append(scan.nextLine()+"\n");
            }
            if(id == null && title == null && author ==null){
                return false;
            }
            Book b = new Book();
            b.setTitle(title);
            b.setAuthor(author);
            b.setId(id);
            b.setContents(contents.toString());
            books.add(b);

            List<Index> indexs = indexService.trans_index(contents.toString(),id);
            for(Index index:indexs){
                indexService.saveIndex(index);
            }

            i++;
            nb--;
        }
        for(Book book:books){
            bookService.saveBook(book);
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
