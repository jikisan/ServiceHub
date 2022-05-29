package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class video_view_page extends AppCompatActivity {

    private List<Videos> arrUrl = new ArrayList<Videos>();
    private String projectIdFromIntent, category, videoName;
    private int currentPosition;

    private ImageView iv_deletPhoto;
    private TextView tv_back;
    private VideoView videoView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_page);

        currentPosition = getIntent().getIntExtra("current position", 0);
        projectIdFromIntent = getIntent().getStringExtra("Project ID");
       // videoName = getIntent().getStringExtra("video name");
        category = getIntent().getStringExtra("category");


        setRef();

        if(category.equals("viewer"))
        {
            iv_deletPhoto.setVisibility(View.GONE);
        }

        generateVideo();
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
                DatabaseReference videoDB = FirebaseDatabase.getInstance().getReference("Videos");
                StorageReference photoStorage = FirebaseStorage.getInstance().getReference("Projects").child(projectIdFromIntent);

                new SweetAlertDialog(video_view_page.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete")
                        .setContentText(videoName + "?")
                        .setCancelText("Cancel")
                        .setConfirmButton("Delete", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                deleteVideoInDb(videoDB, photoStorage, videoName, projectIdFromIntent);

                            }
                        })
                        .show();
            }
        });
    }

    private void deleteVideoInDb(DatabaseReference videoDB, StorageReference photoStorage, String videoName, String projID) {
        progressDialog = new ProgressDialog(video_view_page.this);
        progressDialog.setTitle("Deleting video: " + videoName );
        progressDialog.show();

        Query query = videoDB
                .orderByChild("videoName")
                .equalTo(videoName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StorageReference imageRef = photoStorage.child(videoName);

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
                            Toast.makeText(video_view_page.this, "Video: " + videoName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(video_view_page.this, add_videos.class);
                            intent.putExtra("Project ID", projID);
                            video_view_page.this.startActivity(intent);
                        }
                        else if(category.equals("viewer"))
                        {
                            Toast.makeText(video_view_page.this, "Video: " + videoName + " deleted successfully! ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(video_view_page.this, photo_viewer.class);
                            intent.putExtra("Project ID", projID);
                            video_view_page.this.startActivity(intent);
                        }

                    }
                }, 4000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateVideo() {

        DatabaseReference vidDB = FirebaseDatabase.getInstance().getReference("Videos");

        Query query = vidDB
                .orderByChild("projID")
                .equalTo(projectIdFromIntent);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Videos videos = dataSnapshot.getValue(Videos.class);
                    arrUrl.add(videos);

                }

                String vidUri = arrUrl.get(currentPosition).getLink();
                videoName = arrUrl.get(currentPosition).getVideoName();
                tv_back.setText(videoName);
                loadVid(vidUri);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadVid(String vidUri) {

        Uri uri = Uri.parse(vidUri);

        // sets the resource from the
        // videoUrl to the videoView
        videoView.setVideoURI(uri);

        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(this);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView);

        // sets the media controller to the videoView
        videoView.setMediaController(mediaController);

        // starts the video
        // videoView.start();
    }

    private void setRef() {

        videoView = findViewById(R.id.videoView);
        tv_back = findViewById(R.id.tv_back);
        iv_deletPhoto = findViewById(R.id.iv_deletPhoto);

    }
}