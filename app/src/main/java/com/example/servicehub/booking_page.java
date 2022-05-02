package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class booking_page extends AppCompatActivity {

    private Chip chip_Mon, chip_Tue, chip_Wed, chip_Thu, chip_Fri, chip_Sat, chip_Sun;
    private ImageView iv_projectImage,  iv_message, iv_cart;
    private TextView tv_projName, iv_projRating, tv_projPrice, tv_back, tv_projDesc, tv_availabilityText;
    private Button btn_bookNow;

    private FirebaseUser user;
    private FirebaseStorage mStorage;
    private StorageReference projectStorage;
    private DatabaseReference projectDatabase, listingDatabase;
    private StorageTask addTask;
    private String userID, projectIdFromIntent, listingIdFromIntent, imageUriText, latLng,  tempProjName, tempListName;
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
                Intent intent = new Intent(booking_page.this, cart_page.class);
                startActivity(intent);
            }
        });

        btn_bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getKey();
                Intent intent = new Intent(booking_page.this, booking_application_page.class);
                startActivity(intent);
            }
        });

    }

    private void setRef() {

        iv_projectImage = findViewById(R.id.iv_projectImage);
        iv_message = findViewById(R.id.iv_message);
        iv_cart = findViewById(R.id.iv_cart);
        tv_projName = findViewById(R.id.tv_projName);
        iv_projRating = findViewById(R.id.tv_projRating);

        tv_projPrice = findViewById(R.id.tv_projPrice);
        tv_projDesc = findViewById(R.id.tv_projDesc);
        tv_back = findViewById(R.id.tv_back);
        tv_availabilityText = findViewById(R.id.tv_availabilityText);


        btn_bookNow = findViewById(R.id.btn_bookNow);

        chip_Mon = findViewById(R.id.chip_Mon);
        chip_Tue = findViewById(R.id.chip_Tue);
        chip_Wed = findViewById(R.id.chip_Wed);
        chip_Thu = findViewById(R.id.chip_Thu);
        chip_Fri = findViewById(R.id.chip_Fri);
        chip_Sat = findViewById(R.id.chip_Sat);
        chip_Sun = findViewById(R.id.chip_Sun);

        progressBar = findViewById(R.id.progressBar);

    }

    private void generateProjDataValue() {


        projectDatabase.child(projectIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projectData = snapshot.getValue(Projects.class);

                if(projectData != null){
                    try{
                        tempProjName = projectData.getProjName();

                        imageUriText = projectData.getImageUrl();
                        String sp_category = projectData.getCategory();
                        String sp_ratings = projectData.getRatings();
                        String sp_projName = projectData.getProjName().substring(0, 1).toUpperCase()
                                            + projectData.getProjName().substring(1).toLowerCase();
                        String sp_projPrice = projectData.getPrice();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();
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
                        iv_projRating.setText(sp_ratings);
                        tv_projPrice.setText("₱ " + twoPlaces.format(price) + " /Job");
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
                        iv_projRating.setText(sp_ratings);
                        tv_projPrice.setText("₱ " + twoPlaces.format(price) + " /Job");
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

    private void getKey() {


        Query query = FirebaseDatabase.getInstance().getReference("Projects")
                .orderByChild("projName")
                .equalTo(tempProjName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String projectName = dataSnapshot.getKey().toString();
                    Intent intentProject = new Intent(booking_page.this, edit_project_page.class);
                    intentProject.putExtra("Project Name", projectName);
                    startActivity(intentProject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}