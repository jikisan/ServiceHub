package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Adapter_and_fragments.fragmentAdapterListings;

public class seller_dashboard extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userDatabase, sellerDatabase, walletDb;
    private String userID;
    private TabLayout tabLayout;
    private ViewPager2 vp_viewPager2;
    private fragmentAdapterListings adapter;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_back, iv_userPic, iv_addFund;
    private TextView tv_bannerName, tv_back, tv_fundBalance;
    private Button btn_addListing;
    private ProgressBar progressBar;
    private int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_dashboard);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        sellerDatabase = FirebaseDatabase.getInstance().getReference("Seller Applicants");
        walletDb = FirebaseDatabase.getInstance().getReference("Wallets");
        userID = user.getUid();

        setRef();
        generateTabLayout();
        clickListener();
        getSellerInfo();
        getWalletInfo();
        bottomNavTaskbar();
    }

    private void getWalletInfo() {

        walletDb.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallets wallets = snapshot.getValue(Wallets.class);

                if(wallets != null)
                {
                    Double fundAmount = wallets.fundAmount;
                    tv_fundBalance.setText(fundAmount + "");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_userPic = findViewById(R.id.iv_userPic);
        iv_addFund = findViewById(R.id.iv_addFund);

        btn_addListing = findViewById(R.id.btn_addListing);

        tv_bannerName = findViewById(R.id.tv_bannerName);
        tv_back = findViewById(R.id.tv_back);
        tv_fundBalance = findViewById(R.id.tv_fundBalance);

        tabLayout = findViewById(R.id.tab_layout);

        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

        progressBar = findViewById(R.id.progressBar);

    }

    private void generateTabLayout() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new fragmentAdapterListings(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("My Listings"));
        tabLayout.addTab(tabLayout.newTab().setText("Active Orders"));

        currentTab = getIntent().getIntExtra("currentTab", 0);
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

    private void getSellerInfo() {


        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_imageUrl = userProfile.imageUrl;

                    String firstName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase();
                    String lastName = sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_bannerName.setText(firstName + " " + lastName);
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
                Toast.makeText(seller_dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void clickListener() {

        btn_addListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddListing = new Intent(seller_dashboard.this, add_listing_page.class);
                startActivity(intentAddListing);
            }
        }); // end of add listing button

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(seller_dashboard.this, switch_account_page.class);
                startActivity(intent);
            }
        });

        iv_addFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(seller_dashboard.this, add_funds_page.class);
                intent.putExtra("category", "seller");
                startActivity(intent);
            }
        });

    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(seller_dashboard.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(seller_dashboard.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(seller_dashboard.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(seller_dashboard.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(seller_dashboard.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }
}