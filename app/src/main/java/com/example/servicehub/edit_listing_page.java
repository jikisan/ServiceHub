package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class edit_listing_page extends AppCompatActivity {


    private FirebaseUser user;
    private DatabaseReference listingDatabase;
    private String userID, imageUriText;

    ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn, iv_listingImage, iv_decreaseBtn, iv_increaseBtn;
    TextView tv_uploadPhoto, tv_address, tv_quantity;
    EditText et_listingName, et_price, et_listDesc;
    Button btn_save, btn_delete;
    Uri imageUri;
    int quantity = 1;
    String quantityText, latLng, listingIdFromIntent;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_listing_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings").child(userID);


        generateDataValue();
        setRef();
        initPlaces();
        adjustQuantity();
        clickListener();
        bottomNavTaskbar();
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        //Set edittext no focusable
        tv_address.setFocusable(false);
    }

    private void clickListener() {

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
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(edit_listing_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateListing();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listingIdFromIntent = getIntent().getStringExtra("Listing ID");
                listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Toast.makeText(edit_listing_page.this, "Listing Deleted", Toast.LENGTH_SHORT).show();
                            dataSnapshot.getRef().removeValue();
                            Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                    iv_listingImage.setImageBitmap(bitmap);

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

    private void adjustQuantity() {
        quantityText = String.valueOf(quantity);
        tv_quantity.setText(quantityText);

        iv_decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String quantityText;

                if (quantity == 1){
                    quantity = 1;
                    Toast.makeText(edit_listing_page.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                    quantityText = String.valueOf(quantity);
                    tv_quantity.setText(quantityText);
                }
                else
                {
                    quantity = quantity - 1;
                    quantityText = String.valueOf(quantity);
                    tv_quantity.setText(quantityText);
                }
            }
        });

        iv_increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quantity = quantity + 1;
                quantityText = String.valueOf(quantity);
                tv_quantity.setText(quantityText);

            }
        });
    }

    private void updateListing() {
        String listName = et_listingName.getText().toString();
        String listPrice = et_price.getText().toString();
        String listQuantity = tv_quantity.getText().toString();
        String listDesc = et_listDesc.getText().toString();
        String imageUriText = imageUri.toString();


        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("listName", listName);
        hashMap.put("listAddress", latLng);
        hashMap.put("listPrice", listPrice);
        hashMap.put("listQuantity", listQuantity);
        hashMap.put("listDesc", listDesc);
        hashMap.put("imageUri", imageUriText);

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");
        listingDatabase.child(listingIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(edit_listing_page.this, "Listing is updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
                startActivity(intent);

            }
        });
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(edit_listing_page.this, "Listing Updated", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(edit_listing_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


//
//        Listings listings = new Listings(imageUriText, listName, latLng, listPrice, listQuantity, listDesc);
//
//        listingDatabase.child(listingID).setValue(listings).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(edit_listing_page.this, "Listing Added", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(edit_listing_page.this, tech_dashboard.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(edit_listing_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    private void generateDataValue() {

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");

        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listingData = snapshot.getValue(Listings.class);

                if (listingData != null){
                    try {
                        imageUriText = listingData.imageUri;
                        String sp_listName = listingData.getListName();
                        String sp_listAddress = listingData.getListAddress();
                        String sp_listPrice = listingData.getListPrice();
                        String sp_listQuantity = listingData.getListQuantity();
                        String sp_listDesc = listingData.getListDesc();

                        imageUri = Uri.parse(imageUriText);

                        InputStream stream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);

                        iv_listingImage.setImageBitmap(bitmap);
                        et_listingName.setText(sp_listName);
                        tv_address.setText(sp_listAddress);
                        et_price.setText(sp_listPrice);
                        tv_quantity.setText(sp_listQuantity);
                        et_listDesc.setText(sp_listDesc);

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("LISTINGS LOG JK:" + e.getMessage());

                    }
                }
                else
                {
                    Toast.makeText(edit_listing_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("LISTINGS LOG JK: Empty");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(edit_listing_page.this, "Empty", Toast.LENGTH_SHORT).show();
                System.out.println("LISTINGS LOG JK: Empty");

            }
        });
    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(edit_listing_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(edit_listing_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(edit_listing_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(edit_listing_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(edit_listing_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_listingImage = findViewById(R.id.iv_listingImage);
        iv_decreaseBtn = findViewById(R.id.iv_decreaseBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_increaseBtn = findViewById(R.id.iv_increaseBtn);
        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_address = findViewById(R.id.tv_address);
        et_listingName = findViewById(R.id.et_listingName);
        et_price = findViewById(R.id.et_price);
        et_listDesc = findViewById(R.id.et_listDesc);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);

    }
}