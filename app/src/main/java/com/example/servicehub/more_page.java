package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class more_page extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID;

    private ProgressBar progressBar;
    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_userPhoto;
    private TextView tv_editProfile, tv_changePassword, tv_contactUs, tv_aboutUs, tv_logout,
            tv_bannerName, tv_userRating, tv_privacyPolicy, tv_myAddress, tv_back, tv_myRatings;
    private RatingBar rb_userRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_page);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        setRef();

        progressBar.setVisibility(View.VISIBLE);
        clickListeners();
        generateProfile();
        generateRatings();
        bottomNavTaskbar();

    }

    private void clickListeners() {

        tv_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(more_page.this, privacy_and_policy_page.class);
                startActivity(intent);
            }
        });

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
                new SweetAlertDialog(more_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!.")
                        .setCancelText("Cancel")
                        .setConfirmButton("Log Out", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(), intro_logo.class));
                                finish();
                            }
                        })
                        .setContentText("Are you sure you want to logout?")
                        .show();

            }
        }); // end of about us button

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(more_page.this, change_password_page.class);
                intent.putExtra("User Email", user.getEmail());
                startActivity(intent);
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(more_page.this, homepage.class);
                startActivity(intent);
            }
        });

        tv_myAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(more_page.this, address_page.class);
                startActivity(intent);
            }
        });

        tv_myRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(more_page.this, my_ratings_page.class);
                startActivity(intent);
            }
        });

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
        iv_userPhoto = findViewById(R.id.iv_userPhoto);

        tv_editProfile = findViewById(R.id.tv_editProfile);
        tv_changePassword = findViewById(R.id.tv_changePassword);
        tv_contactUs = findViewById(R.id.tv_contactUs);
        tv_aboutUs = findViewById(R.id.tv_aboutUs);
        tv_logout = findViewById(R.id.tv_logout);
        tv_bannerName = findViewById(R.id.tv_bannerName);
        tv_myAddress = findViewById(R.id.tv_myAddress);
        tv_privacyPolicy = findViewById(R.id.tv_privacyPolicy);
        tv_back = findViewById(R.id.tv_back);
        tv_userRating = findViewById(R.id.tv_userRating);
        tv_myRatings = findViewById(R.id.tv_myRatings);

        progressBar = findViewById(R.id.progressBar);

        rb_userRating = findViewById(R.id.rb_userRating);
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
                                .into(iv_userPhoto);
                    }

                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(more_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void generateRatings() {
        DatabaseReference ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        Query query = ratingDatabase
                .orderByChild("ratingOfId")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Ratings ratings = dataSnapshot.getValue(Ratings.class);
                    tempRatingValue = ratings.ratingValue;
                    totalRating = totalRating + tempRatingValue;
                    counter++;

                }

                averageRating = totalRating / counter;
                String ratingCounter = "(" + String.valueOf(counter) + ")";
                tv_userRating.setText(ratingCounter);
                rb_userRating.setRating((float) averageRating);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressBar.setVisibility(View.GONE);
    }

}