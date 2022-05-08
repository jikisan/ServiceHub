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

import Adapter_and_fragments.AdapterBookingItem;
import Adapter_and_fragments.AdapterCartItem;

public class cart_page extends AppCompatActivity {

    private List<Cart> arr;
    private AdapterCartItem adapterCartItem;
    private DatabaseReference cartDatabase, listingDatbase, userDatabase;
    private String userID, custID;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, item3;
    private RecyclerView recyclerView_cart;
    private TextView tv_back;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");
        listingDatbase = FirebaseDatabase.getInstance().getReference("Listings");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setRef();
        generateRecyclerLayout();
        clickListeners();
        bottomNavTaskbar();
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        adapterCartItem.setOnItemClickListener(new AdapterCartItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final ProgressDialog progressDialog = new ProgressDialog(cart_page.this);
                progressDialog.setTitle("Loading...");
                progressDialog.show();

                arr.get(position);

                Query query = FirebaseDatabase.getInstance().getReference("Cart")
                        .orderByChild("listName")
                        .equalTo(arr.get(position).getListName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String listingID = dataSnapshot.getValue(Cart.class).listingID;
                            Intent intentListing = new Intent(cart_page.this, place_order_page.class);
                            intentListing.putExtra("Listing ID", listingID);
                            startActivity(intentListing);

                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(cart_page.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

                adapterCartItem.notifyItemChanged(position);
            }
        });

    }

    private void generateRecyclerLayout() {

        recyclerView_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_cart.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterCartItem = new AdapterCartItem(arr);
        recyclerView_cart.setAdapter(adapterCartItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = cartDatabase
                .orderByChild("custID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Cart cart = dataSnapshot.getValue(Cart.class);
                    arr.add(cart);
                }

                progressBar.setVisibility(View.GONE);
                adapterCartItem.notifyDataSetChanged();
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
                Intent intentMessageBtn = new Intent(cart_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(cart_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(cart_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(cart_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(cart_page.this, more_page.class);
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

        recyclerView_cart = findViewById(R.id.recyclerView_cart);

        tv_back = findViewById(R.id.tv_back);

        progressBar = findViewById(R.id.progressBar);
    }
}