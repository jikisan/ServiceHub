package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class switch_account_page extends AppCompatActivity {

    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;

    TextView tv_techWelcome, tv_sellerWelcome, tv_techMessage, tv_sellerMessage;

    Button btn_tech, btn_seller, btn_techApply, btn_sellerApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_account_page);

        setRef();
        bottomNavTaskbar();
        clickListener();
    }

    private void clickListener() {

        btn_tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTechSelect = new Intent(switch_account_page.this, tech_dashboard.class);
                startActivity(intentTechSelect);
            }
        }); // end of tech button

        btn_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSellerSelect = new Intent(switch_account_page.this, seller_dashboard.class);
                startActivity(intentSellerSelect);
            }
        }); // end of seller button

        btn_techApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSellerSelect = new Intent(switch_account_page.this, tech_application_page.class);
                startActivity(intentSellerSelect);
            }
        }); // end of seller button

        btn_sellerApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSellerSelect = new Intent(switch_account_page.this, seller_dashboard.class);
                startActivity(intentSellerSelect);
            }
        }); // end of seller button


    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(switch_account_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(switch_account_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(switch_account_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(switch_account_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(switch_account_page.this, more_page.class);
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

        tv_techWelcome = findViewById(R.id.tv_techWelcome);
        tv_sellerWelcome = findViewById(R.id.tv_sellerWelcome);
        tv_techMessage = findViewById(R.id.tv_techMessage);
        tv_sellerMessage = findViewById(R.id.tv_sellerMessage);



        btn_tech = findViewById(R.id.btn_tech);
        btn_seller = findViewById(R.id.btn_seller);
        btn_sellerApply = findViewById(R.id.btn_sellerApply);
        btn_techApply = findViewById(R.id.btn_techApply);


    }
}