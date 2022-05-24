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

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterRatingsItem;

public class my_ratings_page extends AppCompatActivity {

    private List<Ratings> arr;
    private AdapterRatingsItem adapterRatingsItem;
    private DatabaseReference ratingsDatabase;
    private String userID, custID;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, item3;
    private RecyclerView recyclerView_ratings;
    private TextView tv_back;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ratings_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ratingsDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        userID = user.getUid();

        setRef();
        generateRecyclerLayout();
        bottomNavTaskbar();
    }


    private void generateRecyclerLayout() {

        recyclerView_ratings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_ratings.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterRatingsItem = new AdapterRatingsItem(arr);
        recyclerView_ratings.setAdapter(adapterRatingsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = ratingsDatabase
                .orderByChild("ratingOfId")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Ratings ratings = dataSnapshot.getValue(Ratings.class);
                    arr.add(ratings);
                }

                progressBar.setVisibility(View.GONE);
                adapterRatingsItem.notifyDataSetChanged();
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
                Intent intentMessageBtn = new Intent(my_ratings_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(my_ratings_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(my_ratings_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(my_ratings_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(my_ratings_page.this, more_page.class);
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

        recyclerView_ratings = findViewById(R.id.recyclerView_ratings);

        tv_back = findViewById(R.id.tv_back);

        progressBar = findViewById(R.id.progressBar);
    }
}