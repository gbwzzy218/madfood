package com.example.zhuzh.a6200finalwithfirebase.JavaPackage;

/**
 * Created by zhuzh on 2017/11/21.
 */

public class OrderItem {
    String name;
    int qty;
    double price;

    public OrderItem(){}

    public OrderItem(String name, int qty) {
        this.name = name;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
