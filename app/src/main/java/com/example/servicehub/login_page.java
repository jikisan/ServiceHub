package com.example.servicehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login_page extends AppCompatActivity {


    EditText et_username, et_password;
    CheckBox checkBox_rememberMe;
    TextView tv_forgotPassword, tv_signUp;
    Button btn_login, btn_guest;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);


        setRef();
        ClickListener();
    }

    private void ClickListener() {

        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = "Guest";

                Intent intentGuest = new Intent(login_page.this, homepage.class);
                intentGuest.putExtra("keyname", username);
                startActivity(intentGuest);

            }
        });

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignUp = new Intent(login_page.this, sign_up_page.class);
                startActivity(intentSignUp);

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                userDatabase = FirebaseDatabase.getInstance().getReference(Users.class.getSimpleName());
//
//
//                Toast.makeText(login_page.this, username, Toast.LENGTH_SHORT).show();
               // Toast.makeText(login_page.this, password, Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void setRef() {

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        checkBox_rememberMe = findViewById(R.id.cb_rememberMe);
        tv_forgotPassword = findViewById(R.id.tv_ForgotPassword);
        tv_signUp = findViewById(R.id.tv_signUp);
        btn_login = findViewById(R.id.btn_login);
        btn_guest = findViewById(R.id.btn_guest);


    }
}