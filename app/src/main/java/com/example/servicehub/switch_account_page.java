package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class switch_account_page extends AppCompatActivity {

    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    TextView tv_techWelcome, tv_sellerWelcome, tv_techMessage, tv_sellerMessage;
    Button btn_tech, btn_seller, btn_techApply, btn_sellerApply, btn_cancelTechApplication, btn_cancelSellerApplication;
    CardView cardView1, cardView2, cardViewPendingSeller, cardViewPendingTech;
    ProgressBar progressBar;

    DatabaseReference techApplicationDatabase;
    private String userID;
    String techAcctKey = "";

    boolean isTechPending, isTechApproved, isSellerPending, isSellerApproved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_account_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        techApplicationDatabase = FirebaseDatabase.getInstance().getReference("Technician Applicants").child(userID);

        setRef();
        getKey();
        clickListener();
        bottomNavTaskbar();


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
        btn_cancelSellerApplication = findViewById(R.id.btn_cancelSellerApplication);
        btn_cancelTechApplication = findViewById(R.id.btn_cancelTechApplication);

        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardViewPendingSeller = findViewById(R.id.cardViewPendingSeller);
        cardViewPendingTech = findViewById(R.id.cardViewPendingTech);

        progressBar = findViewById(R.id.progressBar);
    }

    private void getKey() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Submitting Application");
//        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseDatabase.getInstance().getReference("Technician Applicants").child(userID)
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    techAcctKey = dataSnapshot.getKey().toString();

                }
                getDataFromDB();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getDataFromDB() {

        techApplicationDatabase.child(techAcctKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tech_application tech_application_data = snapshot.getValue(Tech_application.class);

                if(tech_application_data != null){
                    try
                    {
                        boolean sp_isTechPending = tech_application_data.isPending();
                        boolean sp_isTechApproved = tech_application_data.isApproved();

                        if(!sp_isTechApproved && !sp_isTechPending){
                            cardView2.setVisibility(View.VISIBLE);
                            cardViewPendingTech.setVisibility(View.INVISIBLE);
                            btn_techApply.setVisibility(View.VISIBLE);
                            btn_tech.setVisibility(View.INVISIBLE);
                        }
                        else if(sp_isTechPending && !sp_isTechApproved){
                            cardView2.setVisibility(View.INVISIBLE);
                            cardViewPendingTech.setVisibility(View.VISIBLE);
                        }
                        else if(sp_isTechApproved && !sp_isTechPending){
                            cardView2.setVisibility(View.VISIBLE);
                            cardViewPendingTech.setVisibility(View.INVISIBLE);
                            btn_techApply.setVisibility(View.INVISIBLE);
                            btn_tech.setVisibility(View.VISIBLE);
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    cardView2.setVisibility(View.VISIBLE);
                    cardViewPendingTech.setVisibility(View.INVISIBLE);
                    btn_techApply.setVisibility(View.VISIBLE);
                    btn_tech.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(switch_account_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

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
                Intent intent = new Intent(switch_account_page.this, tech_application_page.class);
                startActivity(intent);

               // Toast.makeText(switch_account_page.this, "Pending: " + isTechPending, Toast.LENGTH_SHORT).show();
               // Toast.makeText(switch_account_page.this, "Approved: " + isTechApproved, Toast.LENGTH_SHORT).show();
                //Toast.makeText(switch_account_page.this, fname + "" + lname, Toast.LENGTH_SHORT).show();

            }
        }); // end of seller button

        btn_sellerApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSellerSelect = new Intent(switch_account_page.this, seller_dashboard.class);
                startActivity(intentSellerSelect);
            }
        }); // end of seller button

        btn_cancelTechApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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


}