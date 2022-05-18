package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class homepage extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private String userID;
    private ProgressBar progressBar;

    private TextView tv_bannerName, tv_userRating,  btn_installation, btn_repair, btn_cleaning, btn_marketplace;
    private ImageView iv_userPic;
    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private EditText et_search;
    private View layout_myBookings, layout_myOrders, layout_favorites, layout_myCart, layout_promos, layout_history;
    private RatingBar rb_userRating;

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
        clickListeners();

    }

    private void clickListeners() {

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSearch = new Intent(homepage.this, search_page.class);
                startActivity(intentSearch);
            }
        });

        btn_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentInsallation = new Intent(homepage.this, installation_page.class);
                intentInsallation.putExtra("Category", "Installation");
                intentInsallation.putExtra("tabNum", 0);
                startActivity(intentInsallation);
            }
        });

        btn_repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearbyTech = new Intent(homepage.this, installation_page.class);
                intentNearbyTech.putExtra("Category", "Repair");
                intentNearbyTech.putExtra("tabNum", 1);
                startActivity(intentNearbyTech);
            }
        });

        btn_cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearbyTech = new Intent(homepage.this, installation_page.class);
                intentNearbyTech.putExtra("Category", "Cleaning");
                intentNearbyTech.putExtra("tabNum", 2);
                startActivity(intentNearbyTech);
            }
        });

        btn_marketplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMarketplace = new Intent(homepage.this, installation_page.class);
                intentMarketplace.putExtra("Category", "Marketplace");
                intentMarketplace.putExtra("tabNum", 3);
                startActivity(intentMarketplace);
            }
        });

        layout_myBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, my_booking_page.class);
                startActivity(intent);
            }
        });

        layout_myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homepage.this, my_orders_page.class);
                startActivity(intent);
            }
        });

        layout_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, favorite_page.class);
                startActivity(intent);
            }
        });

        layout_myCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCartBtn = new Intent(homepage.this, cart_page.class);
                startActivity(intentCartBtn);
            }
        });

        layout_promos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCartBtn = new Intent(homepage.this, promo_page.class);
                startActivity(intentCartBtn);
            }
        });

        layout_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCartBtn = new Intent(homepage.this, history_page.class);
                startActivity(intentCartBtn);
            }
        });

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
        tv_userRating = findViewById(R.id.tv_userRating);

        et_search = findViewById(R.id.et_search);

        rb_userRating = findViewById(R.id.rb_userRating);

        btn_installation = findViewById(R.id.btn_installation);
        btn_repair = findViewById(R.id.btn_repair);
        btn_cleaning = findViewById(R.id.btn_cleaning);
        btn_marketplace = findViewById(R.id.btn_marketplace);

        iv_userPic = findViewById(R.id.iv_userPic);
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        progressBar = findViewById(R.id.progressBar);

        layout_myBookings = findViewById(R.id.layout_myBookings);
        layout_myOrders = findViewById(R.id.layout_myOrders);
        layout_favorites = findViewById(R.id.layout_favorites);
        layout_myCart = findViewById(R.id.layout_myCart);
        layout_promos = findViewById(R.id.layout_promos);
        layout_history = findViewById(R.id.layout_history);

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
                    String sp_fullName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase()+",";
//                            + " " + sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_bannerName.setText(sp_fullName);

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_userPic);
                    }

                    generateRatings();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(homepage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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