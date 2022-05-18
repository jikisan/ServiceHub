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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class booking_page_for_guest extends AppCompatActivity {

    private Chip chip_Mon, chip_Tue, chip_Wed, chip_Thu, chip_Fri, chip_Sat, chip_Sun;
    private ImageView iv_projectImage;
    private TextView tv_projName, tv_projRating, tv_projPrice, tv_back, tv_projDesc, tv_availabilityText, tv_timeAvailable, tv_quantity;
    private Button btn_bookNow, btn_orderNow;
    private View layout_favorite, layout_cart;

    private FirebaseUser user;
    private DatabaseReference projectDatabase, listingDatabase, favoriteDatabase, cartDatabase;
    private String userID, projectIdFromIntent, listingIdFromIntent, imageUrlText, latLng,  tempProjectID, tempProjName,
            tempProjPrice, tempProjRatings, tempListName, listPrice, listRatings, techID;
    private Uri tempUri;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page_for_guest);

        StorageReference projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        favoriteDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");

        listingIdFromIntent = getIntent().getStringExtra("Listing ID");
        projectIdFromIntent = getIntent().getStringExtra("Project ID");

        setRef();
        generateProjDataValue();
        clickListeners();
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user == null)
                {
                    loginValidation();
                }
                else
                {

                    Intent intent = new Intent(booking_page_for_guest.this, booking_application_page.class);
                    intent.putExtra("projectIdFromIntent", tempProjectID);
                    startActivity(intent);
                }
            }
        });

    }

    private void loginValidation(){

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(booking_page_for_guest.this)
                .setTitle("Please log in first to proceed")
                .setMessage("You must login to proceed")
                .setCancelable(true)
                .setPositiveButton("Go to Login", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        Intent intent = new Intent(booking_page_for_guest.this, login_page.class);
                        intent.putExtra("user", "guest");
                        intent.putExtra("projectIdFromIntent", tempProjectID);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Back", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();


    }

    private void setRef() {

        iv_projectImage = findViewById(R.id.iv_projPhotoSummary);
        tv_projRating = findViewById(R.id.tv_projRating);

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
                        techID = projectData.getUserID();

                        imageUrlText = projectData.getImageUrl();
                        String sp_category = projectData.getCategory();
                        String sp_ratings = projectData.getRatings();
                        tempProjName = projectData.getProjName();
                        String sp_projPrice = projectData.getPrice();
                        String sp_projSpecialInstruction = projectData.getProjInstruction();
                        String sp_startTime = projectData.getStartTime();
                        String sp_endTime = projectData.getEndTime();
                        tempProjRatings = projectData.getRatings();

                        boolean sp_isAvailableMon = projectData.isAvailableMon();
                        boolean sp_isAvailableTue = projectData.isAvailableTue();
                        boolean sp_isAvailableWed = projectData.isAvailableWed();
                        boolean sp_isAvailableThu = projectData.isAvailableThu();
                        boolean sp_isAvailableFri = projectData.isAvailableFri();
                        boolean sp_isAvailableSat = projectData.isAvailableSat();
                        boolean sp_isAvailableSun = projectData.isAvailableSun();


                        tempUri = Uri.parse(imageUrlText);


                        Picasso.get()
                                .load(tempUri)
                                .resize(800, 600)
                                .into(iv_projectImage);

                        double price = Double.parseDouble(sp_projPrice);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");
                        tempProjPrice = twoPlaces.format(price);

                        tv_back.setText(sp_category);
                        tv_projName.setText(tempProjName);
                        tv_projRating.setText(sp_ratings);
                        tv_projPrice.setText("₱ " + tempProjPrice + " /Job");
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
                    Toast.makeText(booking_page_for_guest.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(booking_page_for_guest.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}