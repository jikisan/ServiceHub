package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter_and_fragments.AdapterChatUserListItem;

public class message_page extends AppCompatActivity {

    private List<Chat> arr;
    private AdapterChatUserListItem adapterChatUserListItem;
    private FirebaseUser user;
    private DatabaseReference messageDatabase, chatDatabase, projDatabase, listDatabase;

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private String userID, messageID, chatUid, senderUid, receiverUid, projectID,
            listingID, techAsReceiver, techAsSender;
    private RecyclerView recyclerView_chatProfiles;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        senderUid = userID;
        listDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        setRef();
        generateRecyclerLayout();
        bottomNavTaskbar();
        clickListeners();

    }

    private void clickListeners() {
        adapterChatUserListItem.setOnItemClickListener(new AdapterChatUserListItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);
                String receiverName = arr.get(position).receiverName;

                generateChatId(position, receiverName);

            }
        });
    }

    private void generateChatId(int position, String receiverName) {

        chatDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    techAsSender = chat.senderUid;
                    techAsReceiver = chat.receiverUid;

                    if(techAsSender.equals(userID) && chat.getReceiverName().equals(receiverName))
                    {
                        chatUid = dataSnapshot.getKey().toString();
                        senderUid = techAsSender;
                        receiverUid = techAsReceiver;

                        if(chat.chatType.equals("listing"))
                        {
                            generatListId(position, receiverName);
                        }
                        else if(chat.chatType.equals("project"))
                        {
                            generateProjId(position, receiverName);
                        }


                    }
                    else if(techAsReceiver.equals(userID) && chat.getReceiverName().equals(receiverName))
                    {
                        chatUid = dataSnapshot.getKey().toString();
                        senderUid = techAsSender;
                        receiverUid = techAsReceiver;

                        if(chat.chatType.equals("listing"))
                        {
                            generatListId(position, receiverName);
                        }
                        else if(chat.chatType.equals("project"))
                        {
                            generateProjId(position, receiverName);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generatListId(int position, String receiverName) {


        Query query = listDatabase
                .orderByChild("listName")
                .equalTo(receiverName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    listingID = dataSnapshot.getKey().toString();
                    Intent intent = new Intent(message_page.this, chat_activity_order.class);
                    intent.putExtra("listing id", listingID);
                    intent.putExtra("tech id", receiverUid);
                    intent.putExtra("sender id", userID);
                    intent.putExtra("chat id", chatUid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateProjId(int position, String receiverName) {

        Query query = projDatabase
                .orderByChild("projName")
                .equalTo(receiverName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projects = snapshot.getValue(Projects.class);

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    projectID = dataSnapshot.getKey().toString();
                    Intent intent = new Intent(message_page.this, chat_activity.class);
                    intent.putExtra("project id", projectID);
                    intent.putExtra("tech id", receiverUid);
                    intent.putExtra("sender id", userID);
                    intent.putExtra("chat id", chatUid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecyclerLayout() {

        recyclerView_chatProfiles.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_chatProfiles.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterChatUserListItem = new AdapterChatUserListItem(arr);
        recyclerView_chatProfiles.setAdapter(adapterChatUserListItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                        arr.add(chat);
                }

                progressBar.setVisibility(View.GONE);
                adapterChatUserListItem.notifyDataSetChanged();
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
                Intent intentMessageBtn = new Intent(message_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(message_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(message_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(message_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(message_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        recyclerView_chatProfiles = findViewById(R.id.recyclerView_chatProfiles);
        progressBar = findViewById(R.id.progressBar);
    }
}