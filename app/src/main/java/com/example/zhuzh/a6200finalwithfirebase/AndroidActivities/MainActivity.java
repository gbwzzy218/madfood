package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zhuzh.a6200finalwithfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Date curTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickChef(View view) {
        curTime = new Date();
        int hour = curTime.getHours();
        if(hour < 11 || hour > 19){
            Toast.makeText(MainActivity.this, "We are not opening now!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent =  new Intent(MainActivity.this, ChefLoginActivity.class);
        startActivity(intent);
    }

    public void onClickCustomer(View view) {
        curTime = new Date();
        int hour = curTime.getHours();
        if(hour < 11 || hour > 19){
            Toast.makeText(MainActivity.this, "We are not opening now!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
        startActivity(intent);
    }


}
