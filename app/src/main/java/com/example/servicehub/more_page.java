package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class more_page extends AppCompatActivity {

    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;

    TextView tv_editProfile, tv_changePassword, tv_contactUs, tv_aboutUs, tv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_page);

        setRef();
        buttonNav();
        bottomNavTaskbar();


    }

    private void buttonNav() {
        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditProfile = new Intent(more_page.this, edit_profile_page.class);
                startActivity(intentEditProfile);
            }
        }); // end of edit profile button

        tv_contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentContactUs = new Intent(more_page.this, contact_us_page.class);
                startActivity(intentContactUs);
            }
        }); // end of contact us button

        tv_aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAboutUs = new Intent(more_page.this, about_us_page.class);
                startActivity(intentAboutUs);
            }
        }); // end of about us button

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login_page.class));
                finish();
            }
        }); // end of about us button


    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(more_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(more_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(more_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(more_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(more_page.this, more_page.class);
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
        tv_editProfile = findViewById(R.id.tv_editProfile);
        tv_changePassword = findViewById(R.id.tv_changePassword);
        tv_contactUs = findViewById(R.id.tv_contactUs);
        tv_aboutUs = findViewById(R.id.tv_aboutUs);
        tv_logout = findViewById(R.id.tv_logout);
    }
}