package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class add_project_page extends AppCompatActivity {


    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_decreaseSlot, iv_increaseSlot, iv_projectImage;
    EditText et_projectName,  et_price, et_specialInstruction;
    Button btn_pickStartTime, btn_save;
    TextView tv_uploadPhoto, tv_slotCount, tv_startTime, tv_address;
    Uri imageUri;
    Bitmap bitmap;
    Spinner spinner_projCategory;

    int hour, minute;
    int slotCount = 1;
    String slotCountText, latLng;

    FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference projectDatabase;
    private StorageReference projectStorage;
    private StorageTask addTask;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_project_page);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectStorage = FirebaseStorage.getInstance().getReference("Projects").child(userID);
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects").child(userID);

        setRef();
        //adjustSlot();
        initPlaces();
        ClickListener();
        bottomNavTaskbar();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.project_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_projCategory.setAdapter(adapter);
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        //Set edittext no focusable
        tv_address.setFocusable(false);
    }

    private void ClickListener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addTask != null && addTask.isInProgress()){
                    Toast.makeText(add_project_page.this, "In progress", Toast.LENGTH_SHORT).show();
                } else {
                    addProj();
                }
            }
        });

        btn_pickStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        tv_startTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(add_project_page.this, style, onTimeSetListener, hour, minute, false);
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
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(add_project_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });
    }

    private void PickImage() {
        CropImage.activity().start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                try{
//                    InputStream stream = getContentResolver().openInputStream(imageUri);
//                    bitmap = BitmapFactory.decodeStream(stream);
//                    iv_projectImage.setImageBitmap(bitmap);

                    Picasso.get().load(imageUri)
                            .into(iv_projectImage);

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

    private void addProj() {
        StorageReference fileReference = projectStorage.child(imageUri.getLastPathSegment());

        String projName = et_projectName.getText().toString();
        String projAddress = tv_address.getText().toString();
        String price = et_price.getText().toString();
        String projTimeSlot = tv_startTime.getText().toString();
        String projInstruction = et_specialInstruction.getText().toString();
        String imageName = imageUri.getLastPathSegment();
        int ratings = 0;
        String ratingsText = String.valueOf(ratings);


        addTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String downloadUrl = uri.toString();

                        Projects projects = new Projects(downloadUrl, imageName, projName, latLng, projAddress, price, projTimeSlot, projInstruction, ratingsText);

                        projectDatabase.push().setValue(projects).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(add_project_page.this, "Project Added", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(add_project_page.this, tech_dashboard.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(add_project_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(add_project_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
//        iv_decreaseSlot = findViewById(R.id.iv_decreaseSlot);
//        iv_increaseSlot = findViewById(R.id.iv_increaseSlot);
        iv_projectImage = findViewById(R.id.iv_projectImage);
        et_projectName = findViewById(R.id.et_projectName);
        et_price = findViewById(R.id.et_price);
        tv_startTime = findViewById(R.id.tv_startTime);
        tv_address = findViewById(R.id.tv_address);
        et_specialInstruction = findViewById(R.id.et_specialInstruction);
        btn_save = findViewById(R.id.btn_save);
        btn_pickStartTime = findViewById(R.id.btn_pickStartTime);
        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);
        spinner_projCategory = findViewById(R.id.spinner_projCategory);



    }


    //validate permissions
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
}
