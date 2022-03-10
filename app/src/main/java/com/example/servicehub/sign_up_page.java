package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class sign_up_page extends AppCompatActivity {

    TextView tv_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        setRef();

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( sign_up_page.this, login_page.class);
                startActivity(intent);
            }
        });


    }

    private void setRef() {
        tv_signIn = findViewById(R.id.tv_signIn);
    }
}