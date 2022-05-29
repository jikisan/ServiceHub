package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapter_and_fragments.AdapterViewPagerPhotoFullscreen;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class photo_fullscreen_view_page extends AppCompatActivity {


    private AdapterViewPagerPhotoFullscreen adapterViewPagerPhotoFullscreen;
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private String projectIdFromIntent, category, imageName;
    private int currentPosition;
    private DatabaseReference photoDatabase;

    private ImageView iv_deletPhoto;
    private TextView tv_back;
    private ViewPager vp_photoFullscreen;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_fullscreen_view_page);
        projectIdFromIntent = getIntent().getStringExtra("Project ID");
        currentPosition = getIntent().getIntExtra("current position", 0);
        category = getIntent().getStringExtra("category");

        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        setRef();

        if(category.equals("viewer"))
        {
            iv_deletPhoto.setVisibility(View.GONE);
        }

        getViewHolderValues();
        generateImageData();
        clickListeners();
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_deletPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference photoDB = FirebaseDatabase.getInstance().getReference("Photos");
                StorageReference photoStorage = FirebaseStorage.getInstance().getReference("Projects").child(projectIdFromIntent);



                new SweetAlertDialog(photo_fullscreen_view_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!.")
                        .setContentText("Delete " + imageName + "?")
                        .setCancelText("Cancel")
                        .setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                deletePhotoInDb(photoDB, photoStorage, imageName, projectIdFromIntent);

                            }
                        })
                        .show();
            }
        });
    }

    private void deletePhotoInDb(DatabaseReference photoDB, StorageReference photoStorage, String imageName, String projID) {
        progressDialog = new ProgressDialog(photo_fullscreen_view_page.this);
        progressDialog.setTitle("Deleting photo: " + imageName );
        progressDialog.show();

        Query query = photoDB
                .orderByChild("photoName")
                .equalTo(imageName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = photoStorage.child(imageName);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    imageRef.delete();
                    dataSnapshot.getRef().removeValue();

                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();


                        if(category.equals("add"))
                        {
                            Toast.makeText(photo_fullscreen_view_page.this, "Photo: " + imageName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(photo_fullscreen_view_page.this, add_photos.class);
                            intent.putExtra("Project ID", projID);
                            photo_fullscreen_view_page.this.startActivity(intent);
                        }
                        else if(category.equals("viewer"))
                        {
                            Toast.makeText(photo_fullscreen_view_page.this, "Photo: " + imageName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(photo_fullscreen_view_page.this, photo_viewer.class);
                            intent.putExtra("Project ID", projID);
                            photo_fullscreen_view_page.this.startActivity(intent);
                        }

                    }
                }, 4000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateImageData() {

        // Initializing the ViewPagerAdapter
        adapterViewPagerPhotoFullscreen = new AdapterViewPagerPhotoFullscreen(photo_fullscreen_view_page.this, arrUrl, currentPosition, category);

        // Adding the Adapter to the ViewPager
        vp_photoFullscreen.setAdapter(adapterViewPagerPhotoFullscreen);

        vp_photoFullscreen.postDelayed(new Runnable() {

            @Override
            public void run() {
                adapterViewPagerPhotoFullscreen.notifyDataSetChanged();
                vp_photoFullscreen.setCurrentItem(currentPosition);
            }
        }, 500);
//        vp_photoFullscreen.setCurrentItem(currentPosition);

        getViewHolderValues();

    }

    private void getViewHolderValues() {
        Query query = photoDatabase
                .orderByChild("projID")
                .equalTo(projectIdFromIntent);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Photos photos = dataSnapshot.getValue(Photos.class);
                    imageName = photos.getPhotoName().toString();
                    arrUrl.add(photos);
                }

//                adapterViewPagerPhotoFullscreen.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRef() {

        vp_photoFullscreen = findViewById(R.id.vp_photoFullscreen);
        tv_back = findViewById(R.id.tv_back);
        iv_deletPhoto = findViewById(R.id.iv_deletPhoto);

    }
}