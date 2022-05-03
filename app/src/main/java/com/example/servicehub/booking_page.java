package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
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
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class booking_page extends AppCompatActivity {

    private Chip chip_Mon, chip_Tue, chip_Wed, chip_Thu, chip_Fri, chip_Sat, chip_Sun;
    private ImageView iv_projectImage,  iv_message, iv_cart, iv_favorite;
    private TextView tv_projName, tv_projRating, tv_projPrice, tv_back, tv_projDesc, tv_availabilityText, tv_timeAvailable, tv_quantity;
    private Button btn_bookNow, btn_orderNow;
    private View layout_favorite, layout_cart;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage;
    private DatabaseReference projectDatabase, listingDatabase, favoriteDatabase, cartDatabase;
    private StorageTask addTask;
    private String userID, projectIdFromIntent, listingIdFromIntent, imageUriText, latLng,  tempProjectID, tempListName;
    private Uri imageUri, tempUri;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        favoriteDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");
        projectIdFromIntent = getIntent().getStringExtra("Project ID");

        setRef();
        if(projectIdFromIntent == null || projectIdFromIntent == ""){


              generateListDataValue();
        }
        else
        {
            generateProjDataValue();
        }

        clickListeners();
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(booking_page.this, message_page.class);
                startActivity(intent);

            }
        });

        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToCart();

            }
        });

        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToFavorite();

            }
        });

        btn_bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getKey();
                Intent intent = new Intent(booking_page.this, booking_application_page.class);
                intent.putExtra("projectIdFromIntent", tempProjectID);
                startActivity(intent);
            }
        });

    }

    private void addToCart() {

        cartDatabase
                .orderByChild("listingID")
                .equalTo(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    new SweetAlertDialog(booking_page.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Item already in Cart")
                            .setCancelText("Back")
                            .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                }
                            })
                            .setContentText("Go to cart?")
                            .show();
                } else {
                    //Project ID doesn't exists.
                    Date currentTime = Calendar.getInstance().getTime();
                    String cartCreated = currentTime.toString();
                    Cart cart = new Cart(userID, listingIdFromIntent, cartCreated);

                    cartDatabase.push().setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                new SweetAlertDialog(booking_page.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Item is added to Cart!")
                                        .setCancelText("Back")
                                        .setContentText("Go to Cart?")
                                        .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            }
                                        })
                                        .show();


                            } else {
                                Toast.makeText(booking_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addToFavorite() {

        favoriteDatabase
                .orderByChild("projID")
                .equalTo(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    new SweetAlertDialog(booking_page.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Project already in Favorites")
                            .setCancelText("Back")
                            .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                }
                            })
                            .setContentText("Go to favorite?")
                            .show();
                } else {
                    //Project ID doesn't exists.
                    Date currentTime = Calendar.getInstance().getTime();
                    String favoriteCreated = currentTime.toString();
                    Favorites favorites = new Favorites(userID, projectIdFromIntent, favoriteCreated);

                    favoriteDatabase.push().setValue(favorites).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                new SweetAlertDialog(booking_page.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Project is added to favorites!")
                                        .setCancelText("Back")
                                        .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent intent = new Intent(booking_page.this, cart_page.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setContentText("Go to favorite?")
                                        .show();


                            } else {
                                Toast.makeText(booking_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setRef() {

        iv_projectImage = findViewById(R.id.iv_projPhotoSummary);
        iv_message = findViewById(R.id.iv_message);
        iv_cart = findViewById(R.id.iv_cart);

        tv_projRating = findViewById(R.id.tv_projRating);
        iv_favorite = findViewById(R.id.iv_favorite);

        tv_projRating = findViewById(R.id.tv_projRating);
        tv_projName = findViewById(R.id.tv_projName);
        tv_projPrice = findViewById(R.id.tv_projPrice);
        tv_projDesc = findViewById(R.id.tv_projDesc);
        tv_back = findViewById(R.id.tv_back);
        tv_availabilityText = findViewById(R.id.tv_availabilityText);
        tv_timeAvailable = findViewById(R.id.tv_timeAvailable);
        tv_quantity = findViewById(R.id.tv_quantity);

        btn_bookNow = findViewById(R.id.btn_bookNow);
        btn_orderNow = findViewById(R.id.btn_orderNow);

        chip_Mon = findViewById(R.id.chip_Mon);
        chip_Tue = findViewById(R.id.chip_Tue);
        chip_Wed = findViewById(R.id.chip_Wed);
        chip_Thu = findViewById(R.id.chip_Thu);
        chip_Fri = findViewById(R.id.chip_Fri);
        chip_Sat = findViewById(R.id.chip_Sat);
        chip_Sun = findViewById(R.id.chip_Sun);

        progressBar = findViewById(R.id.progressBar);

        layout_favorite = findViewById(R.id.layout_favorite);
        layout_cart = findViewById(R.id.layout_cart);

    }

    private void generateProjDataValue() {

        projectDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projectData = snapshot.getValue(Projects.class);

                if(projectData != null){
                    try{
                        tempProjectID = projectIdFromIntent;

                        imageUriText = projectData.getImageUrl();
                        String sp_category = projectData.getCategory();
                        String sp_ratings = projectData.getRatings();
                        String sp_projName = projectData.getProjName().substring(0, 1).toUpperCase()
                                            + projectData.getProjName().substring(1).toLowerCase();
                        String sp_projPrice = projectData.getPrice();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();
                        String sp_startTime = projectData.getStartTime();
                        String sp_endTime = projectData.getEndTime();
                        boolean sp_isAvailableMon = projectData.isAvailableMon();
                        boolean sp_isAvailableTue = projectData.isAvailableTue();
                        boolean sp_isAvailableWed = projectData.isAvailableWed();
                        boolean sp_isAvailableThu = projectData.isAvailableThu();
                        boolean sp_isAvailableFri = projectData.isAvailableFri();
                        boolean sp_isAvailableSat = projectData.isAvailableSat();
                        boolean sp_isAvailableSun = projectData.isAvailableSun();


                        tempUri = Uri.parse(imageUriText);
                        double price = Double.parseDouble(sp_projPrice);

                        Picasso.get()
                                .load(tempUri)
                                .resize(800, 600)
                                .into(iv_projectImage);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");

                        tv_back.setText(sp_category);
                        tv_projName.setText(sp_projName);
                        tv_projRating.setText(sp_ratings);
                        tv_projPrice.setText("₱ " + twoPlaces.format(price) + " /Job");
                        tv_timeAvailable.setText(sp_startTime + " - " + sp_endTime);
                        tv_projDesc.setText(sp_projSpecialInstruction);

                        if(sp_isAvailableMon == true){
                            chip_Mon.setVisibility(View.VISIBLE);
                            chip_Mon.setChecked(true);
                            chip_Mon.setClickable(false);
                        }
                        if(sp_isAvailableTue == true){
                            chip_Tue.setVisibility(View.VISIBLE);
                            chip_Tue.setChecked(true);
                            chip_Tue.setClickable(false);
                        }
                        if(sp_isAvailableWed == true){
                            chip_Wed.setVisibility(View.VISIBLE);
                            chip_Wed.setChecked(true);
                            chip_Wed.setClickable(false);
                        }
                        if(sp_isAvailableThu == true){
                            chip_Thu.setVisibility(View.VISIBLE);
                            chip_Thu.setChecked(true);
                            chip_Thu.setClickable(false);
                        }
                        if(sp_isAvailableFri == true){

                            chip_Fri.setVisibility(View.VISIBLE);
                            chip_Fri.setChecked(true);
                            chip_Fri.setClickable(false);
                        }
                        if(sp_isAvailableSat == true){

                            chip_Sat.setVisibility(View.VISIBLE);
                            chip_Sat.setChecked(true);
                            chip_Sat.setClickable(false);
                        }
                        if(sp_isAvailableSun == true){

                            chip_Sun.setVisibility(View.VISIBLE);
                            chip_Sun.setChecked(true);
                            chip_Sun.setClickable(false);
                        }

                        progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(booking_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booking_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateListDataValue() {
        tv_availabilityText.setVisibility(View.GONE);
        tv_timeAvailable.setVisibility(View.GONE);
        btn_bookNow.setVisibility(View.GONE);
        layout_favorite.setVisibility(View.GONE);
        layout_cart.setVisibility(View.VISIBLE);
        tv_quantity.setVisibility(View.VISIBLE);
        btn_orderNow.setVisibility(View.VISIBLE);


        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listingsData = snapshot.getValue(Listings.class);

                if(listingsData != null){
                    try{
                        imageUriText = listingsData.getImageUrl();
                        String sp_ratings = listingsData.getRatings();
                        String sp_projName = listingsData.getListName().substring(0, 1).toUpperCase()
                                + listingsData.getListName().substring(1).toLowerCase();
                        String sp_projPrice = listingsData.getListPrice();
                        String sp_quantity = listingsData.getListQuantity();
                        String sp_projSpecialInstruction = listingsData.getListDesc();

                        tempUri = Uri.parse(imageUriText);
                        double price = Double.parseDouble(sp_projPrice);

                        Picasso.get()
                                .load(tempUri)
                                .resize(800, 600)
                                .into(iv_projectImage);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");

                        tv_back.setText("Marketplace");
                        tv_projName.setText(sp_projName);
                        tv_projRating.setText(sp_ratings);
                        tv_projPrice.setText("₱ " + twoPlaces.format(price) + " /Job");
                        tv_quantity.setText(sp_quantity + " Pieces Available");
                        tv_projDesc.setText(sp_projSpecialInstruction);


                        progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(booking_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booking_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}