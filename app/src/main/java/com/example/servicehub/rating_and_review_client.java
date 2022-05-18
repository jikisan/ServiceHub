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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

    private DatabaseReference userDatabase, projectDatabase, listDatabase, ratingDatabase, bookingDatabase;
    private String reviewCategory, serviceId, clientId,techId;
    private String clientFirstName, clientLastName, techFirstName, techLastName, bookingName, bookingImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_and_review_client);

        setRef();
        generateData();
        clickListeners();
    }

    private void generateData() {

        reviewCategory = getIntent().getStringExtra("category");
        serviceId = getIntent().getStringExtra("booking id");
        clientId = getIntent().getStringExtra("client id");
        techId = getIntent().getStringExtra("tech id");

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

                    Picasso.get()
                            .load(imageUrl)
                            .into(iv_clientPhoto);

                    tv_clientName.setText(clientFirstName + " " + clientLastName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userDatabase.child(techId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    techFirstName = users.firstName.substring(0, 1).toUpperCase() +
                            users.firstName.substring(1).toLowerCase();

                    techLastName = users.lastName.substring(0, 1).toUpperCase();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookingDatabase.child(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Booking booking = snapshot.getValue(Booking.class);
                    bookingName = booking.projName;
                    bookingImageUrl = booking.imageUrl;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(rating_and_review_client.this)
                        .setTitle("Submit Rating?")
                        .setMessage("Rate " + getRating + "/" + noOfStars + " \nfor " + clientFirstName + " " + clientLastName + "?")
                        .setCancelable(true)
                        .setPositiveButton("Submit Rating", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                final ProgressDialog progressDialog = new ProgressDialog(rating_and_review_client.this);
                                progressDialog.setTitle("Submit Rating...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                submitRating(getRating);

                            }
                        })
                        .setNegativeButton("Back", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .build();

                // Show Dialog
                mBottomSheetDialog.show();

            }


        });
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

    private void generateNotification() {

        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");

        String sp_bookingName = bookingName;
        String sp_notifTitle = "Booking Completed";
        String sp_notifMessage = "Booking: " + sp_bookingName + " has change to completed by the technician.";

        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();

        Notification notification = new Notification(bookingImageUrl, sp_notifTitle, sp_notifMessage, cartCreated, clientId);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(rating_and_review_client.this, tech_dashboard.class);
                    startActivity(intent);
                    Toast.makeText(rating_and_review_client.this, "Rating Submitted", Toast.LENGTH_SHORT).show();


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

        iv_clientPhoto = findViewById(R.id.iv_clientPhoto);
        tv_back = findViewById(R.id.tv_back);
        tv_clientName = findViewById(R.id.tv_clientName);
        ratingBar = findViewById(R.id.ratingBar);
        et_ratingMessage = findViewById(R.id.et_ratingMessage);
        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);
    }
}