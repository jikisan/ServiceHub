package com.example.servicehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up_page extends AppCompatActivity {

    EditText et_firstName, et_lastName, et_contactNumber, et_username, et_password_signup,
            et_confirmPassword;
    TextView tv_signIn;
    Button btn_signUp;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        userDatabase = FirebaseDatabase.getInstance().getReference(Users.class.getSimpleName());
        setRef();
        ClickListener();

    }

    private void ClickListener() {
        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_up_page.this, login_page.class);
                startActivity(intent);
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signUpUser();

            }
        });
    }

    private void signUpUser() {
        String id = userDatabase.push().getKey();
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String username = et_username.getText().toString();
        String password = et_password_signup.getText().toString();
        String confirmPass = et_confirmPassword.getText().toString();
        String contactNum = et_contactNumber.getText().toString();


        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(contactNum)) {
            Toast.makeText(this, "Contact number is required", Toast.LENGTH_SHORT).show();

        } else if (contactNum.length() != 11) {
            Toast.makeText(this, "Contact number must be 11 digit", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();

        } else if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        } else {
            Users users = new Users(id, firstName, lastName, contactNum, username, password);
            userDatabase.child(id).setValue(users);

            Intent intent = new Intent(sign_up_page.this, login_page.class);
            startActivity(intent);
            Toast.makeText(this, "Account Created", Toast.LENGTH_LONG).show();
        }
    }

    private void setRef() {

        tv_signIn = findViewById(R.id.tv_signIn);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        et_contactNumber = findViewById(R.id.et_contactNumber);
        et_username = findViewById(R.id.et_username);
        et_password_signup = findViewById(R.id.et_password_signup);
        et_confirmPassword = findViewById(R.id.et_confirmPassword);
        btn_signUp = findViewById(R.id.btn_signUp);

    }
}