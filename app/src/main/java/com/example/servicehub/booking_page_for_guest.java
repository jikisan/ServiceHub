package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterRatingsItem;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class booking_page_for_guest extends AppCompatActivity {

    private Chip chip_Mon, chip_Tue, chip_Wed, chip_Thu, chip_Fri, chip_Sat, chip_Sun;
    private ImageView iv_projectImage;
    private TextView tv_projName;
    private TextView tv_projPrice;
    private TextView tv_back;
    private TextView tv_projDesc;
    private TextView tv_timeAvailable;
    private TextView tv_userRating;
    private TextView tv_viewMorePhoto;
    private Button btn_bookNow;
    private RecyclerView recyclerViewRatings;

    private FirebaseUser user;
    private DatabaseReference projectDatabase;
    private String userID;
    private String projectIdFromIntent;
    private String imageUrlText;
    private String latLng;
    private String tempProjectID;
    private String tempProjName;
    private String tempProjPrice;
    private String tempProjRatings;
    private String tempListName;
    private String listPrice;
    private String listRatings;
    private String techID;
    private Uri tempUri;
    private RatingBar rb_userRating;
    private ProgressBar progressBar;
    private ArrayList<Ratings> arr;
    private AdapterRatingsItem adapterRatingsItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page_for_guest);

        StorageReference projectStorage = FirebaseStorage.getInstance().getReference("Projects");
        projectDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        DatabaseReference listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        DatabaseReference favoriteDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        DatabaseReference cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");

        String listingIdFromIntent = getIntent().getStringExtra("Listing ID");
        projectIdFromIntent = getIntent().getStringExtra("Project ID");

        setRef();
        generateProjDataValue();
        generateRecyclerLayout();
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

        tv_viewMorePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(booking_page_for_guest.this, photo_viewer.class);
                intent.putExtra("project ID", projectIdFromIntent);
                startActivity(intent);
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

        tv_userRating = findViewById(R.id.tv_userRating);
        tv_projName = findViewById(R.id.tv_projName);
        tv_projPrice = findViewById(R.id.tv_projPrice);
        tv_projDesc = findViewById(R.id.tv_projDesc);
        tv_back = findViewById(R.id.tv_back);
        TextView tv_availabilityText = findViewById(R.id.tv_availabilityText);
        tv_timeAvailable = findViewById(R.id.tv_timeAvailable);
        TextView tv_quantity = findViewById(R.id.tv_quantity);

        tv_viewMorePhoto = findViewById(R.id.tv_viewMorePhoto);

        btn_bookNow = findViewById(R.id.btn_bookNow);
        Button btn_orderNow = findViewById(R.id.btn_orderNow);

        chip_Mon = findViewById(R.id.chip_Mon);
        chip_Tue = findViewById(R.id.chip_Tue);
        chip_Wed = findViewById(R.id.chip_Wed);
        chip_Thu = findViewById(R.id.chip_Thu);
        chip_Fri = findViewById(R.id.chip_Fri);
        chip_Sat = findViewById(R.id.chip_Sat);
        chip_Sun = findViewById(R.id.chip_Sun);

        progressBar = findViewById(R.id.progressBar);

        rb_userRating = findViewById(R.id.rb_userRating);

        recyclerViewRatings = findViewById(R.id.recyclerViewRatings);

        View layout_favorite = findViewById(R.id.layout_favorite);
        View layout_cart = findViewById(R.id.layout_cart);

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
                        int sp_ratingCount = projectData.getRatingCount();
                        double sp_ratingAverage = projectData.getRatingAverage();
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
                        tv_userRating.setText("("+sp_ratingCount+")");
                        rb_userRating.setRating((float) sp_ratingAverage);
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

    private void generateRecyclerLayout() {

        recyclerViewRatings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewRatings.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterRatingsItem = new AdapterRatingsItem(arr);
        recyclerViewRatings.setAdapter(adapterRatingsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        DatabaseReference ratingsDatabase = FirebaseDatabase.getInstance().getReference("Ratings");

        Query query = ratingsDatabase
                .orderByChild("ratingOfId")
                .equalTo(projectIdFromIntent);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Ratings ratings = dataSnapshot.getValue(Ratings.class);
                    arr.add(ratings);
                }
                adapterRatingsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}