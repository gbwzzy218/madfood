package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhuzh.a6200finalwithfirebase.R;

public class ChefLoginActivity extends AppCompatActivity {


    EditText editPass = null;
    String passcode;
    String chefPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chefPass = "6666";
        setContentView(R.layout.activity_chef_login);
        editPass = (EditText)findViewById(R.id.editPass);
    }

    public void onClickChefLogin(View view) {
        passcode = editPass.getText().toString();

        if(passcode.equals("")){
            Toast.makeText(getApplicationContext(),
                    "Please Enter Your Password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (passcode == null || !passcode.equals(chefPass)){
            Toast.makeText(getApplicationContext(),
                    "Invalid Password!", Toast.LENGTH_LONG).show();
            editPass.setText("");
        } else {
            Intent intent = new Intent(ChefLoginActivity.this, ChefActivity.class);
            startActivity(intent);
        }
    }
}
