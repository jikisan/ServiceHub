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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class client_booking_details extends AppCompatActivity {



    private ImageView iv_messageCustomer, iv_custPhoto, iv_viewInMapBtn, iv_proofOfPayment;
    private TextView tv_addressSummary,tv_propertyTypeSummary,tv_brandSummary,tv_acTypeSummary,tv_unitTypeSummary,tv_prefDateSummary,
            tv_prefTimeSummary,tv_contactNumSummary, tv_back, tv_customerName, tv_bookingName, tv_deleteBtn, tv_paymentMethod,
            tv_time, tv_bookingDesc, tv_month, tv_date, tv_day, tv_techContactNumSummary, tv_custAddressSummary,
            tv_bookPriceSummary, tv_uploadProofOfPayment;
    private ProgressBar progressBar;
    private Button btn_completeBooking;
    private Uri imageUri;

    String imageUrl, custID, bookingIdFromIntent, latString, longString, bookingName;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage, bookingStorage;
    private DatabaseReference userDatabase, projectDatabase, bookingDatabase;
    private StorageTask addTask;
    private String userID, techID, projName, projectIdFromIntent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_booking_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        bookingStorage = FirebaseStorage.getInstance().getReference("Bookings");

        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        setRef();
        generateBookingData();
        clickListeners();
    }

    private void clickListeners() {

        iv_messageCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sender = userID;
                String receiver = custID;
                String chatName = projName;

                String chatUid = sender + "_" + receiver + "_" + chatName;

                Intent intent = new Intent(client_booking_details.this, chat_activity.class);
                intent.putExtra("project id", projectIdFromIntent);
                intent.putExtra("tech id", custID);
                intent.putExtra("sender id", userID);
                intent.putExtra("chat id", chatUid);
                startActivity(intent);
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(client_booking_details.this, my_booking_page.class);
                startActivity(intent);
            }
        });

        tv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelBooking();
            }
        });

        iv_viewInMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String markerTitle = "Technician Location";
                Intent intentProject = new Intent(client_booking_details.this, view_in_map.class);
                intentProject.putExtra("Category", "booking");
                intentProject.putExtra("latString", latString);
                intentProject.putExtra("longString", longString);
                intentProject.putExtra("Marker Title", markerTitle);
                startActivity(intentProject);
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

        btn_completeBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference fileReference = bookingStorage.child(bookingIdFromIntent);

                fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String proofOfPaymentUrl = uri.toString();

                                addProofOfPaymentUrlToDb(proofOfPaymentUrl);
                            }
                        });
                    }
                });
            }
        });
    }

    private void addProofOfPaymentUrlToDb(String proofOfPaymentUrl) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("proofOfPaymentUrl", proofOfPaymentUrl);

        bookingDatabase.child(bookingIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(client_booking_details.this, "Proof of paymnet sent", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(client_booking_details.this, client_booking_details.class);
                intent.putExtra("Booking ID", bookingIdFromIntent);
                startActivity(intent);

            }
        });
    }

    private void cancelBooking() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(client_booking_details.this)
                .setTitle("Cancel Booking?")
                .setMessage("Are you sure you want to cancel this booking?")
                .setCancelable(true)
                .setPositiveButton("Cancel Booking", R.drawable.delete_btn, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        progressDialog = new ProgressDialog(client_booking_details.this);
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


    }

    private void generateNotification() {

        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");

        String sp_notifTitle = "Booking cancelled";
        String sp_notifMessage = "Booking: " + bookingName + " has been cancelled by the customer.";

        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();

        Notification notification = new Notification(imageUrl, sp_notifTitle, sp_notifMessage, cartCreated, techID);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    Intent intent = new Intent(client_booking_details.this, my_booking_page.class);
                    startActivity(intent);

                    Toast.makeText(client_booking_details.this, "Booking Cancelled", Toast.LENGTH_SHORT).show();

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
                        projectIdFromIntent = bookingData.getProjId();
                        projName = bookingData.getProjName();
                        techID = bookingData.getTechID();
                        custID  = bookingData.custID;
                        imageUrl = bookingData.imageUrl;
                        bookingName = bookingData.projName;
                        String sp_bookingtime = bookingData.bookingTime;
                        String sp_bookingDate = bookingData.bookingDate;
                        String sp_addInfo = bookingData.addInfo;
                        String sp_address = bookingData.custAddress;
                        String sp_contactNum = bookingData.custContactNum;
                        String sp_propType = bookingData.propertyType;
                        String sp_airconBrand = bookingData.airconBrand;
                        String sp_airconType = bookingData.airconType;
                        String sp_unitType = bookingData.unitType;
                        String sp_paymentMethod = bookingData.paymentMethod;
                        String sp_price = bookingData.totalPrice;

                        String[] parts = sp_bookingDate.split("/");

                        double price = Double.parseDouble(sp_price);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");

                        tv_time.setText(sp_bookingtime);
                        tv_month.setText(parts[0]);
                        tv_date.setText(parts[1]);
                        tv_day.setText(parts[3]);

                        tv_bookingName.setText(projName);
                        tv_custAddressSummary.setText(sp_address);
                        tv_contactNumSummary.setText(sp_contactNum);
                        tv_propertyTypeSummary.setText(sp_propType);
                        tv_brandSummary.setText(sp_airconBrand);
                        tv_acTypeSummary.setText(sp_airconType);
                        tv_unitTypeSummary.setText(sp_unitType);

                        tv_bookingDesc.setText(sp_addInfo);
                        tv_paymentMethod.setText(sp_paymentMethod);
                        tv_bookPriceSummary.setText("₱ " + twoPlaces.format(price));

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


        Query query = projectDatabase
                .orderByChild("projName")
                .startAt(projName).endAt(projName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    Projects projects = dataSnapshot.getValue(Projects.class);

                    if(projects.getUserID().equals(techID))
                    {
                        tv_addressSummary.setText(projects.getProjAddress());
                        latString = projects.getLatitude();
                        longString = projects.getLongitude();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userDatabase.child(techID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (snapshot.exists())
                {

                    tv_techContactNumSummary.setText(users.contactNum);
                    progressBar.setVisibility(View.GONE);

                    String sp_fName = users.firstName;
                    String sp_lName = users.lastName;
                    String sp_imageUrl = users.imageUrl;
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

            }


        });

    }

    private void setRef() {
        iv_messageCustomer = findViewById(R.id.iv_messageCustomer);
        iv_custPhoto = findViewById(R.id.iv_custPhoto);
        iv_viewInMapBtn = findViewById(R.id.iv_viewInMapBtn);
        tv_paymentMethod = findViewById(R.id.tv_paymentMethod);
        tv_bookPriceSummary = findViewById(R.id.tv_bookPriceSummary);
        iv_proofOfPayment = findViewById(R.id.iv_proofOfPayment);

        tv_deleteBtn = findViewById(R.id.tv_deleteBtn);
        tv_month = findViewById(R.id.tv_month);
        tv_date = findViewById(R.id.tv_date);
        tv_day = findViewById(R.id.tv_day);
        tv_bookingDesc = findViewById(R.id.tv_bookingDesc);
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
        tv_techContactNumSummary = findViewById(R.id.tv_techContactNumSummary);
        tv_custAddressSummary = findViewById(R.id.tv_custAddressSummary);
        tv_uploadProofOfPayment = findViewById(R.id.tv_uploadProofOfPayment);

        progressBar = findViewById(R.id.progressBar);

        btn_completeBooking = findViewById(R.id.btn_completeBooking);


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