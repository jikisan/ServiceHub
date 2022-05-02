package com.example.servicehub;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class edit_project_page extends AppCompatActivity {


    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_projectImage, btn_delete, iv_back;
    private EditText et_projectName,  et_price, et_specialInstruction;
    private Button btn_pickStartTime, btn_pickEndTime, btn_update;
    private TextView tv_uploadPhoto, tv_startTime, tv_endTime, tv_address, tv_back;
    private Chip chip_Mon, chip_Tue, chip_Wed, chip_Thu, chip_Fri, chip_Sat, chip_Sun;
    private ChipGroup chipGroup;
    private String imageUriText, projectIdFromIntent, latLng,  tempImageName;
    private Uri imageUri, tempUri;
    private Spinner spinner_projCategory;
    private Geocoder geocoder;

    private int PLACE_PICKER_REQUEST = 1;
    private int hour, minute;
    private boolean isAvailableMon = false;
    private boolean isAvailableTue = false;
    private boolean isAvailableWed = false;
    private boolean isAvailableThu = false;
    private boolean isAvailableFri = false;
    private boolean isAvailableSat = false;
    private boolean isAvailableSun = false;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage;
    private DatabaseReference projectDatabase;
    private StorageTask addTask;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_project_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        setRef();
        generateDataValue();
        initPlaces();
        ClickListener();
        bottomNavTaskbar();
        spinnerCategory();
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        //Set edittext no focusable
        tv_address.setFocusable(false);
    }

    private void ClickListener() {
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(edit_project_page.this)
                        .setIcon(R.drawable.logo)
                        .setTitle("Delete Application")
                        .setMessage("Are you sure that you want to permanently delete this project?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                projectIdFromIntent = getIntent().getStringExtra("Project ID");
                                projectDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Projects projectData = snapshot.getValue(Projects.class);

                                        String imageName = projectData.getImageName();
                                        StorageReference imageRef = projectStorage.child(imageName);


                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            imageRef.delete();
                                            dataSnapshot.getRef().removeValue();

                                        }

                                        Toast.makeText(edit_project_page.this, "Project Deleted", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(edit_project_page.this, tech_dashboard.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                            }
                        })
                        .show();

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(addTask != null && addTask.isInProgress()){
                    Toast.makeText(edit_project_page.this, "In progress", Toast.LENGTH_SHORT).show();
                } else {

                    inputValidation();
                }

            }
        });

        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        boolean isPM = (hour >= 12);
                        tv_startTime.setText(String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM"));


                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(edit_project_page.this, style, onTimeSetListener, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        boolean isPM = (hour >= 12);
                        tv_endTime.setText(String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM"));

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
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(edit_project_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);

                // placePicker();
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(edit_project_page.this, tech_dashboard.class);
                startActivity(intent);
            }
        });

    }

    private void inputValidation() {
        String sp_projName = et_projectName.getText().toString();
        String sp_address = tv_address.getText().toString();
        String time_start = tv_startTime.getText().toString();
        String time_end = tv_endTime.getText().toString();
        String price = et_price.getText().toString();

        if (TextUtils.isEmpty(sp_projName)){
            Toast.makeText(this, "Project Name is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(sp_address)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
        }
        else if(!chip_Mon.isChecked() && !chip_Tue.isChecked() && !chip_Wed.isChecked() && !chip_Thu.isChecked()
                && !chip_Fri.isChecked() && !chip_Sat.isChecked() && !chip_Sun.isChecked()){
            Toast.makeText(this, "Availability is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(time_start)){
            Toast.makeText(this, "Starting time is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(time_end)){
            Toast.makeText(this, "End time is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
        }
        else{
            new AlertDialog.Builder(edit_project_page.this)
                    .setIcon(R.drawable.logo)
                    .setTitle("Updating project")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(true)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(imageUri == null)
                            {
                                updateProjectNoImage();
                            }
                            else{
                                updateProject();
                            }

                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                        }
                    })
                    .show();
        }

    }

    private void setRef() {

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_projectImage = findViewById(R.id.iv_projectImage);

        et_specialInstruction = findViewById(R.id.et_specialInstruction);
        et_projectName = findViewById(R.id.et_projectName);
        et_price = findViewById(R.id.et_price);

        tv_startTime = findViewById(R.id.tv_startTime);
        tv_address = findViewById(R.id.tv_address);
        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);
        tv_endTime = findViewById(R.id.tv_endTime);
        tv_back = findViewById(R.id.tv_back);


        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        spinner_projCategory = findViewById(R.id.spinner_projCategory);

        chip_Mon = findViewById(R.id.chip_Mon);
        chip_Tue = findViewById(R.id.chip_Tue);
        chip_Wed = findViewById(R.id.chip_Wed);
        chip_Thu = findViewById(R.id.chip_Thu);
        chip_Fri = findViewById(R.id.chip_Fri);
        chip_Sat = findViewById(R.id.chip_Sat);
        chip_Sun = findViewById(R.id.chip_Sun);
        chipGroup = findViewById(R.id.chipGroup);

    }

    private void updateProject() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Project...");
        progressDialog.show();

        StorageReference fileReference = projectStorage.child(imageUri.getLastPathSegment());

        String sp_projCategory = spinner_projCategory.getSelectedItem().toString();
        String projName = et_projectName.getText().toString();
        String projAddress = tv_address.getText().toString();
        String price = et_price.getText().toString();
        String sp_projStartTime = tv_startTime.getText().toString();
        String sp_projEndTime = tv_endTime.getText().toString();
        String projInstruction = et_specialInstruction.getText().toString();
        String imageName = imageUri.getLastPathSegment();
        int ratings = 0;


        addTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imageURL = uri.toString();

                        chipsValidation();

                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("category", sp_projCategory);
                        hashMap.put("imageUrl", imageURL);
                        hashMap.put("imageName", imageName);
                        hashMap.put("projName", projName);
                        hashMap.put("projLatLng", latLng);
                        hashMap.put("projAddress", projAddress);
                        hashMap.put("price", price);
                        hashMap.put("startTime", sp_projStartTime);
                        hashMap.put("endTime", sp_projEndTime);
                        hashMap.put("projInstruction", projInstruction);
                        hashMap.put("availableMon", isAvailableMon);
                        hashMap.put("availableTue", isAvailableTue);
                        hashMap.put("availableWed", isAvailableWed);
                        hashMap.put("availableThu", isAvailableThu);
                        hashMap.put("availableFri", isAvailableFri);
                        hashMap.put("availableSat", isAvailableSat);
                        hashMap.put("availableSun", isAvailableSun);

                        projectIdFromIntent = getIntent().getStringExtra("Project ID");
                        projectDatabase.child(projectIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                                    StorageReference imageRef = projectStorage.child(tempImageName);
                                    imageRef.delete();


                                progressDialog.dismiss();
                                Intent intent = new Intent(edit_project_page.this, tech_dashboard.class);
                                startActivity(intent);
                                Toast.makeText(edit_project_page.this, "Project is updated", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(edit_project_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void updateProjectNoImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating list...");
        progressDialog.show();

        String sp_projCategory = spinner_projCategory.getSelectedItem().toString();
        String projName = et_projectName.getText().toString();
        String projAddress = tv_address.getText().toString();
        String price = et_price.getText().toString();
        String sp_projStartTime = tv_startTime.getText().toString();
        String sp_projEndTime = tv_endTime.getText().toString();
        String projInstruction = et_specialInstruction.getText().toString();
        int ratings = 0;

        chipsValidation();

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("category", sp_projCategory);
        hashMap.put("projName", projName);
        hashMap.put("projLatLng", latLng);
        hashMap.put("projAddress", projAddress);
        hashMap.put("price", price);
        hashMap.put("startTime", sp_projStartTime);
        hashMap.put("endTime", sp_projEndTime);
        hashMap.put("projInstruction", projInstruction);
        hashMap.put("availableMon", isAvailableMon);
        hashMap.put("availableTue", isAvailableTue);
        hashMap.put("availableWed", isAvailableWed);
        hashMap.put("availableThu", isAvailableThu);
        hashMap.put("availableFri", isAvailableFri);
        hashMap.put("availableSat", isAvailableSat);
        hashMap.put("availableSun", isAvailableSun);

        projectIdFromIntent = getIntent().getStringExtra("Project ID");
        projectDatabase.child(projectIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                progressDialog.dismiss();
                Intent intent = new Intent(edit_project_page.this, tech_dashboard.class);
                startActivity(intent);
                Toast.makeText(edit_project_page.this, "Project is updated", Toast.LENGTH_SHORT).show();

            }
        });
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
//                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                    iv_projectImage.setImageBitmap(bitmap);

                    Picasso.get().load(imageUri)
                            .placeholder(R.drawable.logo)
                            .error(R.drawable.logo)
                            .into(iv_projectImage);

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        else if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            tv_address.setText(place.getAddress());
            latLng = place.getLatLng().toString();

        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
        }

        else if(requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {


                List<Address> address = null;
                geocoder = new Geocoder(this, Locale.getDefault());

                assert data != null;
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);

                try {
                    address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String addressLine = String.valueOf(address.get(0).getAddressLine(0));
                String locality = String.valueOf(address.get(0).getLocality());
                String country = String.valueOf(address.get(0).getCountryName());

                String addressText =  addressLine + " " + locality + " " + country;

                tv_address.setText(addressText);
                latLng = place.getLatLng().toString();


            }
        }

    }

    private void PickImage() {
        CropImage.activity().start(this);
    }

    private void generateDataValue() {

        projectIdFromIntent = getIntent().getStringExtra("Project ID");
        projectDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projectData = snapshot.getValue(Projects.class);

                if(projectData != null){
                    try{
                        tempImageName = projectData.getImageName();

                        imageUriText = projectData.getImageUrl();
                        String sp_projName = projectData.getProjName();
                        String sp_projAddress = projectData.getProjAddress();
                        String sp_projPrice = projectData.getPrice();
                        String sp_projStartTime = projectData.getStartTime();
                        String sp_projEndTime = projectData.getEndTime();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();
                        boolean sp_isAvailableMon = projectData.isAvailableMon();
                        boolean sp_isAvailableTue = projectData.isAvailableTue();
                        boolean sp_isAvailableWed = projectData.isAvailableWed();
                        boolean sp_isAvailableThu = projectData.isAvailableThu();
                        boolean sp_isAvailableFri = projectData.isAvailableFri();
                        boolean sp_isAvailableSat = projectData.isAvailableSat();
                        boolean sp_isAvailableSun = projectData.isAvailableSun();

                        tempUri = Uri.parse(imageUriText);

                        Picasso.get().load(tempUri)
                                .into(iv_projectImage);



                        et_projectName.setText(sp_projName);
                        tv_address.setText(sp_projAddress);
                        et_price.setText(sp_projPrice);
                        tv_startTime.setText(sp_projStartTime);
                        tv_endTime.setText(sp_projEndTime);
                        et_specialInstruction.setText(sp_projSpecialInstruction);

                        if(sp_isAvailableMon == true){
                            chip_Mon.setChecked(true);
                        }
                        if(sp_isAvailableTue == true){
                            chip_Tue.setChecked(true);
                        }
                        if(sp_isAvailableWed == true){
                            chip_Wed.setChecked(true);
                        }
                        if(sp_isAvailableThu == true){
                            chip_Thu.setChecked(true);
                        }
                        if(sp_isAvailableFri == true){
                            chip_Fri.setChecked(true);
                        }
                        if(sp_isAvailableSat == true){
                            chip_Sat.setChecked(true);
                        }
                        if(sp_isAvailableSun == true){
                            chip_Sun.setChecked(true);
                        }


                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(edit_project_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(edit_project_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void spinnerCategory() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.project_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_projCategory.setAdapter(adapter);
    }

    private void chipsValidation() {

        if(!chip_Mon.isChecked() && !chip_Tue.isChecked() && !chip_Wed.isChecked() && !chip_Thu.isChecked()
                && !chip_Fri.isChecked() && !chip_Sat.isChecked() && !chip_Sun.isChecked()){
            Toast.makeText(this, "Please choose a day you are available", Toast.LENGTH_SHORT).show();
        }

        if(chip_Mon.isChecked()){
            isAvailableMon = true;
        }

        if(chip_Tue.isChecked()){
            isAvailableTue = true;
        }

        if(chip_Wed.isChecked()){
            isAvailableWed = true;
        }

        if(chip_Thu.isChecked()){
            isAvailableThu = true;
        }

        if(chip_Fri.isChecked()){
            isAvailableFri = true;
        }

        if(chip_Sat.isChecked()){
            isAvailableSat = true;
        }

        if(chip_Sun.isChecked()){
            isAvailableSun = true;
        }
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

    private boolean hasImage(ImageView iv){

        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }


    // validate permissions
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

    // place picker
//    private void placePicker() {
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//
//        try
//        {
//            startActivityForResult (builder.build ( edit_project_page.this)
//                    , PLACE_PICKER_REQUEST);
//        }
//
//        catch (GooglePlayServicesRepairableException e)
//        {
//            e.printStackTrace ();
//        }
//
//        catch (GooglePlayServicesNotAvailableException e)
//        {
//            e.printStackTrace ();
//        }
//    }
}