package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class homepage extends AppCompatActivity {

    TextView tv_searchBar;
    TextView tv_bannerName, tv_userRate, tv_bookingCount, tv_orderCount;
    ImageView iv_cartButton, iv_userPic, iv_installation, iv_cleaning, iv_repair, iv_marketplace,
            iv_nearbyTech, iv_promo;
    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        setRef();
        bottomNavTaskbar();
        buttonsActivity();

        String username = getIntent().getStringExtra("keyname");
        tv_bannerName.setText(username);

    }

    private void buttonsActivity() {

        tv_searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch = new Intent(homepage.this, search_page.class);
                startActivity(intentSearch);
            }
        }); //end of search button

        iv_cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCartBtn = new Intent(homepage.this, cart_page.class);
                startActivity(intentCartBtn);
            }
        }); // end of cart button

        iv_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInsallation = new Intent(homepage.this, installation_page.class);
                startActivity(intentInsallation);
            }
        }); // end of installation button

        iv_marketplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMarketplace = new Intent(homepage.this, marketplace_page.class);
                startActivity(intentMarketplace);
            }
        }); // end of marketplace button

        iv_nearbyTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearbyTech = new Intent(homepage.this, nearby_tech_page.class);
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

        tv_searchBar = findViewById(R.id.tv_searchBar);
        tv_bannerName = findViewById(R.id.tv_bannerName);
        tv_userRate = findViewById(R.id.tv_userRate);
        tv_bookingCount = findViewById(R.id.tv_bookingCount);
        tv_orderCount = findViewById(R.id.tv_orderCount);
        iv_cartButton = findViewById(R.id.iv_cartButton);
        iv_userPic = findViewById(R.id.iv_userPic);
        iv_installation = findViewById(R.id.iv_installation);
        iv_cleaning = findViewById(R.id.iv_cleaning);
        iv_repair = findViewById(R.id.iv_repair);
        iv_marketplace = findViewById(R.id.iv_marketplace);
        iv_nearbyTech = findViewById(R.id.iv_nearbyTech);
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
    }
}