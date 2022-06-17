package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class add_funds_page extends AppCompatActivity {

    private TextView tv_back, tv_bannerName, tv_fundBalance, tv_uploadProofOfPayment;
    private ImageView iv_userPic, iv_proofOfPayment;
    private EditText et_inputFund;
    private Button btn_addFund;
    private ProgressBar progressBar;

    private DatabaseReference walletDb, userDatabase, fundRequestDb;
    private StorageReference fundReqStorage;
    private FirebaseUser user;
    private String userID, category;
    private Double fundAmount;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_funds_page);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        walletDb = FirebaseDatabase.getInstance().getReference("Wallets");
        fundRequestDb = FirebaseDatabase.getInstance().getReference("Fund Requests");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        fundReqStorage = FirebaseStorage.getInstance().getReference("Fund Requests").child(userID);

        category = getIntent().getStringExtra("category");

        setRef();
        clickListeners();
        generateData();
    }

    private void generateData() {

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_imageUrl = userProfile.imageUrl;

                    String firstName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase();
                    String lastName = sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_bannerName.setText(firstName + " " + lastName);
                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_userPic);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(add_funds_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        walletDb.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallets wallets = snapshot.getValue(Wallets.class);

                if(wallets != null)
                {
                    fundAmount = wallets.fundAmount;
                    tv_fundBalance.setText(fundAmount + "");

                }
                else

                    fundAmount = 0.00;
                    tv_fundBalance.setText(fundAmount + "");
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

                validateCategory();

            }
        });

        btn_addFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addFund = et_inputFund.getText().toString();

                if(TextUtils.isEmpty(addFund))
                    Toast.makeText(add_funds_page.this, "Please enter amount", Toast.LENGTH_SHORT).show();
                else if(hasImage(iv_proofOfPayment)){
                    Toast.makeText(add_funds_page.this, "Proof of payment is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    double enteredFundAmount = Double.parseDouble(et_inputFund.getText().toString());


                    BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(add_funds_page.this)
                            .setTitle("ADDING FUNDS")
                            .setMessage("Add " + enteredFundAmount + " to your wallet?")
                            .setCancelable(true)
                            .setPositiveButton("Add Funds", new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                    final ProgressDialog progressDialog = new ProgressDialog(add_funds_page.this);
                                    progressDialog.setTitle("Processing...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    addFund(enteredFundAmount);
                                }
                            })
                            .setNegativeButton("Back", new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .build();

                    mBottomSheetDialog.show();
                }



            }
        });

        tv_uploadProofOfPayment.setOnClickListener(new View.OnClickListener() {
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

    }

    private void addFund(double totalFundAmount) {

        StorageReference fileReference = fundReqStorage.child(imageUri.getLastPathSegment());
        String imageName = imageUri.getLastPathSegment();

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imageURL = uri.toString();

                        addFundToDb(imageName, imageURL, totalFundAmount);
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(add_funds_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addFundToDb(String imageName, String imageURL, double totalFundAmount) {

        String status = "pending";

        Fund_Request fund_request = new Fund_Request(userID, totalFundAmount, imageName, imageURL, status);

        fundRequestDb.push().setValue(fund_request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    new SweetAlertDialog(add_funds_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("SUCCESS!.")
                            .setContentText("Fund request has been processed \n for approval.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    validateCategory();
                                }
                            })
                            .show();
                }
                else
                {
                    new SweetAlertDialog(add_funds_page.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("FAILED!.")
                            .setContentText("Transaction is failed. \n Please try again.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    Intent intent = new Intent(add_funds_page.this, add_funds_page.class);
                                    startActivity(intent);

                                }
                            })
                            .show();
                }
            }

        });

    }

    private void validateCategory() {
        if(category.equals("tech"))
        {
            Intent intent = new Intent(add_funds_page.this, tech_dashboard.class);
            startActivity(intent);
        }
        else if(category.equals("seller"))
        {
            Intent intent = new Intent(add_funds_page.this, seller_dashboard.class);
            startActivity(intent);
        }

    }

    private boolean hasImage(ImageView iv){

        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }

    private void setRef() {
        tv_back = findViewById(R.id.tv_back);
        iv_userPic = findViewById(R.id.iv_userPic);
        iv_proofOfPayment = findViewById(R.id.iv_proofOfPayment);
        tv_uploadProofOfPayment = findViewById(R.id.tv_uploadProofOfPayment);
        tv_bannerName = findViewById(R.id.tv_bannerName);
        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        et_inputFund = findViewById(R.id.et_inputFund);
        btn_addFund = findViewById(R.id.btn_addFund);
        progressBar = findViewById(R.id.progressBar);
    }

    private void PickImage() {
        CropImage.activity().start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                try{
                    Picasso.get().load(imageUri)
                            .into(iv_proofOfPayment);

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
}