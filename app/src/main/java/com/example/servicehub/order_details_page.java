package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class order_details_page extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn, iv_moreBtn,
             iv_orderPhoto, iv_custLocation, iv_messageCust, iv_proofOfPaymentPic ;
    private TextView tv_orderName, tv_customerName, tv_orderPrice, tv_orderQuantity, iv_deleteOrderBtn,
            tv_customerAddress, tv_custContactNum, tv_back, tv_totalAmount,
            tv_paymentMethod, tv_fundBalance, tv_btnText, tv_proofOfPaymentBanner;

    private String userID, imageUriText, orderIdFromIntent, custLatLng, custID,
            orderName, latString, longString, listingIdFromIntent, sellerID;
    private Double fundAmount, totalAmount;
    private CardView cv_finishOrderBtn;

    private FirebaseUser user;
    private DatabaseReference userDatabase;
    private DatabaseReference orderDatabase, walletDb;
    private StorageReference listingStorage;
    private StorageTask addTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        listingStorage = FirebaseStorage.getInstance().getReference("Listings");
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        walletDb = FirebaseDatabase.getInstance().getReference("Wallets");

        setRef();
        clickListeners();
        bottomNavTaskbar();
        generateDataValue();
       // generateProfile();

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
        iv_proofOfPaymentPic = findViewById(R.id.iv_proofOfPaymentPic);

        tv_orderName = findViewById(R.id.tv_orderName);
        tv_orderPrice = findViewById(R.id.tv_orderPrice);
        tv_orderQuantity = findViewById(R.id.tv_orderQuantity);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_customerAddress = findViewById(R.id.tv_customerAddress);
        tv_custContactNum = findViewById(R.id.tv_custContactNum);
        tv_totalAmount = findViewById(R.id.tv_totalAmount);
        tv_back = findViewById(R.id.tv_back);
        tv_paymentMethod = findViewById(R.id.tv_paymentMethod);
        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        tv_btnText = findViewById(R.id.tv_btnText);
        tv_proofOfPaymentBanner = findViewById(R.id.tv_proofOfPaymentBanner);

        cv_finishOrderBtn = findViewById(R.id.cv_finishOrderBtn);
    }

    private void clickListeners() {

        iv_messageCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sender = userID;
                String receiver = custID;
                String chatName = orderName;

                String chatUid = sender + "_" + receiver + "_" + listingIdFromIntent;

                Intent intent = new Intent(order_details_page.this, chat_activity_order.class);
                intent.putExtra("listing id", listingIdFromIntent);
                intent.putExtra("tech id", custID);
                intent.putExtra("sender id", userID);
                intent.putExtra("chat id", chatUid);
                startActivity(intent);

            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(order_details_page.this, seller_dashboard.class);
                startActivity(intent);
            }
        });

        iv_deleteOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOrder();
            }
        });

        iv_custLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProject = new Intent(order_details_page.this, view_in_map.class);
                intentProject.putExtra("Category", "orders");
                intentProject.putExtra("latString", latString);
                intentProject.putExtra("longString", longString);
                startActivity(intentProject);
            }
        });

        cv_finishOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(hasImage(iv_proofOfPaymentPic)){
                    Toast.makeText(order_details_page.this, "Proof of payment is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new SweetAlertDialog(order_details_page.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Finish order?")
                            .setCancelText("Back")
                            .setConfirmButton("FINISH ORDER", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    if(totalAmount < fundAmount)
                                    {
                                        Intent intent = new Intent(order_details_page.this, rating_and_review_client.class);
                                        intent.putExtra("category", "order");
                                        intent.putExtra("booking id", orderIdFromIntent);
                                        intent.putExtra("client id", custID);
                                        intent.putExtra("tech id", sellerID);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        new SweetAlertDialog(order_details_page.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Failed!.")
                                                .setContentText("Insufficient funds.")
                                                .setCancelText("Back")
                                                .setConfirmButton("Add Funds", new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                        Intent intent = new Intent(order_details_page.this, seller_dashboard.class);
                                                        startActivity(intent);

                                                    }
                                                })
                                                .show();
                                    }


                                }
                            })
                            .setContentText("Are you sure you want to \nfinish this order?")
                            .show();
                }




            }
        });
    }

    private void deleteOrder() {

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(order_details_page.this)
                .setTitle("Cancel Order?")
                .setMessage("Are you sure you want to cancel this order?")
                .setCancelable(true)
                .setPositiveButton("Cancel Order", R.drawable.delete_btn, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        progressDialog = new ProgressDialog(order_details_page.this);
                        progressDialog.setTitle("Cancelling order");
                        progressDialog.show();

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
                .setNegativeButton("Back", R.drawable.back, new MaterialDialog.OnClickListener() {
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
        String sp_notifMessage = "Order " + sp_orderName + " has been cancelled by the seller.";

        Date currentTime = Calendar.getInstance().getTime();
        String cartCreated = currentTime.toString();

        Notification notification = new Notification(imageUriText, sp_notifTitle, sp_notifMessage, cartCreated, custID);

        notificationDB.push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(order_details_page.this, seller_dashboard.class);
                    intent.putExtra("currentTab", 1);
                    startActivity(intent);

                    Toast.makeText(order_details_page.this, "Order Cancelled", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void generateDataValue() {

        orderIdFromIntent = getIntent().getStringExtra("Order ID");

        orderDatabase.child(orderIdFromIntent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Orders orders = snapshot.getValue(Orders.class);

                if (orders != null){
                    try {

                        sellerID = orders.sellerID;
                        listingIdFromIntent = orders.listingID;
                        custID = orders.getCustID();
                        latString = orders.getLatitude();
                        longString = orders.getLongitude();
                        imageUriText = orders.getImageUrl();
                        orderName = orders.getItemName();
                        String sp_ordersPrice = orders.getProdSubTotal();
                        String sp_orderQuantity = orders.getItemQuantity();
                        String sp_paymentMethod = orders.getPaymentMethod();
                        String sp_popUrl = orders.getProofOfPaymentUrl();

                        String sp_custName = orders.getCustName();
                        String sp_custContactNum = orders.getCustContactNum();
                        String sp_custAddress = orders.getCustDeliveryAddress();


                        //Order details
                        Picasso.get().load(imageUriText).into(iv_orderPhoto);
                        tv_orderName.setText(orderName);


                        //customer details
                        tv_customerName.setText(sp_custName);
                        tv_custContactNum.setText(sp_custContactNum);
                        tv_customerAddress.setText(sp_custAddress);
                        tv_orderQuantity.setText(sp_orderQuantity);
                        tv_orderPrice.setText("₱ " + sp_ordersPrice);
                        tv_paymentMethod.setText( sp_paymentMethod);

                        totalAmount = Double.parseDouble(sp_ordersPrice) * Double.parseDouble(sp_orderQuantity);
                        tv_totalAmount.setText("₱ " + totalAmount);

                        if (!sp_popUrl.isEmpty())
                        {
                            Picasso.get().load(sp_popUrl).into(iv_proofOfPaymentPic);
                            tv_proofOfPaymentBanner.setVisibility(View.GONE);
                        }

                        Double percentageFee = totalAmount * .05;

                        tv_btnText.setText("5% fee: ₱ " + percentageFee + " · " + "Complete Booking");

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                else
                {
                    Toast.makeText(order_details_page.this, "Empty", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(order_details_page.this, "Empty", Toast.LENGTH_SHORT).show();

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

    private void generateProfile() {
        userDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);

                if(userProfile != null){
                    String sp_fName = userProfile.firstName;
                    String sp_lName = userProfile.lastName;
                    String sp_num = userProfile.contactNum;

                    tv_customerName.setText(sp_fName + " " + sp_lName);
                    tv_custContactNum.setText(sp_num);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(order_details_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(order_details_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(order_details_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(order_details_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(order_details_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(order_details_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private boolean hasImage(ImageView iv){

        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }

}