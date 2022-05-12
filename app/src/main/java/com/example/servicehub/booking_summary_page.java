package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class booking_summary_page extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference projDatabase, userData, bookingDatabase;
    private StorageTask addTask;
    private String userID;


    private TextView tv_addressSummary,tv_propertyTypeSummary,tv_brandSummary,tv_acTypeSummary,tv_unitTypeSummary,tv_prefDateSummary,
            tv_prefTimeSummary,tv_contactNumSummary,tv_addInfoSummary,tv_projNameSummary,tv_techNameSummary,
            tv_techLocationSummary,tv_bookPriceSummry,tv_jobPriceSummary, tv_back;
    private ProgressBar progressBar;
    private ImageView iv_projPhotoSummary;
    private Button btn_confirmBooking;
    private RadioGroup radioGroup;
    private RadioButton rb_gcash, rb_cod;
    private String totalPrice, imageUrl, bookingCreated, paymentMethod, custID, sp_techId , projectIdFromIntent, addressFromIntent, propertyTypeFromIntent,
            airconBrandFromIntent, airconTypeFromIntent, unitTypeFromIntent, bookingDateFromIntent, bookingTimeFromIntent,
            contactNumberFromIntent, addInfoFromIntent, projName, latString, longString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_summary_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

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

        rb_cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rb_cod);
                paymentMethod = "COD";
            }
        });

        rb_gcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.rb_gcash);
                paymentMethod = "Gcash";
            }
        });

        btn_confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    new SweetAlertDialog(
                            booking_summary_page.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText("Please choose payment!")
                            .show();
                }
                else
                {
                    new SweetAlertDialog(booking_summary_page.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning!.")
                            .setCancelText("Cancel")
                            .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    submitBooking();
                                }
                            })
                            .setContentText("Please make sure \nall information \nare correct!")
                            .show();

                }


            }
        });

    }

    private void setRef() {

        tv_addressSummary = findViewById(R.id.tv_addressSummary);
        tv_propertyTypeSummary = findViewById(R.id.tv_propertyTypeSummary);
        tv_brandSummary = findViewById(R.id.tv_brandSummary);
        tv_acTypeSummary = findViewById(R.id.tv_acTypeSummary);
        tv_unitTypeSummary = findViewById(R.id.tv_unitTypeSummary);
        tv_prefDateSummary = findViewById(R.id.tv_prefDateSummary);
        tv_prefTimeSummary = findViewById(R.id.tv_prefTimeSummary);
        tv_contactNumSummary = findViewById(R.id.tv_contactNumSummary);
        tv_addInfoSummary = findViewById(R.id.tv_addInfoSummary);
        tv_projNameSummary = findViewById(R.id.tv_projNameSummary);
        tv_techNameSummary = findViewById(R.id.tv_techNameSummary);
        tv_techLocationSummary = findViewById(R.id.tv_techLocationSummary);
        tv_bookPriceSummry = findViewById(R.id.tv_bookPriceSummry);
        tv_jobPriceSummary = findViewById(R.id.tv_jobPriceSummary);
        tv_back = findViewById(R.id.tv_back);

        rb_gcash = findViewById(R.id.rb_gcash);
        rb_cod = findViewById(R.id.rb_cod);
        radioGroup = findViewById(R.id.radioGroup);

        progressBar = findViewById(R.id.progressBar);

        iv_projPhotoSummary = findViewById(R.id.iv_projPhotoSummary);

        btn_confirmBooking = findViewById(R.id.btn_completeBooking);
    }

    private void generateData() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        latString = extras.getString("latitude");
        longString = extras.getString("longitude");
        projectIdFromIntent = extras.getString("projectIdFromIntent");
         addressFromIntent = extras.getString("address");
         propertyTypeFromIntent = extras.getString("property type");
         airconBrandFromIntent = extras.getString("aircon brand");
         airconTypeFromIntent = extras.getString("aircon type");
         unitTypeFromIntent = extras.getString("unit type");
         bookingDateFromIntent = extras.getString("booking date");
         bookingTimeFromIntent = extras.getString("booking time");
         contactNumberFromIntent = extras.getString("contact number");
         addInfoFromIntent = extras.getString("add info");

        projDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projData = snapshot.getValue(Projects.class);

                if(projData != null) {

                    imageUrl = projData.getImageUrl();
                    custID = userID;
                    sp_techId = projData.userID;
                    String sp_projPhotUrl = projData.getImageUrl();
                    projName = projData.projName;
                    String sp_techLocation = projData.projAddress;
                    String sp_projPrice = projData.price;

                    imageUrl = sp_projPhotUrl;
                    Picasso.get()
                            .load(sp_projPhotUrl)
                            .resize(800, 600)
                            .into(iv_projPhotoSummary);

                    tv_addressSummary.setText(addressFromIntent);
                    tv_propertyTypeSummary.setText(propertyTypeFromIntent);
                    tv_brandSummary.setText(airconBrandFromIntent);
                    tv_acTypeSummary.setText(airconTypeFromIntent);
                    tv_unitTypeSummary.setText(unitTypeFromIntent);
                    tv_prefDateSummary.setText(bookingDateFromIntent);
                    tv_prefTimeSummary.setText(bookingTimeFromIntent);
                    tv_contactNumSummary.setText(contactNumberFromIntent);
                    tv_addInfoSummary.setText(addInfoFromIntent);

                    totalPrice = sp_projPrice;
                    double price = Double.parseDouble(sp_projPrice);
                    DecimalFormat twoPlaces = new DecimalFormat("0.00");

                    getTechName(sp_techId);
                    tv_projNameSummary.setText(projName);
                    tv_techLocationSummary.setText(sp_techLocation);

                    tv_bookPriceSummry.setText("₱ " + twoPlaces.format(price));
                    tv_jobPriceSummary.setText("₱ " + twoPlaces.format(price));

                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitBooking() {
        final ProgressDialog progressDialog = new ProgressDialog(booking_summary_page.this);
        progressDialog.setTitle("Processing your booking...");
        progressDialog.show();

        Date currentTime = new Date();

        bookingCreated = currentTime.toString();
        Booking booking = new Booking(imageUrl, custID, projName, addressFromIntent, latString, longString, propertyTypeFromIntent, airconBrandFromIntent,
                airconTypeFromIntent, unitTypeFromIntent, bookingDateFromIntent, bookingTimeFromIntent,
                contactNumberFromIntent, addInfoFromIntent, totalPrice, paymentMethod, sp_techId, bookingCreated);



        bookingDatabase.push().setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    new SweetAlertDialog(
                            booking_summary_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Booked Successfully!.")
                            .setContentText("Thank you for booking with us!")
                            .show();


                    Intent intent = new Intent(booking_summary_page.this, homepage.class);
                    startActivity(intent);



                } else {
                    Toast.makeText(booking_summary_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void getTechName(String sp_techId) {
        try {

            userData = FirebaseDatabase.getInstance().getReference("Technician Applicants");

            userData.child(sp_techId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Tech_application techData = snapshot.getValue(Tech_application.class);

                    if(techData != null) {
                        try {

                            String sp_techFname = techData.firstName;
                            String sp_techLname = techData.lastName;
                            tv_techNameSummary.setText(sp_techFname + " " + sp_techLname);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}