package com.example.zhuzh.a6200finalwithfirebase.AndroidActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhuzh.a6200finalwithfirebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerOrderActivityDetail extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    String[] Item_Sequence = {"1","2","3","4"};
    String[] Item_Name = new String[4];
    String[] Item_Qty = new String[4];
    String[] Item_Single_item_price = new String[4];

    //String selectedOrder;

    int CurrentLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);

        //selectedOrder = getIntent().getStringExtra("OrderId");

        String Order_ID = getIntent().getStringExtra("Order_Id");
        String Customer_Name = getIntent().getStringExtra("Customer_Name");
        String Total_Price = getIntent().getStringExtra("Total_Price");
        String Order_Date = getIntent().getStringExtra("Order_Date");
        String Order_State = getIntent().getStringExtra("Order_State");
        String Order_Tax = getIntent().getStringExtra("Order_Tax");

        Item_Name = getIntent().getStringArrayExtra("Item_Name");
        Item_Qty = getIntent().getStringArrayExtra("Item_Qty");
        Item_Single_item_price = getIntent().getStringArrayExtra("Item_Single_item_price");

        CurrentLength = getIntent().getIntExtra("ItemLength",0);

        TextView textView_OrderID = (TextView)findViewById(R.id.Order_Num);
        TextView textView_CustName = (TextView)findViewById(R.id.Cust_Name);
        TextView textView_TotalPrice = (TextView)findViewById(R.id.total_Paid);
        TextView textView_OrderDate = (TextView)findViewById(R.id.Order_Date);
        TextView textView_OrderState = (TextView)findViewById(R.id.Order_State);
        TextView textView_OrderTax = (TextView)findViewById(R.id.Order_Tax);

        textView_OrderID.setText(Order_ID);
        textView_CustName.setText(Customer_Name);
        textView_TotalPrice.setText(Total_Price);
        textView_OrderDate.setText(Order_Date);
        textView_OrderState.setText(Order_State);

        ListView listView = (ListView)findViewById(R.id.order_List);
        ItemAdapter itemAdapter = new ItemAdapter ();
        listView.setAdapter(itemAdapter);

    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount (){
            return CurrentLength;
        }

        @Override
        public Object getItem (int i){
            return null;
        }

        @Override
        public long getItemId (int i){
            return 0;
        }

        @Override
        public View getView (int i, View view, ViewGroup viewGroup){
            view = getLayoutInflater().inflate(R.layout.customer_item_single,null);

            TextView textView_sequence = (TextView)view.findViewById(R.id.order_item_Sequence);
            TextView textView_itemName = (TextView)view.findViewById(R.id.order_item_Name);
            TextView textView_itemPrice = (TextView)view.findViewById(R.id.order_item_Total_Price);

            textView_sequence.setText(Item_Sequence[i]);
            textView_itemName.setText(Item_Name[i]);
            textView_itemPrice.setText(Item_Single_item_price[i]);

            return view;
        }

    }
}
