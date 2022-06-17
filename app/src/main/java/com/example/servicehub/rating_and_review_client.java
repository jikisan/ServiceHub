package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class rating_and_review_client extends AppCompatActivity {

    private ImageView iv_clientPhoto;
    private TextView tv_back, tv_clientName;
    private RatingBar ratingBar;
    private EditText et_ratingMessage;
    private Button btn_ratingSubmit;

    private DatabaseReference userDatabase, projectDatabase, walletDb, listDatabase, ratingDatabase, bookingDatabase, orderDatabase;
    private String reviewCategory, serviceId, clientId, techId;
    private String clientFirstName, clientLastName, techFirstName, techLastName, bookingName,
            bookingImageUrl, orderImageUrl, itemName;
    private Double fundAmount, price;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_and_review_client);

        reviewCategory = getIntent().getStringExtra("category");
        serviceId = getIntent().getStringExtra("booking id");
        clientId = getIntent().getStringExtra("client id");
        techId = getIntent().getStringExtra("tech id");
        walletDb = FirebaseDatabase.getInstance().getReference("Wallets");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        setRef();
        generateData();
        clickListeners();
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int noOfStars = ratingBar.getNumStars();
                float getRating = ratingBar.getRating();

                new SweetAlertDialog(rating_and_review_client.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("SUBMIT RATING")
                        .setCancelText("Back")
                        .setConfirmButton("Finish and submit Rating?", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                final ProgressDialog progressDialog = new ProgressDialog(rating_and_review_client.this);
                                progressDialog.setTitle("Submit Rating...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRating);

                            }
                        })
                        .setContentText("Rate " + getRating + "/" + noOfStars + " \nfor " + clientFirstName + " " + clientLastName + "?")
                        .show();

            }


        });
    }

    private void generateData() {

        userDatabase.child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    String imageUrl = users.getImageUrl();
                    clientFirstName = users.firstName.substring(0, 1).toUpperCase() +
                            users.firstName.substring(1).toLowerCase();

                    clientLastName = users.lastName.substring(0, 1).toUpperCase() +
                            users.lastName.substring(1).toLowerCase();

                    if(!imageUrl.isEmpty())
                    {
                        Picasso.get()
                                .load(imageUrl)
                                .into(iv_clientPhoto);
                    }

                    tv_clientName.setText(clientFirstName + " " + clientLastName);

                    getTechSellerInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        walletDb.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallets wallets = snapshot.getValue(Wallets.class);

                if(wallets != null)
                {
                    fundAmount = wallets.fundAmount;

                }
                else

                    fundAmount = 0.00;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void getTechSellerInfo() {
        userDatabase.child(techId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    techFirstName = users.firstName.substring(0, 1).toUpperCase() +
                            users.firstName.substring(1).toLowerCase();

                    techLastName = users.lastName.substring(0, 1).toUpperCase();

                    getBookingOrderInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getBookingOrderInfo() {
        if(reviewCategory.equals("booking"))
        {
            bookingDatabase.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Booking booking = snapshot.getValue(Booking.class);
                        bookingName = booking.projName;
                        bookingImageUrl = booking.imageUrl;
                        String sp_price = booking.totalPrice;
                        price = Double.parseDouble(sp_price);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else if(reviewCategory.equals("order"))
        {
            orderDatabase.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Orders orders = snapshot.getValue(Orders.class);
                        itemName = orders.itemName;
                        orderImageUrl = orders.imageUrl;
                        String sp_ordersPrice = orders.getProdSubTotal();
                        String sp_orderQuantity = orders.getItemQuantity();
                        price = Double.parseDouble(sp_ordersPrice) * Double.parseDouble(sp_orderQuantity);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void submitRating(float getRating) {

        String ratingMessage = et_ratingMessage.getText().toString();
        String ratingByName = techFirstName + " " + techLastName;

        Ratings ratings = new Ratings(clientId, clientFirstName, clientLastName,
                techId, ratingByName, getRating, ratingMessage);

        ratingDatabase.push().setValue(ratings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                    updataBookingStatus();
                }

            }
        });

    }

    private void updataBookingStatus() {
        if(reviewCategory.equals("booking"))
        {
            String bookingStatus = "complete";
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("status", bookingStatus);

            bookingDatabase.child(serviceId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    generateNotification();

                }
            });

        }
        else if(reviewCategory.equals("order"))
        {
            orderDatabase.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Orders orders = snapshot.getValue(Orders.class);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getRef().removeValue();
                        generateNotification();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void generateNotification() {


        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");
        String sp_bookingName;
        String sp_notifTitle = null;
        String sp_notifMessage = null;

        if(reviewCategory.equals("booking"))
        {
            sp_bookingName = bookingName;
            sp_notifTitle = "Booking Completed";
            sp_notifMessage = "Booking status for BOOKING:" + sp_bookingName + " has change to COMPLETED by the technician.";
        }
        else if(reviewCategory.equals("order"))
        {
            sp_bookingName = itemName;
            sp_notifTitle = "Order Completed";
            sp_notifMessage = "Order status for ORDER:" + sp_bookingName + " has change to COMPLETED by the Seller.";
        }

        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();

        Notification notification = new Notification(bookingImageUrl, sp_notifTitle, sp_notifMessage, cartCreated, clientId);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    updateWallet();


                }

            }
        });
    }

    private void updateWallet() {
        Double percentRate = null;
        
        if(reviewCategory.equals("booking"))
        {
            percentRate = .15;
        }
        else if(reviewCategory.equals("order"))
        {
            percentRate = .05;
        }

        Double percentageFee = price * percentRate;

        Double totalFundAmount = fundAmount - percentageFee;

        Wallets wallets = new Wallets(userID, totalFundAmount);

        walletDb.child(userID).setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    if(reviewCategory.equals("booking"))
                    {
                        Intent intent = new Intent(rating_and_review_client.this, tech_dashboard.class);
                        startActivity(intent);
                        Toast.makeText(rating_and_review_client.this, "Rating Submitted", Toast.LENGTH_SHORT).show();
                    }
                    else if(reviewCategory.equals("order"))
                    {
                        Intent intent = new Intent(rating_and_review_client.this, seller_dashboard.class);
                        startActivity(intent);
                        Toast.makeText(rating_and_review_client.this, "Rating Submitted", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });

    }

    private void setRef() {

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        listDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        iv_clientPhoto = findViewById(R.id.iv_clientPhoto);
        tv_back = findViewById(R.id.tv_back);
        tv_clientName = findViewById(R.id.tv_clientName);
        ratingBar = findViewById(R.id.ratingBar);
        et_ratingMessage = findViewById(R.id.et_ratingMessage);
        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);
    }

}