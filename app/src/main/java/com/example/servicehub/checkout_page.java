package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class checkout_page extends AppCompatActivity {

    private String listingIdFromIntent, userID, sellerID, latLng, imageUrl,
                prodSubTotal, shipFee, totalPayment;
    private TextView tv_custName, tv_productSub, tv_shippinSub, tv_listName,
            tv_totalPriceSub, tv_totalPrice, tv_placeOrderBtn, tv_back;
    private EditText et_contactNum, et_address, et_message;
    private ImageView iv_listPhoto;
    private ProgressBar progressBar;

    private FirebaseUser user;
    private DatabaseReference listingDatabase,  userDatabase, ordersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        listingIdFromIntent = getIntent().getStringExtra("Listing ID");

        setRef();
        initPlaces();
        generateData();
        clickListeners();

        System.out.println("Listing:" + listingIdFromIntent);
        System.out.println("Seller" + sellerID);
    }

    private void setRef() {

        tv_custName = findViewById(R.id.tv_custName);
        tv_productSub = findViewById(R.id.tv_productSub);
        tv_shippinSub = findViewById(R.id.tv_shippinSub);
        tv_totalPriceSub = findViewById(R.id.tv_totalPriceSub);
        tv_totalPrice = findViewById(R.id.tv_totalPrice);
        tv_placeOrderBtn = findViewById(R.id.tv_placeOrderBtn);
        tv_listName = findViewById(R.id.tv_listName);
        tv_back = findViewById(R.id.tv_back);

        et_contactNum = findViewById(R.id.et_contactNum);
        et_address = findViewById(R.id.et_address);
        et_message = findViewById(R.id.et_message);

        iv_listPhoto = findViewById(R.id.iv_listPhoto);

        progressBar = findViewById(R.id.progressBar);
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        //Set edittext no focusable
        et_address.setFocusable(false);
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        et_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(checkout_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);

            }
        });

        tv_placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(et_contactNum.getText())){
                    et_contactNum.setError("Contact number is required");
                }
                else if(et_contactNum.length() < 11){
                    Toast.makeText(checkout_page.this, "Mobile number should be 11 digits", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(et_address.getText())){
                    et_address.setError("Delivery address is requires");
                }
                else
                {
                    new SweetAlertDialog(checkout_page.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning!.")
                            .setCancelText("Cancel")
                            .setConfirmButton("Submit", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    placeOrder();
                                }
                            })
                            .setContentText("Please make sure\n all information are correct.\n")
                            .show();

                }
            }
        });
    }

    private void generateData() {
        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if(snapshot.exists()){
                    String sp_fname = users.firstName;
                    String sp_lname = users.lastName;
                    String sp_contactNum = users.contactNum;

                    tv_custName.setText(sp_fname + " " + sp_lname);
                    et_contactNum.setText(sp_contactNum);

                    generateListingData();

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateListingData() {

        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listings = snapshot.getValue(Listings.class);

                sellerID = listings.userID;
                imageUrl = listings.imageUrl;
                String sp_listName = listings.listName;
                String sp_listPrice = listings.listPrice;

                double price = Double.parseDouble(sp_listPrice);
                DecimalFormat twoPlaces = new DecimalFormat("0.00");
                double listShipFee = 55.50;

                prodSubTotal = twoPlaces.format(price);
                shipFee = String.valueOf(listShipFee);
                totalPayment = String.valueOf(twoPlaces.format(price + listShipFee));

                Picasso.get().load(imageUrl).into(iv_listPhoto);
                tv_listName.setText(sp_listName);
                tv_productSub.setText("₱ " + String.valueOf(price));
                tv_totalPriceSub.setText("₱ " + prodSubTotal);
                tv_shippinSub.setText("₱ " + shipFee);
                tv_totalPrice.setText("₱ " + totalPayment);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void placeOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(checkout_page.this);
        progressDialog.setTitle("Processing your booking...");
        progressDialog.show();

//        String sp_custID = userID;
//        String sp_listingID = listingIdFromIntent;
//        String sp_sellerID = sellerID;
        String sp_custName = tv_custName.getText().toString();
        String sp_custContactNum = et_contactNum.getText().toString();
        String sp_custDeliveryAddress = et_address.getText().toString();
//        String sp_latlng = latLng;
//        String sp_imageUrl = imageUrl;
        String sp_itemName = tv_listName.getText().toString();
        String sp_message = et_message.getText().toString();
//        String sp_prodSubTotal = tv_productSub.getText().toString();
//        String sp_shipFee = tv_shippinSub.getText().toString();
//        String sp_totalPayment = tv_totalPrice.getText().toString();

        Orders orders = new Orders(userID, listingIdFromIntent, sellerID, sp_custName, sp_custContactNum, sp_custDeliveryAddress,
                latLng, imageUrl, sp_itemName, sp_message, prodSubTotal, shipFee, totalPayment);

        ordersDatabase.push().setValue(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    new SweetAlertDialog(
                            checkout_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Your order has been placed!")
                            .setContentText("Congratulations! Your order is completed " +
                                    "\nand have been placed successfully")
                            .show();


                    Intent intent = new Intent(checkout_page.this, homepage.class);
                    startActivity(intent);



                } else {
                    Toast.makeText(checkout_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
            et_address.setText(place.getAddress());
            latLng = place.getLatLng().toString();

        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
        }

    }
}