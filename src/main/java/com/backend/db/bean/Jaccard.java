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
@TableName("jaccard")
public class Jaccard {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Integer book1;

    private Integer book2;

    private double distance;


    public Integer getBook1() {
        return book1;
    }

    public void setBook1(Integer book1) {
        this.book1 = book1;
    }

    public Integer getBook2() {
        return book2;
    }

    public void setBook2(Integer book2) {
        this.book2 = book2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
