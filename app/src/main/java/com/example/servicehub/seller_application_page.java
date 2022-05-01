package com.example.servicehub;

import static com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;

public class seller_application_page extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 60;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_validIdImage, iv_selfieImage;
    private TextView tv_uploadSelfie, tv_uploadValidID, tv_back;
    private Button btn_submit, btn_requestCode, btn_uploadFile;
    private TextInputEditText et_firstName, et_lastName, phoneEditText;

    private Uri validIdUri, selfieUri;

    private FirebaseUser user;
    private DatabaseReference sellerApplicationDatabase;
    private StorageReference sellerApplicationStorage;
    private StorageTask addTask;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_application_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        sellerApplicationStorage = FirebaseStorage.getInstance().getReference("Seller Applicants").child(userID);
        sellerApplicationDatabase = FirebaseDatabase.getInstance().getReference("Seller Applicants").child(userID);


        setRef();
        bottomNavTaskbar();
        clickListener();

    }

    private void clickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(seller_application_page.this, switch_account_page.class);
                startActivity(intent);
            }
        });

        tv_uploadValidID.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                boolean pick = true;
                if (pick == true){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else
                        pickValidIdImage();

                }else{
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else
                        pickValidIdImage();
                }
            }
        });

        tv_uploadSelfie.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                boolean pick = true;
                if (pick == true){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else
                        pickSelfieImage();

                }else{
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else
                        pickSelfieImage();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addTask != null && addTask.isInProgress()){
                    Toast.makeText(seller_application_page.this, "In progress", Toast.LENGTH_SHORT).show();
                } else {
                    inputValidation();
                }
            }
        });

    }

    private void setRef() {

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_validIdImage = findViewById(R.id.iv_validIdImage);
        iv_selfieImage = findViewById(R.id.iv_selfieImage);

        tv_uploadSelfie = findViewById(R.id.tv_uploadSelfie);
        tv_uploadValidID = findViewById(R.id.tv_uploadValidID);
        tv_back = findViewById(R.id.tv_back);

        btn_submit = findViewById(R.id.btn_submit);
        btn_requestCode = findViewById(R.id.btn_requestCode);
        btn_uploadFile = findViewById(R.id.btn_uploadFile);

        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        phoneEditText = findViewById(R.id.phoneEditText);

        Users user = new Users();
        et_firstName.setText(user.firstName);
        et_lastName.setText(user.lastName);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                validIdUri = result.getUri();

                try {

                    Picasso.get().load(validIdUri)
                            .placeholder(R.drawable.upload_photo)
                            .error(R.drawable.upload_photo)
                            .into(iv_validIdImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        else if (requestCode == PICK_IMAGE_REQUEST) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selfieUri = result.getUri();

                try {

                    Picasso.get().load(selfieUri)
                            .placeholder(R.drawable.upload_photo)
                            .error(R.drawable.upload_photo)
                            .into(iv_selfieImage);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void pickSelfieImage() {
        Intent intent = CropImage.activity(selfieUri).getIntent(getBaseContext());
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void pickValidIdImage() {
        CropImage.activity().start(this);
    }

    private void inputValidation() {
        String sp_fName = et_firstName.getText().toString();
        String sp_lName = et_lastName.getText().toString();
        String sp_phone = phoneEditText.getText().toString();



        if (TextUtils.isEmpty(sp_fName)){
            Toast.makeText(this, "First Name is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(sp_lName)){
            Toast.makeText(this, "Last Name is required", Toast.LENGTH_SHORT).show();
        }
        else if(hasImage(iv_validIdImage)){
            Toast.makeText(this, "Valid ID is required", Toast.LENGTH_SHORT).show();
        }
        else if(hasImage(iv_selfieImage)){
            Toast.makeText(this, "Selfie is required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please verify Phone Number", Toast.LENGTH_SHORT).show();

        }
        else if(sp_phone.length() < 10){
            Toast.makeText(this, "Please enter 10 digit number", Toast.LENGTH_SHORT).show();

        }
        else
        {
            String sp_fullName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase()
                    + " " + sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

            new AlertDialog.Builder(seller_application_page.this)
                    .setIcon(R.drawable.logo)
                    .setTitle("ServiceHUB")
                    .setMessage("Please make sure all information entered are correct"
                            + "\n\nFull Name: " + sp_fullName
                            + "\nPhone number: 0" + phoneEditText.getText().toString() + "\n")
                    .setCancelable(true)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            uploadDataToFireBaseStorage(validIdUri, selfieUri);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                        }
                    })
                    .show();
        }


    }

    private void uploadDataToFireBaseStorage(Uri validIdUri, Uri selfieUri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Submitting Application");
        progressDialog.show();

        StorageReference validIdImageRef = sellerApplicationStorage.child(validIdUri.getLastPathSegment());
        StorageReference selfieImageRef = sellerApplicationStorage.child(selfieUri.getLastPathSegment());


        //adding of valid id image to firebase storage
        addTask = validIdImageRef.putFile(validIdUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                validIdImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String validIdUrl = uri.toString();


                        //adding of selfie image to firebase storage
                        selfieImageRef.putFile(selfieUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                selfieImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String selfieUrl = uri.toString();

                                        final String pdfUrl = "";
                                        String pdfFileName = "";

                                        addingDataToRealtimeDB(validIdUrl, selfieUrl, pdfFileName);

                                        Intent intent = new Intent(seller_application_page.this, switch_account_page.class);
                                        startActivity(intent);
                                        Toast.makeText(seller_application_page.this, "Application Submitted", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(seller_application_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.01* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Sending Application " + (int)progress + "%");
                    }
                });
    }

    private void addingDataToRealtimeDB(String validIdUrl, String selfieUrl, String pdfFileName) {
        String sp_fName = et_firstName.getText().toString();
        String sp_lName = et_lastName.getText().toString();

        String firstName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase();
        String lastName = sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();
        String validIDName = validIdUri.getLastPathSegment();
        String selfieImageName = selfieUri.getLastPathSegment();
        String phoneNumber = "0" + phoneEditText.getText().toString();
        boolean isPending = true;
        boolean isApprove = false;

        Seller_application seller_application = new Seller_application(firstName, lastName, validIDName, validIdUrl, selfieImageName, selfieUrl,
                phoneNumber, userID, isApprove, isPending);

        sellerApplicationDatabase.setValue(seller_application).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(seller_application_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean hasImage(ImageView iv)    {

        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }


    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(seller_application_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(seller_application_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(seller_application_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(seller_application_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(seller_application_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
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