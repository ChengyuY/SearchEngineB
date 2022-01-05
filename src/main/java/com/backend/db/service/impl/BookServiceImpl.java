package com.backend.db.service.impl;

import com.backend.db.bean.Book;
import com.backend.db.bean.Index;
import com.backend.db.mapper.BookMapper;
import com.backend.db.service.BookService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    BookMapper bookMapper;

    public Book Bookbyid(Integer id){
        Book i= getById(id);
        return i;
    }

    public Boolean saveBook(Book book){
        Boolean b = saveOrUpdate(book);
        if (b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public List<Book> books_id(){
        QueryWrapper<Book> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        return bookMapper.selectList(queryWrapper);
    }

    public Boolean load(Integer nb){
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

            i++;
            nb--;
        }
        for(Book book:books){
            saveBook(book);
        }
        return true;
    }

}
