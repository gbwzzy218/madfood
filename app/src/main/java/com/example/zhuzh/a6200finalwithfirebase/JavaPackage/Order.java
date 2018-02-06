package com.example.zhuzh.a6200finalwithfirebase.JavaPackage;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zhuzh on 2017/11/21.
 */

public class Order implements Serializable {
    ArrayList<OrderItem> orderList;
    public static int count = 1;
    private int id;
    Status status;
    Customer customer;
    Date orderTime;
    double totalPrice;

    public enum Status{
        Submitting("Submitting"),Receiving("Receiving"),Preparing("Preparing"),Packaging("Packaging"),
        Finished("Finished"),Canceld("Canceled"),Partially("Partially"),FinishedAndPaid("Finished And Paid"),
        NotAvailable("NotAvailable");
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        Status(String value) {
            this.value = value;
        }
    }

    public Order() {
        this.orderList = new ArrayList<>();
        this.status = Status.Receiving;
        this.orderTime = new Date();
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public ArrayList<OrderItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<OrderItem> orderList) {
        this.orderList = orderList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice() {
        double result = 0;
        for(OrderItem oi:orderList){
            result += oi.getQty() * oi.getPrice();
        }
        result *= 1.06;
        totalPrice = result;
    }

    public String getTax () {
        double result = 0;
        for(OrderItem oi:orderList){
            result += oi.getQty() * oi.getPrice();
        }
        result *= 0.06;
        return String.format(".2f",result);
    }

    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String date = sdf.format(orderTime);
        return String.valueOf(this.id) + "        " + this.status.getValue() + "     " + date ;
    }
}
