package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class order_details_page extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn, iv_moreBtn,
            iv_deleteOrderBtn, iv_orderPhoto, iv_custLocation, iv_messageCust ;
    private TextView tv_orderName, tv_customerName, tv_orderPrice, tv_orderQuantity,
            tv_customerAddress, tv_custContactNum, tv_back;

    private String imageUriText, listingIdFromIntent;


    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private DatabaseReference listingDatabase;
    private StorageReference listingStorage;
    private StorageTask addTask;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingStorage = FirebaseStorage.getInstance().getReference("Listings").child(userID);
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings").child(userID);
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setRef();
        clickListeners();
        bottomNavTaskbar();
        generateDataValue();
        generateProfile();

    }

    private void setRef() {
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_deleteOrderBtn = findViewById(R.id.iv_deleteOrderBtn);
        iv_orderPhoto = findViewById(R.id.iv_orderPhoto);
        iv_custLocation = findViewById(R.id.iv_custLocation);
        iv_messageCust = findViewById(R.id.iv_messageCust);

        tv_orderName = findViewById(R.id.tv_orderName);
        tv_orderPrice = findViewById(R.id.tv_orderPrice);
        tv_orderQuantity = findViewById(R.id.tv_orderQuantity);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_customerAddress = findViewById(R.id.tv_customerAddress);
        tv_custContactNum = findViewById(R.id.tv_custContactNum);
        tv_back = findViewById(R.id.tv_back);

    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(order_details_page.this, seller_dashboard.class);
                startActivity(intent);
            }
        });
    }

    private void generateDataValue() {

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");

        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listingData = snapshot.getValue(Listings.class);

                if (listingData != null){
                    try {
                        imageUriText = listingData.getImageUrl();
                        String sp_listName = listingData.getListName();
                        String sp_listAddress = listingData.getListAddress();
                        String sp_listPrice = listingData.getListPrice();
                        String sp_listQuantity = listingData.getListQuantity();
                        String sp_listDesc = listingData.getListDesc();

                        Picasso.get()
                                .load(imageUriText)
                                .into(iv_orderPhoto);

                        tv_orderName.setText(sp_listName);
                        tv_orderPrice.setText("₱ " + sp_listPrice);
                        tv_orderQuantity.setText(sp_listQuantity);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(order_details_page.this, "Empty", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(order_details_page.this, "Empty", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void generateProfile() {
        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_num = userProfile.contactNum;

                    tv_customerName.setText(sp_fName + " " + sp_lName);
                    tv_custContactNum.setText(sp_num);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(order_details_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(order_details_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(order_details_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(order_details_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(order_details_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(order_details_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

}