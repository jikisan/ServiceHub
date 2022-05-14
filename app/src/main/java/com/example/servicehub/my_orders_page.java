package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter_and_fragments.AdapterMyBookings;
import Adapter_and_fragments.AdapterMyOrderItem;

public class my_orders_page extends AppCompatActivity {

    private List<Orders> arr;
    private AdapterMyOrderItem adapterMyOrderItem;
    private DatabaseReference orderDatabase;
    private String userID, orderID;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, item3;
    private RecyclerView recyclerView_myOrders;
    private TextView tv_back;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_orders_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        userID = user.getUid();

        setRef();
        generateRecyclerView();
        clickListeners();
        bottomNavTaskbar();
    }

    private void clickListeners() {

        adapterMyOrderItem.setOnItemClickListener(new AdapterMyOrderItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                final ProgressDialog progressDialog = new ProgressDialog(my_orders_page.this);
                progressDialog.setTitle("Loading...");
                progressDialog.show();

                arr.get(position);

                Query query = FirebaseDatabase.getInstance().getReference("Orders")
                        .orderByChild("itemName")
                        .equalTo(arr.get(position).getItemName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            orderID = dataSnapshot.getKey().toString();
                            Intent intent = new Intent(my_orders_page.this, client_order_details.class);
                            intent.putExtra("Order ID", orderID);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(my_orders_page.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

                adapterMyOrderItem.notifyItemChanged(position);

            }
        });

    }

    private void generateRecyclerView() {

        recyclerView_myOrders.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_myOrders.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterMyOrderItem = new AdapterMyOrderItem(arr);
        recyclerView_myOrders.setAdapter(adapterMyOrderItem);


        orderDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Orders orders = dataSnapshot.getValue(Orders.class);

                    if(orders.getCustID().equals(userID))
                    {
                        arr.add(orders);
                    }
                }

                progressBar.setVisibility(View.GONE);
                adapterMyOrderItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(my_orders_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(my_orders_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(my_orders_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(my_orders_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(my_orders_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        item3 = findViewById(R.id.item3);

        recyclerView_myOrders = findViewById(R.id.recyclerView_myOrders);

        tv_back = findViewById(R.id.tv_back);

        progressBar = findViewById(R.id.progressBar);
    }
}