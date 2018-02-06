package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhuzh.a6200finalwithfirebase.JavaPackage.Customer;
import com.example.zhuzh.a6200finalwithfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerLoginActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "UserNamePassword";


    private EditText mEmailField;
    private EditText mPasswordField;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<Customer> customers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Test").child("Users").addValueEventListener(new ValueEventListener() {
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
        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

        // [START initialize_auth]
        // [END initialize_auth]
    }

    // [START on_start_check_user]




    private void signIn(String email, String password) {

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        for(Customer c: customers){
            if (email.equals(c.getName()) && password.equals(c.getPass())){
                Intent i = new Intent(CustomerLoginActivity.this, CustomerActivity.class);
                i.putExtra("User Name",c.getName());
                startActivity(i);
                mEmailField.setText("");
                mPasswordField.setText("");
                return;
            }else{
                continue;
            }
        }
        Toast.makeText(CustomerLoginActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        mEmailField.setText("");
        mPasswordField.setText("");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    hideProgressDialog();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }





    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());


    }
}
