package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class booking_details extends AppCompatActivity {

    private ImageView iv_bookingPhoto, iv_messageCustomer, iv_deleteOrder,iv_messageBtn,
            iv_notificationBtn, iv_homeBtn, iv_accountBtn, iv_moreBtn, btn_viewInMap;
    private TextView tv_bookingName, tv_time, tv_specialInstruction, tv_month, tv_date, tv_day, tv_customerName,
            tv_customerContactNum, tv_customerAddress, tv_back;

    String imageUriText, projectIdFromIntent;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage;
    private DatabaseReference userDatabase;
    private DatabaseReference projectDatabase;
    private StorageTask addTask;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        projectStorage = FirebaseStorage.getInstance().getReference("Projects").child(userID);
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects").child(userID);


        setRef();
        clickListener();
        generateDataValue();
        generateProfile();
        bottomNavTaskbar();
    }

    private void clickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(booking_details.this, tech_dashboard.class);
                startActivity(intent);
            }
        });

    }

    private void setRef() {
        iv_bookingPhoto = findViewById(R.id.iv_bookingPhoto);
        iv_messageCustomer = findViewById(R.id.iv_messageCustomer);
        iv_deleteOrder = findViewById(R.id.iv_deleteOrder);
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        tv_bookingName = findViewById(R.id.tv_bookingName);
        tv_time = findViewById(R.id.tv_time);
        tv_specialInstruction = findViewById(R.id.tv_specialInstruction);
        tv_month = findViewById(R.id.tv_month);
        tv_date = findViewById(R.id.tv_date);
        tv_day = findViewById(R.id.tv_day);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_customerContactNum = findViewById(R.id.tv_customerContactNum);
        tv_customerAddress = findViewById(R.id.tv_customerAddress);
        tv_back = findViewById(R.id.tv_back);


    }

    private void generateProfile() {
        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_num = userProfile.contactNum;

                    tv_customerName.setText(sp_fName + " " + sp_lName);
                    tv_customerContactNum.setText(sp_num);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booking_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataValue() {



        projectIdFromIntent = getIntent().getStringExtra("Project ID");
        projectDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projectData = snapshot.getValue(Projects.class);

                if(projectData != null){
                    try{
                        imageUriText = projectData.getImageUrl();
                        String sp_projName = projectData.getProjName();
                        String sp_projAddress = projectData.getProjAddress();
                        String sp_projStartTime = projectData.getStartTime();
                        String sp_projEndTime = projectData.getEndTime();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();

                        Picasso.get().load(imageUriText)
                                .into(iv_bookingPhoto);

                        tv_bookingName.setText(sp_projName);
                        tv_specialInstruction.setText(sp_projSpecialInstruction);
                        tv_time.setText(sp_projStartTime+ " - " + sp_projEndTime);
                        tv_month.setText("May");
                        tv_date.setText("20");
                        tv_day.setText("Tue");

                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(booking_details.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booking_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(booking_details.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(booking_details.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(booking_details.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(booking_details.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(booking_details.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }


}