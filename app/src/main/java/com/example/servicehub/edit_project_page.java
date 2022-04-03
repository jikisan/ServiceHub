package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class edit_project_page extends AppCompatActivity {

    public static final String API_KEY = "AIzaSyCQdaPd6EyJuLoDMLGHX2vNLL18a8kdRH8";
    String projectID = "-MzQqfbaaTu-W-2KdFgu";


    private FirebaseUser user;
    private DatabaseReference projectDatabase;
    private String userID;

    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_projectImage;
    EditText et_projectName,  et_price, et_specialInstruction;
    Button btn_pickTime, btn_save;
    TextView tv_uploadPhoto, tv_slotCount, tv_timeSlot, tv_address;
    String imageUriText;
    Uri imageUri;

    int hour, minute;
    int slotCount = 1;
    String slotCountText, latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_project_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects").child(userID);

        setRef();
        generateDataValue();
        initPlaces();
        ClickListener();
        bottomNavTaskbar();
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), API_KEY);

        //Set edittext no focusable
        tv_address.setFocusable(false);
    }

    private void ClickListener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProject();
            }
        });

        btn_pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        tv_timeSlot.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(edit_project_page.this, style, onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        tv_uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                boolean pick = true;
                if (pick == true){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else
                        PickImage();

                }else{
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else
                        PickImage();
                }
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(edit_project_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });
    }



    private void PickImage() {
        CropImage.activity().start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                try{

                    InputStream stream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    iv_projectImage.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        else if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
            tv_address.setText(place.getAddress());
            latLng = place.getLatLng().toString();

        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
        }
    }

    private void generateDataValue() {


        projectDatabase.child(projectID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projectData = snapshot.getValue(Projects.class);

                if(projectData != null){
                    try{
                        imageUriText = projectData.imageUri;
                        String sp_projName = projectData.getProjName();
                        String sp_projAddress = projectData.getProjAddress();
                        String sp_projPrice = projectData.getPrice();
                        String sp_projTimeSlot = projectData.getProjTimeSlot();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();

                        imageUri = Uri.parse(imageUriText);

                        InputStream stream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);

                        iv_projectImage.setImageBitmap(bitmap);
                        et_projectName.setText(sp_projName);
                        tv_address.setText(sp_projAddress);
                        et_price.setText(sp_projPrice);
                        tv_timeSlot.setText(sp_projTimeSlot);
                        et_specialInstruction.setText(sp_projSpecialInstruction);

                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("PROJECT LOG JK:" + e.getMessage());
                    }
                }
                else
                {
                    Toast.makeText(edit_project_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("PROJECT LOG JK: Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(edit_project_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProject() {
        String projName = et_projectName.getText().toString();
        String projAddress = tv_address.getText().toString();
        String price = et_price.getText().toString();
        String projTimeSlot = tv_timeSlot.getText().toString();
        String projInstruction = et_specialInstruction.getText().toString();
        String imageUriText = imageUri.toString();

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("imageUri", imageUriText);
        hashMap.put("latlng", latLng);
        hashMap.put("price", price);
        hashMap.put("projAddress", projAddress);
        hashMap.put("projInstruction", projInstruction);
        hashMap.put("projName", projName);
        hashMap.put("projTimeSlot", projTimeSlot);
        hashMap.put("userID", userID);

        projectDatabase.child(projectID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(edit_project_page.this, "Project is updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(edit_project_page.this, tech_dashboard.class);
                startActivity(intent);
            }
        });
    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(edit_project_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(edit_project_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(edit_project_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(edit_project_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(edit_project_page.this, more_page.class);
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
//        iv_decreaseSlot = findViewById(R.id.iv_decreaseSlot);
//        iv_increaseSlot = findViewById(R.id.iv_increaseSlot);
        iv_projectImage = findViewById(R.id.iv_projectImage);
        et_projectName = findViewById(R.id.et_projectName);
        et_price = findViewById(R.id.et_price);
        tv_timeSlot = findViewById(R.id.tv_timeSlot);
        tv_address = findViewById(R.id.tv_address);
        et_specialInstruction = findViewById(R.id.et_specialInstruction);
        btn_save = findViewById(R.id.btn_save);
        btn_pickTime = findViewById(R.id.btn_pickTime);
        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);

    }
}