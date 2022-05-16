package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private DatabaseReference messageDatabase, chatDatabase, projDatabase;
    private String userID, messageID, chatUid, senderUid, receiverUid, projectID,
            techAsReceiver, techAsSender;
    private RecyclerView recyclerView_chatProfiles;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        senderUid = userID;
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        setRef();
        generateRecyclerLayout();
        clickListeners();

    }

    private void clickListeners() {
        adapterChatUserListItem.setOnItemClickListener(new AdapterChatUserListItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);
                String projName = arr.get(position).receiverName;

                generateChatId(position, projName);

            }
        });
    }

    private void generateChatId(int position, String projName) {


        chatDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    techAsSender = chat.senderUid;
                    techAsReceiver = chat.receiverUid;

                    if(chat.getSenderUid().equals(userID) && chat.getReceiverName().equals(projName))
                    {
                        chatUid = dataSnapshot.getKey().toString();
                        senderUid = techAsSender;
                        receiverUid = techAsReceiver;

                        generateProjId(position, projName);
                    }

                    else if(chat.getReceiverUid().equals(userID) && chat.getReceiverName().equals(projName))
                    {
                        chatUid = dataSnapshot.getKey().toString();
                        senderUid = techAsSender;
                        receiverUid = techAsReceiver;

                        generateProjId(position, projName);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateProjId(int position, String projName) {


        Query query = projDatabase
                .orderByChild("projName")
                .equalTo(projName);

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

    private void setRef() {

        recyclerView_chatProfiles = findViewById(R.id.recyclerView_chatProfiles);
        progressBar = findViewById(R.id.progressBar);
    }
}