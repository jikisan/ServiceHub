package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class place_order_page extends AppCompatActivity {

    private ImageView iv_projectImage,  iv_message, iv_cart;
    private TextView tv_projName, tv_projRating, tv_projPrice, tv_back, tv_projDesc, tv_quantity;
    private Button btn_orderNow;
    private boolean isExist = false;

    private FirebaseUser user;
    private DatabaseReference listingDatabase, cartDatabase;
    private String userID, sellerID, listingIdFromIntent, imageUrlText, tempListName, listPrice, listRatings;
    private Uri tempUri;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_order_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");
        listingIdFromIntent = getIntent().getStringExtra("Listing ID");

        setRef();
        generateListDataValue();
        clickListeners();
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sender = userID;
                String receiver = sellerID;
                String chatName = tempListName;

                String chatUid = sender + "_" + receiver + "_" + listingIdFromIntent;

                Intent intent = new Intent(place_order_page.this, chat_activity_order.class);
                intent.putExtra("listing id", listingIdFromIntent);
                intent.putExtra("tech id", sellerID);
                intent.putExtra("sender id", userID);
                intent.putExtra("chat id", chatUid);
                startActivity(intent);

            }
        });

        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addToCart();

            }
        });

        btn_orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(place_order_page.this, checkout_page.class);
                intent.putExtra("Listing ID", listingIdFromIntent);
                startActivity(intent);
            }
        });

    }

    private void addToCart() {

        Query query = cartDatabase
                .orderByChild("custID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Cart c = dataSnapshot.getValue(Cart.class);

                        if( c.getListName().equals(tempListName) )
                        {
                            new SweetAlertDialog(place_order_page.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Item is already in Cart")
                                    .setCancelText("Back")
                                    .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent intent = new Intent(place_order_page.this, cart_page.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setContentText("Go to cart?")
                                    .show();

                            isExist = true;
                            break;
                        }
                    }

                    if(!isExist)
                    {
                        addListToCart();
                    }

                }
                else
                {

                   addListToCart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addListToCart() {
        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();
        Cart cart = new Cart(userID, listingIdFromIntent, cartCreated, imageUrlText, tempListName, listPrice, listRatings);

        cartDatabase.push().setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    new SweetAlertDialog(place_order_page.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Item is added to Cart!")
                            .setCancelText("Back")
                            .setContentText("Go to Cart?")
                            .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(place_order_page.this, cart_page.class);
                                    startActivity(intent);
                                }
                            })
                            .show();


                } else {
                    Toast.makeText(place_order_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setRef() {

        iv_projectImage = findViewById(R.id.iv_projPhotoSummary);
        iv_message = findViewById(R.id.iv_message);
        iv_cart = findViewById(R.id.iv_cart);

        tv_projName = findViewById(R.id.tv_projName);
        tv_projPrice = findViewById(R.id.tv_projPrice);
        tv_projDesc = findViewById(R.id.tv_projDesc);
        tv_back = findViewById(R.id.tv_back);
        tv_quantity = findViewById(R.id.tv_quantity);

        btn_orderNow = findViewById(R.id.btn_orderNow);

        progressBar = findViewById(R.id.progressBar);

    }

    private void generateListDataValue() {

        listingDatabase.child(listingIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listingsData = snapshot.getValue(Listings.class);

                if(listingsData != null){
                    try{
                        sellerID = listingsData.getUserID();
                        imageUrlText = listingsData.getImageUrl();
                        listRatings= listingsData.getRatings();
                        tempListName = listingsData.getListName();
                        String sp_projPrice = listingsData.getListPrice();
                        String sp_quantity = listingsData.getListQuantity();
                        String sp_projSpecialInstruction = listingsData.getListDesc();

                        tempUri = Uri.parse(imageUrlText);
                        double price = Double.parseDouble(sp_projPrice);

                        Picasso.get()
                                .load(tempUri)
                                .resize(800, 600)
                                .into(iv_projectImage);
                        DecimalFormat twoPlaces = new DecimalFormat("0.00");
                        listPrice = twoPlaces.format(price);

                        tv_back.setText("Marketplace");
                        tv_projName.setText(tempListName);
                        tv_projPrice.setText("₱ " + listPrice + " /Job");
                        tv_quantity.setText(sp_quantity + " Pieces Available");
                        tv_projDesc.setText(sp_projSpecialInstruction);


                        progressBar.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(place_order_page.this, "Empty", Toast.LENGTH_SHORT).show();
                    System.out.println("Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(place_order_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}