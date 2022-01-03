package com.backend.db.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Validated
@TableName("bookindex")
public class Index {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private String word;

    private Integer bookid;

    private Integer cpt;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getBookid() {
        return bookid;
    }

    public void setBookid(Integer bookid) {
        this.bookid = bookid;
    }

    public Integer getCpt() {
        return cpt;
    }

    public void setCpt(Integer cpt) {
        this.cpt = cpt;
    }
}
