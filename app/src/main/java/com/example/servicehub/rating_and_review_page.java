package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class rating_and_review_page extends AppCompatActivity {

    private ImageView iv_ratingPhoto;
    private TextView tv_back, tv_ratingName;
    private RatingBar ratingBar;
    private EditText et_ratingMessage;
    private Button btn_ratingSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_and_review_page);

        setRef();
    }

    private void setRef() {

        iv_ratingPhoto = findViewById(R.id.iv_ratingPhoto);
        tv_back = findViewById(R.id.tv_back);
        tv_ratingName = findViewById(R.id.tv_ratingName);
        ratingBar = findViewById(R.id.ratingBar);
        et_ratingMessage = findViewById(R.id.et_ratingMessage);
        btn_ratingSubmit = findViewById(R.id.btn_ratingSubmit);
    }
}