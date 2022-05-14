package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class intro_logo extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_logo);


        user = FirebaseAuth.getInstance().getCurrentUser();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(user == null)
                {
                    Intent intent = new Intent(intro_logo.this, login_page.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(intro_logo.this, homepage.class);
                    startActivity(intent);
                }


            }
        }, 2000);

    }
}