package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapter_and_fragments.AdapterPhotoItem;
import Adapter_and_fragments.AdapterVideoItem;


public class add_videos extends AppCompatActivity {

    private static final int PICK_VID = 1;
    private ArrayList<Uri> arrVideoList = new ArrayList<Uri>();
    private List<Videos> arrUrl = new ArrayList<Videos>();
    private int uploads = 0;

    private Button btn_addVideo, btn_choose;
    private TextView tv_back, tv_summary;
    private RecyclerView rv_videos;
    private String projectIdFromIntent;
    private ProgressDialog progressDialog;

    private DatabaseReference videoDatabase;
    private StorageReference projectVideoStorage, listingVideoStorage;
    private AdapterVideoItem adapterVideoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_videos);

        projectIdFromIntent = getIntent().getStringExtra("Project ID");

        listingVideoStorage = FirebaseStorage.getInstance().getReference("Listings");
        projectVideoStorage = FirebaseStorage.getInstance().getReference("Projects").child(projectIdFromIntent);
        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");

        setRef();
        generateRecyclerLayout();
        clickListeners();
    }



    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_videos.this, edit_project_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                startActivity(intent);
            }
        });

        btn_addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( arrVideoList.size() <= 0 )
                {
                    Toast.makeText(add_videos.this, "Please choose a video", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog = new ProgressDialog(add_videos.this);
                    progressDialog.setTitle("Uploading video/s");
                    progressDialog.show();

                    uploadVideos();
                }

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), PICK_VID);
               // startActivityForResult(intent, PICK_VID);
            }
        });

        adapterVideoItem.setOnItemClickListener(new AdapterVideoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(add_videos.this, video_view_page.class);
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

        if (requestCode == PICK_VID) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentVideoSelect = 0;

                    while (CurrentVideoSelect < count) {
                        Uri videouri = data.getClipData().getItemAt(CurrentVideoSelect).getUri();
                        arrVideoList.add(videouri);
                        CurrentVideoSelect = CurrentVideoSelect + 1;
                    }

                    tv_summary.setVisibility(View.VISIBLE);
                    tv_summary.setText("You Have Selected "+ arrVideoList.size() +" Videos" );

                }
                else {
                    Uri uri = data.getData();
                    if (data.getData() != null) {

                        arrVideoList.add(uri);
                        tv_summary.setVisibility(View.VISIBLE);
                        tv_summary.setText("You Have Selected "+ arrVideoList.size() +" Video" );
                    }
                }

            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void uploadVideos() {
        for (uploads=0; uploads < arrVideoList.size(); uploads++)
        {
            Uri Video  = arrVideoList.get(uploads);

            long videoTime = System.currentTimeMillis();
            String fileType = getfiletype(Video);
            String videoName = videoTime + Video.getLastPathSegment().toString() + uploads + "." + fileType;

            StorageReference videoRef = projectVideoStorage.child(videoName);

            videoRef.putFile(arrVideoList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url, videoName);
                        }
                    });

                }
            });
        }
    }

    private void SendLink(String url, String videoName) {

        Videos videos = new Videos(projectIdFromIntent, url, videoName);

        videoDatabase.push().setValue(videos).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                arrVideoList.clear();
                progressDialog.dismiss();
            }
        });
    }

    private void generateRecyclerLayout() {
        rv_videos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(add_videos.this, 3, GridLayoutManager.VERTICAL, false);
        rv_videos.setLayoutManager(gridLayoutManager);

        adapterVideoItem = new AdapterVideoItem(arrUrl, add_videos.this);
        rv_videos.setAdapter(adapterVideoItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = videoDatabase
                .orderByChild("projID")
                .equalTo(projectIdFromIntent);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Videos videos = snapshot.getValue(Videos.class);
                arrUrl.add(videos);
                rv_videos.setAdapter(adapterVideoItem);
                adapterVideoItem.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Videos videos = snapshot.getValue(Videos.class);
                arrUrl.add(videos);
                rv_videos.setAdapter(adapterVideoItem);
                adapterVideoItem.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void setRef() {
        btn_addVideo = findViewById(R.id.btn_addVideo);
        btn_choose = findViewById(R.id.btn_choose);

        tv_back = findViewById(R.id.tv_back);
        tv_summary = findViewById(R.id.tv_summary);

        rv_videos = findViewById(R.id.rv_videos);
    }
}