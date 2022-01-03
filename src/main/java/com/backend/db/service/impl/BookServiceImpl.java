package com.backend.db.service.impl;

import com.backend.db.bean.Book;
import com.backend.db.mapper.BookMapper;
import com.backend.db.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

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
}
