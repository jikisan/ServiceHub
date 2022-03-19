package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class sign_up_page extends AppCompatActivity {

    EditText et_firstName, et_lastName, et_contactNumber, et_email, et_password_signup,
            et_confirmPassword;
    TextView tv_signIn;
    Button btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        setRef();
        buttonClickListener();


    }

    private void buttonClickListener() {
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
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);
        et_email = findViewById(R.id.et_email);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        btn_signUp = findViewById(R.id.btn_signUp);

    }
}