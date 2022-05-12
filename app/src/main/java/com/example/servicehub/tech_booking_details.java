package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class tech_booking_details extends AppCompatActivity {

    private ImageView iv_bookingPhoto, iv_messageCustomer, btn_viewInMap, iv_custPhoto, iv_viewInMapBtn;
    private TextView tv_addressSummary,tv_propertyTypeSummary,tv_brandSummary,tv_acTypeSummary,tv_unitTypeSummary,tv_prefDateSummary,
            tv_prefTimeSummary,tv_contactNumSummary, tv_back, tv_customerName, tv_bookingName,
            tv_time, tv_specialInstruction, tv_month, tv_date, tv_day, iv_deleteBtn;
    private ProgressBar progressBar;
    private Button btn_completeBooking;

    String imageUrl, custID, bookingIdFromIntent, latString, longString;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage;
    private DatabaseReference userDatabase, projectDatabase, bookingDatabase;
    private StorageTask addTask;
    private String userID;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tech_booking_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");


        setRef();
        clickListener();
        generateBookingData();

    }

    private void clickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelBooking();
            }
        });

        iv_viewInMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProject = new Intent(tech_booking_details.this, view_in_map.class);
                intentProject.putExtra("Category", "booking");
                intentProject.putExtra("latString", latString);
                intentProject.putExtra("longString", longString);
                startActivity(intentProject);
            }
        });

    }

    private void cancelBooking() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(tech_booking_details.this)
                .setTitle("Cancel Booking?")
                .setMessage("Are you sure you want to cancel this booking?")
                .setCancelable(true)
                .setPositiveButton("Cancel Booking", R.drawable.delete_btn, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        progressDialog = new ProgressDialog(tech_booking_details.this);
                        progressDialog.setTitle("Cancelling...");
                        progressDialog.show();

                        bookingIdFromIntent = getIntent().getStringExtra("Booking ID");
                        bookingDatabase.child(bookingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue();

                                }
                                generateNotification();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("Back", R.drawable.back_arrow, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }


                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();

//                new AlertDialog.Builder(tech_booking_details.this)
//                        .setIcon(R.drawable.warning)
//                        .setTitle("Delete Booking")
//                        .setMessage("Are you sure that you want to permanently delete this booking?")
//                        .setCancelable(true)
//                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//
//                            }
//                        })
//                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int i) {
//                            }
//                        })
//                        .show();
    }

    private void generateNotification() {

        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");

        String sp_bookingName = tv_bookingName.getText().toString();
        String sp_notifTitle = "Booking cancelled";
        String sp_notifMessage = "Booking: " + sp_bookingName + " has been cancelled by the technician.";

        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();

        Notification notification = new Notification(imageUrl, sp_notifTitle, sp_notifMessage, cartCreated, custID);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    Intent intent = new Intent(tech_booking_details.this, tech_dashboard.class);
                    startActivity(intent);

                    Toast.makeText(tech_booking_details.this, "Booking Cancelled", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    private void generateBookingData() {

        bookingIdFromIntent = getIntent().getStringExtra("Booking ID");

        bookingDatabase.child(bookingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Booking bookingData = snapshot.getValue(Booking.class);

                if(bookingData != null) {
                    try {
                        latString = bookingData.getLatitude();
                        longString = bookingData.getLongitude();

                        custID  = bookingData.custID;
                        imageUrl = bookingData.imageUrl;
                        String sp_bookingName = bookingData.projName;
                        String sp_bookingtime = bookingData.bookingTime;
                        String sp_bookingDate = bookingData.bookingDate;
                        String sp_addInfo = bookingData.addInfo;
                        String sp_address = bookingData.custAddress;
                        String sp_contactNum = bookingData.custContactNum;
                        String sp_propType = bookingData.propertyType;
                        String sp_airconBrand = bookingData.airconBrand;
                        String sp_airconType = bookingData.airconType;
                        String sp_unitType = bookingData.unitType;
                        String sp_price = bookingData.totalPrice;

                        String[] parts = sp_bookingDate.split("/");

                        double price = Double.parseDouble(sp_price);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");


                        Picasso.get().load(imageUrl).into(iv_bookingPhoto);
                        tv_bookingName.setText(sp_bookingName);
                        tv_time.setText(sp_bookingtime);
                        tv_month.setText(parts[0]);
                        tv_date.setText(parts[1]);
                        tv_day.setText(parts[3]);
                        tv_specialInstruction.setText(sp_addInfo);

                        tv_addressSummary.setText(sp_address);
                        tv_contactNumSummary.setText(sp_contactNum);
                        tv_propertyTypeSummary.setText(sp_propType);
                        tv_brandSummary.setText(sp_airconBrand);
                        tv_acTypeSummary.setText(sp_airconType);
                        tv_unitTypeSummary.setText(sp_unitType);
                        btn_completeBooking.setText("Total Price: ₱ " + twoPlaces.format(price) + " · " + "Complete Booking");

                        generateProfile();
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateProfile() {
        userDatabase.child(custID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users custData = snapshot.getValue(Users.class);
                if(snapshot.exists()){

                    String sp_fName = custData.firstName;
                    String sp_lName = custData.lastName;
                    String sp_imageUrl = custData.imageUrl;
                    String sp_fullName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase()
                            + " " + sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_customerName.setText(sp_fullName);

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_custPhoto);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(tech_booking_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef() {
        iv_bookingPhoto = findViewById(R.id.iv_bookingPhoto);
        iv_messageCustomer = findViewById(R.id.iv_messageCustomer);
        iv_custPhoto = findViewById(R.id.iv_custPhoto);
        iv_deleteBtn = findViewById(R.id.iv_deleteBtn);
        iv_viewInMapBtn = findViewById(R.id.iv_viewInMapBtn);

        tv_month = findViewById(R.id.tv_month);
        tv_date = findViewById(R.id.tv_date);
        tv_day = findViewById(R.id.tv_day);
        tv_specialInstruction = findViewById(R.id.tv_specialInstruction);
        tv_time = findViewById(R.id.tv_time);
        tv_back = findViewById(R.id.tv_back);
        tv_bookingName = findViewById(R.id.tv_bookingName);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_addressSummary = findViewById(R.id.tv_addressSummary);
        tv_contactNumSummary = findViewById(R.id.tv_contactNumSummary);
        tv_propertyTypeSummary = findViewById(R.id.tv_propertyTypeSummary);
        tv_brandSummary = findViewById(R.id.tv_brandSummary);
        tv_acTypeSummary = findViewById(R.id.tv_acTypeSummary);
        tv_unitTypeSummary = findViewById(R.id.tv_unitTypeSummary);
        tv_prefDateSummary = findViewById(R.id.tv_prefDateSummary);
        tv_prefTimeSummary = findViewById(R.id.tv_prefTimeSummary);

        progressBar = findViewById(R.id.progressBar);

        btn_completeBooking = findViewById(R.id.btn_completeBooking);


    }

}