package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class homepage extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID;
    private ProgressBar progressBar;

    private TextView tv_bannerName, tv_userRate,  btn_installation, btn_repair, btn_cleaning, btn_marketplace;
    private ImageView iv_cart, iv_userPic;
    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setRef();
        generateProfile();
        bottomNavTaskbar();
        buttonsActivity();

    }

    private void buttonsActivity() {

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch = new Intent(homepage.this, search_page.class);
                startActivity(intentSearch);
            }
        }); //end of search button

        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCartBtn = new Intent(homepage.this, cart_page.class);
                startActivity(intentCartBtn);
            }
        }); // end of cart button

        btn_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentInsallation = new Intent(homepage.this, installation_page.class);
                intentInsallation.putExtra("Category", "Installation");
                startActivity(intentInsallation);
            }
        }); // end of installation button

        btn_marketplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMarketplace = new Intent(homepage.this, installation_page.class);
                intentMarketplace.putExtra("Category", "Marketplace");
                startActivity(intentMarketplace);
            }
        }); // end of marketplace button

        btn_cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearbyTech = new Intent(homepage.this, installation_page.class);
                intentNearbyTech.putExtra("Category", "Cleaning");
                startActivity(intentNearbyTech);
            }
        }); // end of marketplace button

        btn_repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearbyTech = new Intent(homepage.this, installation_page.class);
                intentNearbyTech.putExtra("Category", "Repair");
                startActivity(intentNearbyTech);
            }
        }); // end of marketplace button

    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(homepage.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(homepage.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(homepage.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(homepage.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(homepage.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {

        tv_bannerName = findViewById(R.id.tv_bannerName);

        et_search = findViewById(R.id.et_search);

        btn_installation = findViewById(R.id.btn_installation);
        btn_repair = findViewById(R.id.btn_repair);
        btn_cleaning = findViewById(R.id.btn_cleaning);
        btn_marketplace = findViewById(R.id.btn_marketplace);

        iv_cart = findViewById(R.id.iv_cart);
        iv_userPic = findViewById(R.id.iv_userPic);
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        progressBar = findViewById(R.id.progressBar);

    }

    private void generateProfile() {


        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_imageUrl = userProfile.imageUrl;
                    String sp_fullName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase()
                            + " " + sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_bannerName.setText(sp_fullName);

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_userPic);
                    }

                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(homepage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

}