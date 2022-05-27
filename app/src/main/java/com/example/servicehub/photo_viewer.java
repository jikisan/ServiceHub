package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter_and_fragments.AdapterPhotoItem;

public class photo_viewer extends AppCompatActivity {

    private RecyclerView rv_photos;
    private TextView tv_back;

    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private AdapterPhotoItem adapterPhotoItem;
    private int uploads = 0;
    private String projectIdFromIntent;
    private DatabaseReference photoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        projectIdFromIntent = getIntent().getStringExtra("project ID");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        setRef();
        generateRecyclerLayout();
        clickListeners();
    }

    private void setRef() {
        tv_back = findViewById(R.id.tv_back);

        rv_photos = findViewById(R.id.rv_photos);

    }

    private void generateRecyclerLayout() {
        rv_photos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(photo_viewer.this, 3, GridLayoutManager.VERTICAL, false);
        rv_photos.setLayoutManager(gridLayoutManager);

        adapterPhotoItem = new AdapterPhotoItem(arrUrl);
        rv_photos.setAdapter(adapterPhotoItem);

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
                    arrUrl.add(photos);
                }

                adapterPhotoItem.notifyDataSetChanged();
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
                onBackPressed();


            }
        });

    }
}