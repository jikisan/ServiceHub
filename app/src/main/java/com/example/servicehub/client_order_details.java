package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class client_order_details extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn, iv_moreBtn,
            iv_orderPhoto, iv_custLocation, iv_messageCust,iv_custPhoto ;
    private TextView tv_orderName, tv_customerName, tv_orderPrice, tv_orderQuantity, iv_deleteOrderBtn,
            tv_customerAddress, tv_custContactNum, tv_back, tv_totalAmount, tv_paymentMethod;

    private String userID, imageUriText, orderIdFromIntent, custLatLng, custID,
            orderName, latString, longString, sellerID;
    private CardView cv_finishOrderBtn;

    private FirebaseUser user;
    private DatabaseReference orderDatabase, listDatabase, userDatabase;
    private StorageReference listingStorage;
    private StorageTask addTask;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_order_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingStorage = FirebaseStorage.getInstance().getReference("Listings");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        listDatabase = FirebaseDatabase.getInstance().getReference("Listings");

        setRef();
        bottomNavTaskbar();
        generateDataValue();
        clickListeners();
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(client_order_details.this, my_orders_page.class);
                startActivity(intent);
            }
        });

        iv_deleteOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelOrder();
            }
        });

        iv_custLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String markerTitle = "Seller Location";
                Intent intentProject = new Intent(client_order_details.this, view_in_map.class);
                intentProject.putExtra("Category", "orders");
                intentProject.putExtra("latString", latString);
                intentProject.putExtra("longString", longString);
                intentProject.putExtra("Marker Title", markerTitle);
                startActivity(intentProject);
            }
        });

    }

    private void cancelOrder() {
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(client_order_details.this)
                .setTitle("Cancel Order for" + orderName)
                .setMessage("Are you sure you want to cancel this order?")
                .setCancelable(true)
                .setPositiveButton("Cancel", R.drawable.delete_btn, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        progressDialog = new ProgressDialog(client_order_details.this);
                        progressDialog.setTitle("Cancelling order...");
                        progressDialog.show();

                        orderIdFromIntent = getIntent().getStringExtra("Order ID");
                        orderDatabase.child(orderIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue();

                                }
                                generateNotification();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("Back", R.drawable.back_arrow, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }


                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();


    }

    private void generateNotification() {

        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference("Notifications");

        String sp_orderName = tv_orderName.getText().toString();
        String sp_notifTitle = "Order cancelled";
        String sp_notifMessage = "Order " + sp_orderName + " has been cancelled by the customer.";

        Date currentTime = Calendar.getInstance().getTime();
        String notifCreated = currentTime.toString();

        Notification notification = new Notification(imageUriText, sp_notifTitle, sp_notifMessage, notifCreated, custID);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(client_order_details.this, seller_dashboard.class);
                    intent.putExtra("currentTab", 1);
                    startActivity(intent);

                    Toast.makeText(client_order_details.this, "Order Cancelled", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void setRef() {
        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);
        iv_deleteOrderBtn = findViewById(R.id.iv_deleteOrderBtn);
        iv_orderPhoto = findViewById(R.id.iv_orderPhoto);
        iv_custLocation = findViewById(R.id.iv_custLocation);
        iv_messageCust = findViewById(R.id.iv_messageCust);
        iv_custPhoto = findViewById(R.id.iv_custPhoto);

        tv_orderName = findViewById(R.id.tv_orderName);
        tv_orderPrice = findViewById(R.id.tv_orderPrice);
        tv_orderQuantity = findViewById(R.id.tv_orderQuantity);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_customerAddress = findViewById(R.id.tv_customerAddress);
        tv_custContactNum = findViewById(R.id.tv_custContactNum);
        tv_totalAmount = findViewById(R.id.tv_totalAmount);
        tv_back = findViewById(R.id.tv_back);
        tv_paymentMethod = findViewById(R.id.tv_paymentMethod);

        cv_finishOrderBtn = findViewById(R.id.cv_finishOrderBtn);

        progressBar = findViewById(R.id.progressBar);
    }

    private void generateDataValue() {

        orderIdFromIntent = getIntent().getStringExtra("Order ID");

        orderDatabase.child(orderIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Orders orders = snapshot.getValue(Orders.class);

                if (orders != null){
                    try {
                        custID = orders.getCustID();
                        sellerID = orders.getSellerID();
                        imageUriText = orders.getImageUrl();
                        orderName = orders.getItemName();
                        String sp_ordersPrice = orders.getTotalPayment();
                        String sp_orderQuantity = orders.getItemQuantity();
                        String sp_paymentMethod = orders.getPaymentMethod();

                        //Order details
                        Picasso.get().load(imageUriText).into(iv_orderPhoto);
                        tv_orderName.setText(orderName);

                        //customer details
                        tv_orderQuantity.setText(sp_orderQuantity);
                        tv_orderPrice.setText("₱ " + sp_ordersPrice);
                        tv_paymentMethod.setText( sp_paymentMethod);

                        double totalAmount = Double.parseDouble(sp_ordersPrice)
                                * Double.parseDouble(sp_orderQuantity);
                        tv_totalAmount.setText("₱ " + totalAmount);


                        generateProfile();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(client_order_details.this, "Empty", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(client_order_details.this, "Empty", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void generateProfile() {

        Query query = listDatabase
                .orderByChild("listName")
                .startAt(orderName).endAt(orderName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Listings listings = dataSnapshot.getValue(Listings.class);

                    if(listings.getUserID().equals(sellerID))
                    {
                        tv_customerAddress.setText(listings.getListAddress());
                        latString = listings.getLatitude();
                        longString = listings.getLongitude();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userDatabase.child(sellerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (snapshot.exists())
                {

                    String sp_fName = users.firstName;
                    String sp_lName = users.lastName;
                    String sp_imageUrl = users.imageUrl;
                    String sp_fullName = sp_fName.substring(0, 1).toUpperCase()+ sp_fName.substring(1).toLowerCase()
                            + " " + sp_lName.substring(0, 1).toUpperCase()+ sp_lName.substring(1).toLowerCase();

                    if (!sp_imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(sp_imageUrl)
                                .into(iv_custPhoto);

                    tv_customerName.setText(sp_fullName);
                    tv_custContactNum.setText(users.contactNum);


                    }

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(client_order_details.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(client_order_details.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(client_order_details.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(client_order_details.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(client_order_details.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }
}