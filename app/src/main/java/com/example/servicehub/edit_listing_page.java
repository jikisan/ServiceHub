package com.example.servicehub;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
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

public class edit_listing_page extends AppCompatActivity {


    private FirebaseUser user;
    private DatabaseReference listingDatabase;
    private StorageReference listingStorage;
    private StorageTask addTask;
    private String userID, imageUriText, tempImageName;
    private Geocoder geocoder;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn, iv_pickAddress,
            iv_moreBtn, iv_listingImage, iv_decreaseBtn, iv_increaseBtn, btn_delete;
    private TextView tv_uploadPhoto, tv_address, tv_quantity, tv_back;
    private EditText et_listingName, et_price, et_listDesc;
    private Button btn_save;
    private Uri imageUri;
    private int quantity = 1;
    private String quantityText, listingIdFromIntent, latString, longString;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_listing_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingStorage = FirebaseStorage.getInstance().getReference("Listings").child("Banner");
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");


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

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(edit_listing_page.this)
                        .setIcon(R.drawable.logo)
                        .setTitle("Delete Application")
                        .setMessage("Are you sure that you want to permanently delete this listing?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                listingIdFromIntent = getIntent().getStringExtra("Listing ID");
                                listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Listings listingsData = snapshot.getValue(Listings.class);

                                        String imageName = listingsData.getImageName();

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            StorageReference imageRef = listingStorage.child(imageName);

                                            imageRef.delete();
                                            dataSnapshot.getRef().removeValue();

                                            Toast.makeText(edit_listing_page.this, "Listing Deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
                                            startActivity(intent);
                                        }
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
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(edit_listing_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addTask != null && addTask.isInProgress()) {
                    Toast.makeText(edit_listing_page.this, "In progress", Toast.LENGTH_SHORT).show();

                } else {

                    inputValidation();
                }
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        et_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputEditTextField= new EditText(edit_listing_page.this);
                inputEditTextField.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(edit_listing_page.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Enter Price:");
                builderSingle.setView(inputEditTextField);
                builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String editTextInput = inputEditTextField.getText().toString();
                        double servicePrice = Double.parseDouble(editTextInput);
                        double percentageFee = servicePrice * .15;

                        et_price.setText(String.valueOf(servicePrice));
                    }
                });

                builderSingle.show();
            }
        });

        iv_pickAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(edit_listing_page.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select Address:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(edit_listing_page.this, android.R.layout.select_dialog_singlechoice);

                DatabaseReference myAddressDatabase = FirebaseDatabase.getInstance().getReference("Address");

                Query query = myAddressDatabase
                        .orderByChild("custID")
                        .equalTo(userID);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            MyAddress myAddress = dataSnapshot.getValue(MyAddress.class);
                            String addrses = myAddress.getAddressValue();
                            latString = myAddress.getLatString();
                            longString = myAddress.getLongString();

                            arrayAdapter.add(addrses);
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                builderSingle.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addressFromDialog = arrayAdapter.getItem(which);
                        tv_address.setText(addressFromDialog);
                        dialog.dismiss();
                    }
                });

                builderSingle.show();

            }
        });
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
        iv_pickAddress = findViewById(R.id.iv_pickAddress);

        tv_uploadPhoto = findViewById(R.id.tv_uploadPhoto);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_address = findViewById(R.id.et_address);
        tv_back = findViewById(R.id.tv_back);

        et_listingName = findViewById(R.id.et_listingName);
        et_price = findViewById(R.id.et_price);
        et_listDesc = findViewById(R.id.et_listDesc);

        btn_save = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

    }

    private void inputValidation() {
        String sp_listingName = et_listingName.getText().toString();
        String sp_address = tv_address.getText().toString();
        String sp_price = et_price.getText().toString();
        String sp_quantity = tv_quantity.getText().toString();

        if (TextUtils.isEmpty(sp_listingName)){
            Toast.makeText(this, "Listing name is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(sp_address)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(sp_price)){
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(sp_quantity)){
            Toast.makeText(this, "Quantity is required", Toast.LENGTH_SHORT).show();
        }else{
            new AlertDialog.Builder(edit_listing_page.this)
                    .setIcon(R.drawable.logo)
                    .setTitle("ServiceHUB")
                    .setMessage("Please make sure all information entered are correct")
                    .setCancelable(true)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(imageUri == null)
                            {
                                 updateListingNoImage();
                            }
                            else
                            {
                                updateListing();
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
//                    iv_listingImage.setImageBitmap(bitmap);

                    Picasso.get().load(imageUri)
                            .into(iv_listingImage);

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        else if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                latString = String.valueOf(address.get(0).getLatitude());
                longString = String.valueOf(address.get(0).getLongitude());
                String addressText =  place.getAddress().toString();

                tv_address.setText(addressText);

            } catch (IOException e) {
                e.printStackTrace();
            }

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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference fileReference = listingStorage.child(imageUri.getLastPathSegment());

        String listName = et_listingName.getText().toString();
        String listAddress = tv_address.getText().toString();
        String listPrice = et_price.getText().toString();
        String listQuantity = tv_quantity.getText().toString();
        String listDesc = et_listDesc.getText().toString();
        String imageName = imageUri.getLastPathSegment();


        addTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String downloadUrl = uri.toString();

                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("imageUrl", downloadUrl);
                        hashMap.put("imageName", imageName);
                        hashMap.put("listName", listName);
                        hashMap.put("latitude", latString);
                        hashMap.put("longitude", longString);
                        hashMap.put("listAddress", listAddress);
                        hashMap.put("listPrice", listPrice);
                        hashMap.put("listQuantity", listQuantity);
                        hashMap.put("listDesc", listDesc);

                        listingIdFromIntent = getIntent().getStringExtra("Listing ID");
                        listingDatabase.child(listingIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {

                                StorageReference imageRef = listingStorage.child(tempImageName);
                                imageRef.delete();

                                progressDialog.dismiss();
                                Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
                                startActivity(intent);
                                Toast.makeText(edit_listing_page.this, "Listing is updated", Toast.LENGTH_SHORT).show();


                            }
                        });


                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(edit_listing_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void updateListingNoImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String listName = et_listingName.getText().toString();
        String listAddress = tv_address.getText().toString();
        String listPrice = et_price.getText().toString();
        String listQuantity = tv_quantity.getText().toString();
        String listDesc = et_listDesc.getText().toString();

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("listName", listName);
        hashMap.put("latitude", latString);
        hashMap.put("longitude", longString);
        hashMap.put("listAddress", listAddress);
        hashMap.put("listPrice", listPrice);
        hashMap.put("listQuantity", listQuantity);
        hashMap.put("listDesc", listDesc);

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");
        listingDatabase.child(listingIdFromIntent).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                progressDialog.dismiss();
                Intent intent = new Intent(edit_listing_page.this, seller_dashboard.class);
                startActivity(intent);
                Toast.makeText(edit_listing_page.this, "Listing is updated", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void generateDataValue() {

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");

        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listingData = snapshot.getValue(Listings.class);

                if (listingData != null){
                    try {
                        imageUriText = listingData.getImageUrl();
                        String sp_listName = listingData.getListName();
                        String sp_listAddress = listingData.getListAddress();
                        String sp_listPrice = listingData.getListPrice();
                        String sp_listQuantity = listingData.getListQuantity();
                        String sp_listDesc = listingData.getListDesc();

                        tempImageName = listingData.getImageName();

                        Picasso.get()
                                .load(imageUriText)
                                .into(iv_listingImage);

                        et_listingName.setText(sp_listName);
                        tv_address.setText(sp_listAddress);
                        et_price.setText(sp_listPrice);
                        tv_quantity.setText(sp_listQuantity);
                        et_listDesc.setText(sp_listDesc);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(edit_listing_page.this, "Empty", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(edit_listing_page.this, "Empty", Toast.LENGTH_SHORT).show();

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

    private void PickImage() {
        CropImage.activity().start(this);
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
}