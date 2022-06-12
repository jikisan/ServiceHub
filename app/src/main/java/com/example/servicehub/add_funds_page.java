package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class add_funds_page extends AppCompatActivity {

    private TextView tv_back, tv_bannerName, tv_fundBalance;
    private ImageView iv_userPic;
    private EditText et_inputFund;
    private Button btn_addFund;
    private ProgressBar progressBar;

    private DatabaseReference walletDb, userDatabase;
    private FirebaseUser user;
    private String userID, category;
    private Double fundAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_funds_page);

        walletDb = FirebaseDatabase.getInstance().getReference("Wallets");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        category = getIntent().getStringExtra("category");

        setRef();
        clickListeners();
        generateData();
    }

    private void generateData() {

        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_imageUrl = userProfile.imageUrl;

                    String firstName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase();
                    String lastName = sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    tv_bannerName.setText(firstName + " " + lastName);
                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_userPic);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(add_funds_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        walletDb.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallets wallets = snapshot.getValue(Wallets.class);

                if(wallets != null)
                {
                    fundAmount = wallets.fundAmount;
                    tv_fundBalance.setText(fundAmount + "");

                }
                else

                    fundAmount = 0.00;
                    tv_fundBalance.setText(fundAmount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateCategory();

            }
        });

        btn_addFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                double enteredFundAmount = Double.parseDouble(et_inputFund.getText().toString());
                double totalFundAmount = fundAmount + enteredFundAmount;

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(add_funds_page.this)
                        .setTitle("ADDING FUNDS")
                        .setMessage("Add " + enteredFundAmount + " to your wallet?")
                        .setCancelable(true)
                        .setPositiveButton("Add Funds", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                final ProgressDialog progressDialog = new ProgressDialog(add_funds_page.this);
                                progressDialog.setTitle("Processing...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                addFund(totalFundAmount);
                            }
                        })
                        .setNegativeButton("Back", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .build();

                mBottomSheetDialog.show();
            }
        });


    }

    private void addFund(double totalFundAmount) {

        Wallets wallets = new Wallets(userID, totalFundAmount);

        walletDb.child(userID).setValue(wallets).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {


                    new SweetAlertDialog(add_funds_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("SUCCESS!.")
                            .setContentText("Funds successfully added \n to your wallet")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    validateCategory();
                                }
                            })
                            .show();
                }
                else
                {
                    new SweetAlertDialog(add_funds_page.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("FAILED!.")
                            .setContentText("Transaction is failed. \n Please try again.")
                            .setConfirmButton("Proceed", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    Intent intent = new Intent(add_funds_page.this, add_funds_page.class);
                                    startActivity(intent);

                                }
                            })
                            .show();
                }
            }

        });

    }

    private void validateCategory() {
        if(category.equals("tech"))
        {
            Intent intent = new Intent(add_funds_page.this, tech_dashboard.class);
            startActivity(intent);
        }
        else if(category.equals("seller"))
        {
            Intent intent = new Intent(add_funds_page.this, seller_dashboard.class);
            startActivity(intent);
        }

    }

    private void setRef() {
        tv_back = findViewById(R.id.tv_back);
        iv_userPic = findViewById(R.id.iv_userPic);
        tv_bannerName = findViewById(R.id.tv_bannerName);
        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        et_inputFund = findViewById(R.id.et_inputFund);
        btn_addFund = findViewById(R.id.btn_addFund);
        progressBar = findViewById(R.id.progressBar);
    }
}