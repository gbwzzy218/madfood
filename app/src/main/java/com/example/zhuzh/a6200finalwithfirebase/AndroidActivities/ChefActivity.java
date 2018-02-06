package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.Order;
import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.OrderItem;
import com.example.zhuzh.a6200finalwithfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChefActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Order> orderCatagory;
    ArrayList<Integer> inventoryQty;
    Order selectedOrder;
    int selectedPosition;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);
        listView = (ListView)findViewById(R.id.listOrder);
        orderCatagory = new ArrayList<>();
        try {
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference();
            databaseReference.child("Test").child("Sending&ReceivingCatagory").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //this part get all orders which are stored in the cloud to the local ArrayList "orderCatagory"
                        orderCatagory = new ArrayList<>();
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child: children){
                            Order or = child.getValue(Order.class);
                            orderCatagory.add(or);
                        }
                        populate();
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            databaseReference.child("Test").child("Inventory").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    inventoryQty = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot child: children){
                        Integer i = child.getValue(Integer.class);
                        inventoryQty.add(i);
                    }
                    populate();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    //it's a demo to indicate we have retrieved data from cloud successfully
    private void populate (){
        @SuppressLint("ResourceType") ArrayAdapter<Order> adapter = new ArrayAdapter<Order>(ChefActivity.this,
                android.R.layout.simple_list_item_1, orderCatagory);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedPosition = position;
                        view.setBackgroundColor(0x4d90fe);
                        selectedOrder = (Order)listView.getItemAtPosition(position);
                    }
                });
    }

    public void prepareOrder(final Order selectedOrder){
        if(isEnough(selectedOrder) == true){
            Thread inventoryDeducThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (OrderItem oi: selectedOrder.getOrderList()){
                        if (oi.getName().equals("Burger")){
                            inventoryQty.set(0, inventoryQty.get(0) - oi.getQty());
                            continue;
                        }
                        if(oi.getName().equals("Chicken")){
                            inventoryQty.set(1, inventoryQty.get(1) - oi.getQty());
                            continue;
                        }
                        if(oi.getName().equals("French Fries")){
                            inventoryQty.set(2, inventoryQty.get(2) - oi.getQty());
                            continue;
                        }
                        if (oi.getName().equals("Onion Rings")){
                            inventoryQty.set(3, inventoryQty.get(3) - oi.getQty());
                            continue;
                        }
                    }
                    databaseReference.child("Test").child("Inventory").setValue(inventoryQty);
                }
            });
            inventoryDeducThread.start();
            Thread t = new Thread(new prepareRun());
            t.start();
        }else {
            Toast.makeText(ChefActivity.this, "Your inventory is not able to prepare this order!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public boolean isEnough(Order selectedOrder){
        for(OrderItem oi: selectedOrder.getOrderList()){
            if(oi.getName().equals("Burger") && oi.getQty() > inventoryQty.get(0)){
                return false;
            }else if(oi.getName().equals("Chicken") && oi.getQty() > inventoryQty.get(1)){
                return false;
            }else if(oi.getName().equals("French Fries") && oi.getQty() > inventoryQty.get(2)){
                return false;
            }else if(oi.getName().equals("Onion Rings") && oi.getQty() > inventoryQty.get(3)){
                return false;
            }
        }
        return true;
    }

    public void onClickProcess(View view) {
        if (selectedPosition < 0){
            Toast.makeText(ChefActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedPosition > 0){
            for(int i = 0; i < selectedPosition; i++){
                Order tempOrder = (Order) listView.getItemAtPosition(selectedPosition - 1);
                if (tempOrder.getStatus().equals(Order.Status.Receiving)){
                    Toast.makeText(ChefActivity.this, "Please Process Order Received Earlier!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(!selectedOrder.getStatus().equals(Order.Status.Receiving)){
                Toast.makeText(ChefActivity.this, "This Order Has Been Processed!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            prepareOrder(selectedOrder);
        }else {
            if(!selectedOrder.getStatus().equals(Order.Status.Receiving)){
                Toast.makeText(ChefActivity.this, "This Order Has Been Processed!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            prepareOrder(selectedOrder);
        }

    }

    public void onClickViewDetail(View view) {
        if(selectedOrder == null){
            Toast.makeText(ChefActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent chefListDetail = new Intent (ChefActivity.this,ChefListDetail.class);

        String Order_Id = String.valueOf(selectedOrder.getId());
        String Customer_Name = selectedOrder.getCustomer().getName();
        String Total_Price = String.format("%.2f", selectedOrder.getTotalPrice());
        String Order_Date = selectedOrder.getOrderTime().toString();
        String Order_State = selectedOrder.getStatus().getValue();
        String Order_Tax = selectedOrder.getTax();

        String[] Item_Name = new String[4];
        String[] Item_Qty = new String[4];
        String[] Item_Single_item_price = new String[4];
        int i = 0;
        for (OrderItem orderItem : selectedOrder.getOrderList())
        {
            Item_Name[i] = orderItem.getName();
            if (orderItem.getQty()>1) Item_Name[i]+="*"+Integer.toString(orderItem.getQty());
            Item_Single_item_price[i++] = String.format("%.2f",orderItem.getPrice()*orderItem.getQty());
        }

        chefListDetail.putExtra("Order_Id",Order_Id);
        chefListDetail.putExtra("Customer_Name",Customer_Name);
        chefListDetail.putExtra("Total_Price",Total_Price);
        chefListDetail.putExtra("Order_Date",Order_Date);
        chefListDetail.putExtra("Order_State",Order_State);
        chefListDetail.putExtra("Order_Tax",Order_Tax);

        chefListDetail.putExtra("Item_Name",Item_Name);
        chefListDetail.putExtra("Item_Qty",Item_Qty);
        chefListDetail.putExtra("Item_Single_item_price",Item_Single_item_price);

        chefListDetail.putExtra("ItemLength",i);

        startActivity(chefListDetail);
    }

    public void onClickInventory(View view) {
        Intent intent = new Intent(ChefActivity.this, InventoryActivity.class);
        startActivity(intent);
    }

    public void onClickNotification(View view) {
        if(selectedPosition < 0 || selectedOrder == null){
            Toast.makeText(ChefActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedPosition > 0){
            for(int i = 0; i < selectedPosition; i++){
                Order tempOrder = (Order) listView.getItemAtPosition(selectedPosition - 1);
                if (tempOrder.getStatus().equals(Order.Status.Receiving)){
                    Toast.makeText(ChefActivity.this, "Please Process Order Received Earlier!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(!selectedOrder.getStatus().equals(Order.Status.Receiving)){
                Toast.makeText(ChefActivity.this, "This Order Has Been Processed!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendNotification();
        }else {
            if(!selectedOrder.getStatus().equals(Order.Status.Receiving)){
                Toast.makeText(ChefActivity.this, "This Order Has Been Processed!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    class prepareRun implements Runnable {
        public void run() {
            try {
                Order tempOrder = selectedOrder;
                int tempPostion = selectedPosition;
                DatabaseReference temp = databaseReference.child("Test").child("Sending&ReceivingCatagory");
                tempOrder.setStatus(Order.Status.Preparing);
                temp.setValue(orderCatagory);
                Thread.sleep(prepareTime(tempOrder));
                tempOrder = (Order) listView.getItemAtPosition(tempPostion);
                tempOrder.setStatus(Order.Status.Packaging);
                temp.setValue(orderCatagory);
                Thread.sleep(packageTime(tempOrder));
                tempOrder = (Order) listView.getItemAtPosition(tempPostion);
                tempOrder.setStatus(Order.Status.Finished);
                temp.setValue(orderCatagory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int prepareTime(Order order){
        int burgerTime = 5000;
        int chickenTime = 6000;
        int friesTime = 2000;
        int ringsTime = 2000;
        int result = 0;
        for (OrderItem orderItem: order.getOrderList()) {
            if(orderItem.getName().equals("Burger")){
                result += orderItem.getQty() * burgerTime;
            }else if(orderItem.getName().equals("Chicken")){
                result += orderItem.getQty() * chickenTime;
            }else if(orderItem.getName().equals("French Fires")){
                result += orderItem.getQty() * friesTime;
            }else{
                result += orderItem.getQty() * ringsTime;
            }
        }
        return result;
    }

    public int packageTime(Order order){
        int burgerTime = 3000;
        int chickenTime = 2000;
        int friesTime = 1000;
        int ringsTime = 1000;
        int result = 0;
        for (OrderItem orderItem: order.getOrderList()) {
            if(orderItem.getName().equals("Burger")){
                result += orderItem.getQty() * burgerTime;
            }else if(orderItem.getName().equals("Chicken")){
                result += orderItem.getQty() * chickenTime;
            }else if(orderItem.getName().equals("French Fires")){
                result += orderItem.getQty() * friesTime;
            }else{
                result += orderItem.getQty() * ringsTime;
            }
        }
        return result;
    }

    public void sendNotification(){
        if(selectedOrder == null){
            Toast.makeText(ChefActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        new android.app.AlertDialog.Builder(ChefActivity.this)
                .setIcon(null)
                .setTitle("What message do you want to send?")
                .setMessage("Choose partially available or not available for this order!")
                .setPositiveButton("Partially Available",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if(isEnough(selectedOrder) == false){
                                    for(OrderItem oi: selectedOrder.getOrderList()){
                                        if(oi.getName().equals("Burger")){
                                            oi.setQty(oi.getQty() < inventoryQty.get(0) ? oi.getQty():inventoryQty.get(0));
                                        }
                                        if(oi.getName().equals("Chicken")){
                                            oi.setQty(oi.getQty() < inventoryQty.get(1) ? oi.getQty() : inventoryQty.get(1));
                                        }
                                        if (oi.getName().equals("French Fries")){
                                            oi.setQty(oi.getQty() < inventoryQty.get(2) ? oi.getQty() : inventoryQty.get(2));
                                        }
                                        if (oi.getName().equals("Onion Rings")){
                                            oi.setQty(oi.getQty() < inventoryQty.get(3) ? oi.getQty() : inventoryQty.get(3));
                                        }
                                    }
                                    ArrayList<OrderItem> temp = new ArrayList<>();
                                    for(OrderItem orderI: selectedOrder.getOrderList()){
                                        if(orderI.getQty() != 0){
                                            temp.add(orderI);
                                        }
                                    }
                                    selectedOrder.setOrderList(temp);
                                    if(selectedOrder.getOrderList().isEmpty()){
                                        Toast.makeText(ChefActivity.this, "Your inventory cannot prepare any food in this order!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        selectedOrder.setStatus(Order.Status.Partially);
                                        databaseReference.child("Test").child("Sending&ReceivingCatagory").setValue(orderCatagory);
                                        Toast.makeText(ChefActivity.this, "Your Response is Sent Successfully!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }else{
                                    Toast.makeText(ChefActivity.this, "Your Inventory is able to prepare this oreder!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }
                        }).setNegativeButton("Cannot Prepared", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isEnough(selectedOrder) == true){
                    Toast.makeText(ChefActivity.this, "Your Inventory is able to prepare this oreder!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    selectedOrder.setStatus(Order.Status.NotAvailable);
                    databaseReference.child("Test").child("Sending&ReceivingCatagory").setValue(orderCatagory);
                    Toast.makeText(ChefActivity.this, "Your Response is Sent Successfully!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }).create()
                .show();
    }

}
