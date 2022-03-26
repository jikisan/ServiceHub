package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_project_page extends AppCompatActivity {
    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_decreaseSlot, iv_increaseSlot;
    EditText et_projectName, et_address, et_price, et_timeSlot, et_specialInstruction;
    Button btn_pickTime, btn_save;
    TextView tv_uploadPhoto, tv_slotCount;

    int slotCount = 1;
    String slotCountText;

    FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference projectDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project_page);


        user = FirebaseAuth.getInstance().getCurrentUser();
        projectDatabase = FirebaseDatabase.getInstance().getReference(Projects.class.getSimpleName());

        setRef();
        adjustSlot();
        ClickListener();
        bottomNavTaskbar();
    }

    private void ClickListener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProj();
            }
        });
    }

    private void addProj() {
        String projName = et_projectName.getText().toString();
        String projAddress = et_address.getText().toString();
        String price = et_price.getText().toString();
        String projTimeSlot = et_timeSlot.getText().toString();
        String projTimeSlotCount = tv_slotCount.getText().toString();
        String projInstruction = et_specialInstruction.getText().toString();;

        String userID = user.getUid();

        Projects projects = new Projects(projName, projAddress, price, projTimeSlot, projTimeSlotCount, projInstruction, userID);

//        projectDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .setValue(projects).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(add_project_page.this, "Project Added", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(add_project_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        projectDatabase.push().setValue(projects).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(add_project_page.this, "Project Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(add_project_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void adjustSlot() {

        slotCountText = String.valueOf(slotCount);
        tv_slotCount.setText(slotCountText);

        iv_decreaseSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slotCountText;


                if (slotCount == 1){
                    slotCount = 1;
                    Toast.makeText(add_project_page.this, "Slot cannot be empty", Toast.LENGTH_SHORT).show();
                    slotCountText = String.valueOf(slotCount);
                    tv_slotCount.setText(slotCountText);
                }else
                {
                    slotCount = slotCount - 1;
                    slotCountText = String.valueOf(slotCount);
                    tv_slotCount.setText(slotCountText);
                }
            }
        });

        iv_increaseSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    slotCount = slotCount + 1;
                    slotCountText = String.valueOf(slotCount);
                    tv_slotCount.setText(slotCountText);

            }
        });
    }
    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(add_project_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(add_project_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(add_project_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(add_project_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(add_project_page.this, more_page.class);
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
        iv_decreaseSlot = findViewById(R.id.iv_decreaseSlot);
        iv_increaseSlot = findViewById(R.id.iv_increaseSlot);
        et_projectName = findViewById(R.id.et_projectName);
        et_price = findViewById(R.id.et_price);
        et_timeSlot = findViewById(R.id.et_timeSlot);
        et_address = findViewById(R.id.et_address);
        et_specialInstruction = findViewById(R.id.et_specialInstruction);
        btn_save = findViewById(R.id.btn_save);
        btn_pickTime = findViewById(R.id.btn_pickTime);
        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);
        tv_slotCount = findViewById(R.id.tv_slotCount);

    }
}
