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
import Adapter_and_fragments.AdapterFavoriteItem;

public class favorite_page extends AppCompatActivity {

    private List<Favorites> arr;
    private AdapterFavoriteItem favoriteItem;
    private DatabaseReference favoriteDatabase, projectDatbase, userDatabase;
    private String userID, custID;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, item3;
    private RecyclerView recyclerView_favorites;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        favoriteDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        projectDatbase = FirebaseDatabase.getInstance().getReference("Projects");
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

        favoriteItem.setOnItemClickListener(new AdapterFavoriteItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final ProgressDialog progressDialog = new ProgressDialog(favorite_page.this);
                progressDialog.setTitle("Loading...");
                progressDialog.show();

                arr.get(position);

                Query query = FirebaseDatabase.getInstance().getReference("Favorites")
                        .orderByChild("projName")
                        .equalTo(arr.get(position).getProjName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String projectID = dataSnapshot.getValue(Favorites.class).projID;
                            Intent intentListing = new Intent(favorite_page.this, booking_page.class);
                            intentListing.putExtra("Project ID", projectID);
                            startActivity(intentListing);

                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(favorite_page.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

                favoriteItem.notifyItemChanged(position);

            }
        });



    }

    private void generateRecyclerLayout() {

        recyclerView_favorites.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_favorites.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        favoriteItem = new AdapterFavoriteItem(arr);
        recyclerView_favorites.setAdapter(favoriteItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = favoriteDatabase
                .orderByChild("custID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Favorites favorites = dataSnapshot.getValue(Favorites.class);
                    arr.add(favorites);
                }

                favoriteItem.notifyDataSetChanged();
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
                Intent intentMessageBtn = new Intent(favorite_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(favorite_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(favorite_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(favorite_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(favorite_page.this, more_page.class);
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

        recyclerView_favorites = findViewById(R.id.recyclerView_favorites);

        tv_back = findViewById(R.id.tv_back);
    }
}