package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.Customer;
import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.Order;
import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.OrderItem;
import com.example.zhuzh.a6200finalwithfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {
    Customer customer;
    String cName;
    int orderId;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Customer> customers;
    TextView burgerQtyText;
    TextView chickenQtyText;
    TextView frenchFriesText;
    TextView onionRingsText;
    int minteger1=0;
    int minteger2=0;
    int minteger3=0;
    int minteger4=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        TextView testIntent = (TextView)findViewById(R.id.testUserInfotUserInfo);
        burgerQtyText = (TextView)findViewById(R.id.etFoodQty_1);
        chickenQtyText = (TextView)findViewById(R.id.etFoodQty_2);
        frenchFriesText = (TextView)findViewById(R.id.etFoodQty_3);
        onionRingsText = (TextView) findViewById(R.id.etFoodQty_4);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Test");
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customers = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child: children){
                    Customer c = child.getValue(Customer.class);
                    customers.add(c);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                cName = null;
            } else {
                cName = extras.getString("User Name");
            }
        } else {
            cName = (String) savedInstanceState.getSerializable("User Name");
        }
        databaseReference.child("OrderID").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orderId = Integer.parseInt(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        testIntent.setText(cName);
    }

    public void increaseInteger(View view) {
        minteger1 = minteger1 + 1;
        display(minteger1);
    }

    public void decreaseInteger(View view) {
        if (minteger1>0) display(--minteger1);
    }

    public void increaseInteger2(View view) {
        minteger2 = minteger2 + 1;
        display2(minteger2);
    }

    public void decreaseInteger2(View view) {

        if (minteger2>0) display2(--minteger2);
    }

    public void increaseInteger3(View view) {
        minteger3 = minteger3 + 1;
        display3(minteger3);
    }

    public void decreaseInteger3(View view) {
        if (minteger3>0) display3(--minteger3);
    }

    public void increaseInteger4(View view) {
        minteger4 = minteger4 + 1;
        display4(minteger4);
    }

    public void decreaseInteger4(View view) {
        if (minteger4>0) display4(--minteger4);
    }

    private void display(int number) {
        burgerQtyText.setText("" + number);
    }

    private void display2(int number) {
        chickenQtyText.setText("" + number);
    }

    private void display3(int number) {
        frenchFriesText.setText("" + number);
    }

    private void display4(int number) {
        onionRingsText.setText("" + number);
    }

    public void onClickSubmit(View view) {
        //this customer should be the one who login this activity

        final Order order = new Order();
        databaseReference.child("OrderID").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        orderId = Integer.parseInt(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        setCustomer();
        order.setCustomer(customer);

        try{
            String burgerQty = burgerQtyText.getText().toString();
            OrderItem oi = new OrderItem("Burger", Integer.parseInt((burgerQty)));
            oi.setPrice(7.15);
            order.getOrderList().add(oi);
            String chickenQty = chickenQtyText.getText().toString();
            oi = new OrderItem("Chicken", Integer.parseInt(chickenQty));
            oi.setPrice(7.8);
            order.getOrderList().add(oi);
            String firesQty = frenchFriesText.getText().toString();
            oi = new OrderItem("French Fries", Integer.parseInt(firesQty));
            oi.setPrice(2.6);
            order.getOrderList().add(oi);
            String onionQty = onionRingsText.getText().toString();
            oi = new OrderItem("Onion Rings", Integer.parseInt(onionQty));
            oi.setPrice(3.25);
            order.getOrderList().add(oi);
            ArrayList<OrderItem> temp = new ArrayList<>();
            for(OrderItem orderI: order.getOrderList()){
                if(orderI.getQty() != 0){
                    temp.add(orderI);
                }
            }
            order.setOrderList(temp);
        }catch(Exception e){
            Toast.makeText(CustomerActivity.this, "Please Enter Correct Quantity!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(order.getOrderList().isEmpty()){
            Toast.makeText(CustomerActivity.this, "You Ordered Nothing!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        order.setTotalPrice();
        order.setId(orderId++);
        databaseReference.child("OrderID").setValue(String.valueOf(orderId));
        Thread submitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    submitOrder(order);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        submitThread.start();
    }



    public void setCustomer(){

        for(Customer c: customers){
            if(c.getName().equals(cName)){
                customer = c;
            }
        }
    }

    //this method will send an order to cloud
    public void submitOrder(Order order){

        databaseReference.child("Sending&ReceivingCatagory").push().setValue(order);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CustomerActivity.this, "Your order has been submited!",
                        Toast.LENGTH_SHORT).show();
                burgerQtyText.setText("0");
                chickenQtyText.setText("0");
                onionRingsText.setText("0");
                frenchFriesText.setText("0");
            }
        });
    }

    public void onClickViewSubmitOrder(View view) {
        Intent intent = new Intent(CustomerActivity.this, CustomerOrderActivity.class);
        intent.putExtra("User Name", cName);
        startActivity(intent);
    }
}
