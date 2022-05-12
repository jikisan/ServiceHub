package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import Adapter_and_fragments.fragmentAdapterInstallation;

public class installation_page extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private TabLayout tabLayout;
    private ViewPager2 vp_viewPager2;
    private fragmentAdapterInstallation adapter;
    private ProgressBar progressBar;
    private int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.installation_page);

        currentTab = getIntent().getIntExtra("tabNum", 0);

        setRef();
        //selectPage();
        generateTabLayout();
        bottomNavTaskbar();
    }

    private void generateTabLayout() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new fragmentAdapterInstallation(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

        tabLayout.addTab(tabLayout.newTab().setText("Installation"));
        tabLayout.addTab(tabLayout.newTab().setText("Repair"));
        tabLayout.addTab(tabLayout.newTab().setText("Cleaning"));
        tabLayout.addTab(tabLayout.newTab().setText("Marketplace"));

        tabLayout.setScrollPosition(currentTab,0f,true);
        vp_viewPager2.setCurrentItem(currentTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               vp_viewPager2.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(installation_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(installation_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(installation_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(installation_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(installation_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {

        progressBar = findViewById(R.id.progressBar);

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        tabLayout = findViewById(R.id.tab_layout);

        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        TextView tv_back = findViewById(R.id.tv_back);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


}