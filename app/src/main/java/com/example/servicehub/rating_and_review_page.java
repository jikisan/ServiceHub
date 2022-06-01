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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class rating_and_review_page extends AppCompatActivity {

    private ImageView iv_ratingPhoto;
    private TextView tv_back, tv_ratingName;
    private RatingBar ratingBar;
    private EditText et_ratingMessage;
    private Button btn_ratingSubmit;

    private DatabaseReference bookingDatabase, ratingDatabase, projDatabase;
    private String reviewCategory, bookingIdFromIntent, clientId, techId, projId;
    private String ratedByFirstName, ratedByLastName, bookingName, bookingImageUrl, ratingMessage,
            ratingByName, clientLastName;

    private int counter = 0;
    private double totalRating = 0, tempRatingValue = 0, averageRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_and_review_page);

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

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(rating_and_review_page.this)
                        .setTitle("Submit Rating?")
                        .setMessage("Rate " + getRating + "/" + noOfStars + " \nfor " + bookingName + "?")
                        .setCancelable(true)
                        .setPositiveButton("Submit Rating", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                final ProgressDialog progressDialog = new ProgressDialog(rating_and_review_page.this);
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

        ratingMessage = et_ratingMessage.getText().toString();
        ratingByName = ratedByFirstName + " " + ratedByLastName;
        clientLastName = "";

        Ratings ratings = new Ratings(projId, bookingName, clientLastName,
                clientId, ratingByName, getRating, ratingMessage);

        ratingDatabase.push().setValue(ratings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                    processUpdateProjectRating();

                }

            }
        });
    }

    private void processUpdateProjectRating() {

        DatabaseReference ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        Query query = ratingDatabase
                .orderByChild("ratingOfId")
                .equalTo(projId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Ratings ratings = dataSnapshot.getValue(Ratings.class);
                    tempRatingValue = ratings.ratingValue;
                    totalRating = totalRating + tempRatingValue;
                    counter++;

                }

                averageRating = totalRating / counter;
                UpdateProjectRating(averageRating, counter);
            }

            private void UpdateProjectRating(double averageRating, int counter) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("ratingCount", counter);
                hashMap.put("ratingAverage", averageRating);

                projDatabase.child(projId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateBookingStatus();
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void updateBookingStatus() {
        String bookingStatus = "complete";
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("status", bookingStatus);

        bookingDatabase.child(bookingIdFromIntent).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                generateNotification();

            }
        });

    }

    private void generateNotification() {

        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");

        String sp_bookingName = bookingName;
        String sp_notifTitle = "Customer Rated your project";
        String sp_notifMessage = "Customer has rated the project" + sp_bookingName + ".";

        Date currentTime = Calendar.getInstance().getTime();
        String notifCreated = currentTime.toString();

        Notification notification = new Notification(bookingImageUrl, sp_notifTitle, sp_notifMessage, notifCreated, techId);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Intent intent = new Intent(rating_and_review_page.this, history_page.class);
                    startActivity(intent);
                    Toast.makeText(rating_and_review_page.this, "Rating Submitted", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void generateData() {
        bookingIdFromIntent = getIntent().getStringExtra("booking id");
        reviewCategory = getIntent().getStringExtra("category");
        clientId = getIntent().getStringExtra("client id");
        techId = getIntent().getStringExtra("tech id");

        bookingDatabase.child(bookingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Booking booking = snapshot.getValue(Booking.class);

                if(snapshot.exists())
                {
                    bookingImageUrl = booking.imageUrl;
                    bookingName = booking.projName;
                    projId = booking.projId;

                    Picasso.get()
                            .load(bookingImageUrl)
                            .into(iv_ratingPhoto);

                    tv_ratingName.setText(bookingName);

                    generateClientDate(clientId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateClientDate(String clientId) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        userDatabase.child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    ratedByFirstName = users.firstName.substring(0, 1).toUpperCase() +
                            users.firstName.substring(1).toLowerCase();

                    ratedByLastName = users.lastName.substring(0, 1).toUpperCase() +
                            users.lastName.substring(1).toLowerCase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        iv_ratingPhoto = findViewById(R.id.iv_ratingPhoto);
        tv_back = findViewById(R.id.tv_back);
        tv_ratingName = findViewById(R.id.tv_ratingName);
        ratingBar = findViewById(R.id.ratingBar);
        et_ratingMessage = findViewById(R.id.et_ratingMessage);
        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);
    }

}