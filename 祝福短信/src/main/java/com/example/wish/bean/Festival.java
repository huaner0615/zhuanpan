package com.example.wish.bean;

import java.util.Date;

/**
 * Created by huanhuan on 2016/5/15.
 */
public class Festival {

    private int id;
    private String name;
    private String desc;
    private Date date;

    public Festival(int id, String name) {
        this.id=id;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

