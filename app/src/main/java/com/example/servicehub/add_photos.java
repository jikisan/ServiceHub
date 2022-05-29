package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterPhotoItem;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class add_photos extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    private ArrayList<Uri> arrImageList = new ArrayList<Uri>();
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private AdapterPhotoItem adapterPhotoItem;
    private int uploads = 0;

    private Button btn_addPhoto, btn_choose;
    private TextView tv_back, tv_summary;
    private RecyclerView rv_photos;
    private String projectIdFromIntent;
    private ProgressDialog progressDialog;

    private DatabaseReference photoDatabase;
    private StorageReference projectPhotoStorage, listingPhotoStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photos);

        projectIdFromIntent = getIntent().getStringExtra("Project ID");

        listingPhotoStorage = FirebaseStorage.getInstance().getReference("Listings");
        projectPhotoStorage = FirebaseStorage.getInstance().getReference("Projects");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        setRef();
        generateRecyclerLayout();
        clickListeners();

    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_photos.this, edit_project_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                startActivity(intent);
            }
        });

        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( arrImageList.size() <= 0)
                {
                    Toast.makeText(add_photos.this, "Please choose a photo", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog = new ProgressDialog(add_photos.this);
                    progressDialog.setTitle("Uploading photos");
                    progressDialog.show();

                    uploadPhotos();
                }

            }
        });
        
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMG);

            }
        });

        adapterPhotoItem.setOnItemClickListener(new AdapterPhotoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(add_photos.this, photo_fullscreen_view_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                intent.putExtra("current position", position);
                intent.putExtra("category", "add");
                startActivity(intent);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        arrImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }

                    tv_summary.setVisibility(View.VISIBLE);
                    tv_summary.setText("You Have Selected "+ arrImageList.size() +" Pictures" );

                }
                else {
                    Uri imageuri = data.getData();
                    if (imageuri != null) {

                        arrImageList.add(imageuri);
                        tv_summary.setVisibility(View.VISIBLE);
                        tv_summary.setText("You Have Selected "+ arrImageList.size() +" Picture" );
                    }
                }

            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void uploadPhotos() {

        for (uploads=0; uploads < arrImageList.size(); uploads++)
        {
            Uri Image  = arrImageList.get(uploads);

            long imageTime = System.currentTimeMillis();
            String imageName = imageTime + Image.getLastPathSegment().toString();

            StorageReference imagename = projectPhotoStorage.child(projectIdFromIntent+"/"+ imageName);

            imagename.putFile(arrImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url, imageName);
                        }
                    });

                }
            });

        }

    }

    private void SendLink(String url, String imageName) {

        Photos photos = new Photos(projectIdFromIntent, url, imageName);

        photoDatabase.push().setValue(photos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                arrImageList.clear();
                progressDialog.dismiss();
            }
        });
    }

    private void generateRecyclerLayout() {
        rv_photos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(add_photos.this, 3, GridLayoutManager.VERTICAL, false);
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

    private void setRef() {

        btn_addPhoto = findViewById(R.id.btn_addPhoto);
        btn_choose = findViewById(R.id.btn_choose);

        tv_back = findViewById(R.id.tv_back);
        tv_summary = findViewById(R.id.tv_summary);

        rv_photos = findViewById(R.id.rv_photos);

    }
}