package com.example.zhuzh.a6200finalwithfirebase.JavaPackage;

import java.io.Serializable;

/**
 * Created by zhuzh on 2017/11/25.
 */

public class Customer implements Serializable {
    String name;
    String pass;
    public Customer (){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
