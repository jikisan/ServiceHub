package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up_page extends AppCompatActivity {

    EditText et_firstName, et_lastName, et_contactNumber, et_username, et_password_signup,
            et_confirmPassword;
    TextView tv_signIn;
    Button btn_signUp;
    FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
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
        String firstName = et_firstName.getText().toString();
        String lastName = et_lastName.getText().toString();
        String username = et_username.getText().toString();
        String password = et_password_signup.getText().toString();
        String confirmPass = et_confirmPassword.getText().toString();
        String contactNum = et_contactNumber.getText().toString();


        if (TextUtils.isEmpty(firstName)) {
            et_firstName.setError("This field is required");

        } else if (TextUtils.isEmpty(lastName)) {
            et_lastName.setError("This field is required");
        } else if (TextUtils.isEmpty(contactNum)) {
            et_contactNumber.setError("This field is required");
        } else if (contactNum.length() != 11) {
            et_contactNumber.setError("Contact number must be 11 digit");
        } else if (TextUtils.isEmpty(username)) {
            et_username.setError("This field is required");
        } else if (TextUtils.isEmpty(password)) {
            et_password_signup.setError("This field is required");
        } else if (TextUtils.isEmpty(confirmPass)) {
            et_confirmPassword.setError("This field is required");
        } else if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
        } else {

            fAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    fAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = user.getUid();
                                Users users = new Users(id, firstName, lastName, contactNum, username, password);

                                userDatabase.child(user.getUid())
                                        .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(sign_up_page.this, "User Created", Toast.LENGTH_LONG).show();
                                            fAuth.signOut();
                                            startActivity(new Intent(getApplicationContext(), login_page.class));
                                        } else {
                                            Toast.makeText(sign_up_page.this, "Creation Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(sign_up_page.this, "Creation Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            });
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
        fAuth = FirebaseAuth.getInstance();

    }
}