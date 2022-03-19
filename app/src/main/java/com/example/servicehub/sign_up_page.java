package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        ClickListener();




    }

    private void ClickListener() {
        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( sign_up_page.this, login_page.class);
                startActivity(intent);
            }
        });

        DAOusers dao = new DAOusers();

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Users users = new Users(et_firstName.getText().toString(),
                                        et_lastName.getText().toString(),
                                        Integer.parseInt(et_contactNumber.getText().toString()),
                                        et_email.getText().toString(),
                                        et_password_signup.getText().toString());

                dao.add(users).addOnSuccessListener(suc->{
                    Toast.makeText(sign_up_page.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(er->{
                    Toast.makeText(sign_up_page.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

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