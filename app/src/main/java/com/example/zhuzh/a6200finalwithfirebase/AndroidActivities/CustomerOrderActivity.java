package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class CustomerOrderActivity extends AppCompatActivity {

    Customer customer;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Order> orderArrayList;
    ArrayList<Order> allOrderArrayList;
    ListView listView;
    Order selectedOrder;
    int selectedPosition;
    String cName;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
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
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Test");
        databaseReference.child("Sending&ReceivingCatagory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderArrayList = new ArrayList<>();
                allOrderArrayList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child: children){
                    Order or = child.getValue(Order.class);
                    allOrderArrayList.add(or);
                    if (or.getCustomer().getName().equals(cName)) {
                        orderArrayList.add(or);
                    }
                }
                populate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView = findViewById(R.id.customerOrder);


    }

    private void populate (){
        @SuppressLint("ResourceType") ArrayAdapter<Order> adapter = new ArrayAdapter<Order>(CustomerOrderActivity.this,
                android.R.layout.simple_list_item_1, orderArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                selectedOrder = (Order)listView.getItemAtPosition(position);
            }
        });
        if(flag == 0){
            flag = 1;
                    for(Order or: orderArrayList){
                        if(or.getStatus().equals(Order.Status.Partially) || or.getStatus().equals(Order.Status.NotAvailable)){
                            new AlertDialog.Builder(CustomerOrderActivity.this)
                                    .setIcon(null)
                                    .setTitle("Notification from Chef?")
                                    .setMessage("Some of your orders cannot be prepared or can only partially prepared, please check the detail to decide if you still wanna place you order.")
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    // TODO Auto-generated method stub

                                                }
                                            }).create()
                                    .show();
                            break;
                        }
                    }
        }
    }

    public void onClickCheckout(View view) {
        if(selectedOrder == null){
            Toast.makeText(CustomerOrderActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(CustomerOrderActivity.this)
                .setIcon(null)
                .setTitle("Do you confirm to checkout?")
                .setMessage("$" + String.format("%.2f", selectedOrder.getTotalPrice()) + "in all")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                if(selectedOrder.getStatus().equals(Order.Status.Finished)){
                                    selectedOrder.setStatus(Order.Status.FinishedAndPaid);
                                    databaseReference.child("Sending&ReceivingCatagory").setValue(allOrderArrayList);
                                    return;
                                }else if(selectedOrder.getStatus().equals(Order.Status.FinishedAndPaid)){
                                    Toast.makeText(CustomerOrderActivity.this, "You have paid this order before! Thank you!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }else {
                                    Toast.makeText(CustomerOrderActivity.this, "You food is not ready yet!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }
                        }).setNegativeButton("No", null).create()
                .show();
    }

    public void onClickViewDetails(View view) {
        if(selectedOrder == null){
            Toast.makeText(CustomerOrderActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent chefListDetail = new Intent (CustomerOrderActivity.this,CustomerOrderActivityDetail.class);

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

    public void onClickCancel(View view) {
        if(selectedOrder != null){
            if(selectedOrder.getStatus().equals(Order.Status.NotAvailable) || selectedOrder.getStatus().equals(Order.Status.Partially)){
                new AlertDialog.Builder(CustomerOrderActivity.this)
                        .setIcon(null)
                        .setTitle("Do you confirm to cancel?")
                        .setMessage("Cancel the selected order?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        selectedOrder.setStatus(Order.Status.Canceld);
                                        databaseReference.child("Sending&ReceivingCatagory").setValue(allOrderArrayList);
                                        Toast.makeText(CustomerOrderActivity.this, "You Order is Canceld!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("No", null).create()
                        .show();
            }else{
                Toast.makeText(CustomerOrderActivity.this, "This Order Cannot be Canceld!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(CustomerOrderActivity.this, "Please Select",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickPartial(View view) {
        if(selectedOrder != null){
            if(selectedOrder.getStatus().equals(Order.Status.Partially)){
                new AlertDialog.Builder(CustomerOrderActivity.this)
                        .setIcon(null)
                        .setTitle("Do you confirm to Accept Changed Order?")
                        .setMessage("Accept this changed order?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        selectedOrder.setStatus(Order.Status.Receiving);
                                        databaseReference.child("Sending&ReceivingCatagory").setValue(allOrderArrayList);
                                        Toast.makeText(CustomerOrderActivity.this, "Your order is submitted!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("No", null).create()
                        .show();
            }else{
                Toast.makeText(CustomerOrderActivity.this, "You are not able to do so to this order!",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CustomerOrderActivity.this, "Please Select!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
