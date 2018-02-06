package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class InventoryActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<Integer> inventoryQty;
    TextView burgerInventory;
    TextView chickenInventory;
    TextView friesInventory;
    TextView onionInventory;
    Map<String, Integer> inventoryTxt;
    int curHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        burgerInventory = (TextView)findViewById(R.id.invenFoodQty_1);
        chickenInventory = (TextView)findViewById(R.id.invenFoodQty_2);
        friesInventory = (TextView)findViewById(R.id.invenFoodQty_3);
        onionInventory = (TextView)findViewById(R.id.invenFoodQty_4);
        Date d = new Date();
        curHour = d.getHours();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
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
        inventoryTxt = new HashMap<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                databaseReference.child("Test").child("CurHour").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int remoteCur = Integer.parseInt(dataSnapshot.getValue(String.class));
                        if(curHour > remoteCur){
                            addBurger();
                            addChicken();
                            addFries();
                            addOnionRings();
                            databaseReference.child("Test").child("CurHour").setValue(String.valueOf(curHour));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        t.start();
    }

    public void populate(){
        burgerInventory.setText(inventoryQty.get(0).toString());
        chickenInventory.setText(inventoryQty.get(1).toString());
        friesInventory.setText(inventoryQty.get(2).toString());
        onionInventory.setText(inventoryQty.get(3).toString());
    }

    public void onClickForceBurger(View view) {

        try{
            addBurger();
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void onClickForceChicken(View view) {
        try{
            addChicken();
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void onClickForceFries(View view) {

        try{
            addFries();
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void onClickForceOnion(View view) {

        try{
            addOnionRings();
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void readTxt() {
        try {
            inventoryTxt = new HashMap<>();
            File file = new File(Environment.getExternalStorageDirectory(), "Inventory.txt");

            FileInputStream fis = new FileInputStream(file);
            DataInputStream dataIO = new DataInputStream(fis);

            StringBuffer sBuffer = new StringBuffer();
            String line = null;
            while ((line = dataIO.readLine()) != null) {
                String[] temp = line.split(",");
                String name = temp[0];
                int quantity = Integer.parseInt(temp[1]);
                inventoryTxt.put(name, quantity);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTxt(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),
                    "Inventory.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            Set<String> key = inventoryTxt.keySet();
            Iterator<String> keyIterate = key.iterator();
            while(keyIterate.hasNext()) {
                String name = keyIterate.next();
                Integer quantity = inventoryTxt.get(name);
                bw.write(name +"," +quantity +",");
                bw.write("\n");
                bw.flush();
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBurger(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                readTxt();
                for (String key : inventoryTxt.keySet()) {
                    if (key.equals("Burger")) {
                        int inventoryQ = inventoryTxt.get(key);
                        if (inventoryQ >= 50) {
                            inventoryQty.set(0, inventoryQty.get(0) + 50);
                            inventoryTxt.put(key, inventoryQ - 50);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "Burger increased successfully!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (inventoryQ < 50) {
                            inventoryQty.set(0, inventoryQty.get(0) + inventoryQ);
                            inventoryTxt.put(key, 0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
                writeTxt();
                databaseReference.child("Test").child("Inventory").setValue(inventoryQty);
            }
        });
        t.start();
    }

    public void addChicken(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                readTxt();
                for (String key : inventoryTxt.keySet()) {
                    if (key.equals("Chicken")) {
                        int inventoryQ = inventoryTxt.get(key);
                        if (inventoryQ >= 50) {
                            inventoryQty.set(1, inventoryQty.get(1) + 50);
                            inventoryTxt.put(key, inventoryQ - 50);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "Chicken increased successfully!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (inventoryQ < 50) {
                            inventoryQty.set(1, inventoryQty.get(1) + inventoryQ);
                            inventoryTxt.put(key, 0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
                writeTxt();
                databaseReference.child("Test").child("Inventory").setValue(inventoryQty);
            }
        });
        t.start();
    }

    public void addFries(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                readTxt();
                for (String key : inventoryTxt.keySet()) {
                    if (key.equals("French Fries")) {
                        int inventoryQ = inventoryTxt.get(key);
                        if (inventoryQ >= 50) {
                            inventoryQty.set(2, inventoryQty.get(2) + 50);
                            inventoryTxt.put(key, inventoryQ - 50);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "French Fries increased successfully!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (inventoryQ < 50) {
                            inventoryQty.set(2, inventoryQty.get(2) + inventoryQ);
                            inventoryTxt.put(key, 0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
                writeTxt();
                databaseReference.child("Test").child("Inventory").setValue(inventoryQty);
            }
        });
        t.start();
    }

    public void addOnionRings(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                readTxt();
                for (String key : inventoryTxt.keySet()) {
                    if (key.equals("Onion Rings")) {
                        int inventoryQ = inventoryTxt.get(key);
                        if (inventoryQ >= 50) {
                            inventoryQty.set(3, inventoryQty.get(3) + 50);
                            inventoryTxt.put(key, inventoryQ - 50);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "Onion Rings increased successfully!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (inventoryQ < 50) {
                            inventoryQty.set(3, inventoryQty.get(3) + inventoryQ);
                            inventoryTxt.put(key, 0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(InventoryActivity.this, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
                writeTxt();
                databaseReference.child("Test").child("Inventory").setValue(inventoryQty);
            }
        });
        t.start();
    }


}
