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
import Adapter_and_fragments.AdapterMyBookings;

public class my_booking_page extends AppCompatActivity {

    private List<Booking> arr;
    private AdapterMyBookings adapterMyBookings;
    private DatabaseReference cartDatabase;
    private String userID, bookingID;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, item3;
    private RecyclerView recyclerView_myBookings;
    private TextView tv_back;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_booking_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cartDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        userID = user.getUid();

        setRef();
        generateRecyclerView();
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

        adapterMyBookings.setOnItemClickListener(new AdapterMyBookings.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                final ProgressDialog progressDialog = new ProgressDialog(my_booking_page.this);
                progressDialog.setTitle("Loading...");
                progressDialog.show();

                arr.get(position);

                Query query = FirebaseDatabase.getInstance().getReference("Bookings")
                        .orderByChild("projName")
                        .equalTo(arr.get(position).getProjName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Toast.makeText(my_booking_page.this, "Coming soon!", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(my_booking_page.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

                adapterMyBookings.notifyItemChanged(position);


            }
        });
    }

    private void generateRecyclerView() {

        recyclerView_myBookings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_myBookings.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterMyBookings = new AdapterMyBookings(arr);
        recyclerView_myBookings.setAdapter(adapterMyBookings);

        Query query = cartDatabase
                .orderByChild("custID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Booking booking = dataSnapshot.getValue(Booking.class);
                    arr.add(booking);

                }

                progressBar.setVisibility(View.GONE);
                adapterMyBookings.notifyDataSetChanged();
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
                Intent intentMessageBtn = new Intent(my_booking_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(my_booking_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(my_booking_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(my_booking_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(my_booking_page.this, more_page.class);
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
        tv_back = findViewById(R.id.tv_back);

        recyclerView_myBookings = findViewById(R.id.recyclerView_myBookings);

        progressBar = findViewById(R.id.progressBar);
    }
}