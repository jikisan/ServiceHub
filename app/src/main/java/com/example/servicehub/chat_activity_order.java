package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class chat_activity_order extends AppCompatActivity {

    private LinearLayout layout;
    private ImageView sendButton, iv_listPhotoInChat;
    private EditText messageArea;
    private TextView tv_listNameInChat;

    private ScrollView scrollView;
    private Firebase reference1, reference2;
    private DatabaseReference messageDatabase, chatDatabase, userDatabase, listDatabase;
    private String chatUid, userID, senderUid, senderPhotoUrl, listingID, receiverUid, receiverPhotoUrl,
            receiverName, tempChatUid, chatType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_order);

        chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        listDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        chatType = "listing";
        listingID = getIntent().getStringExtra("listing id");
        tempChatUid = getIntent().getStringExtra("chat id");

        String[] chatUidSplit = tempChatUid.split("_");
        receiverUid = chatUidSplit[1];
        senderUid = chatUidSplit[0];

        chatUid = tempChatUid;

        setRef();
        generateProjectData();
        eventListeners();
        clickListeners();
    }

    private void generateProjectData() {

        listDatabase.child(listingID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listings listings = snapshot.getValue(Listings.class);

                if(snapshot.exists()){
                    receiverPhotoUrl = listings.imageUrl;
                    receiverName = listings.listName;

                    tv_listNameInChat.setText(receiverName);
                    Picasso.get().load(receiverPhotoUrl).into(iv_listPhotoInChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void eventListeners() {

        Query query = messageDatabase
                .orderByChild("chatUid")
                .equalTo(chatUid);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                String message = messages.getMessage();


                if(messages.senderUid.equals(userID))
                {
                    addMessageBox( message, 1);
                }
                else
                {
                    addMessageBox( message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void clickListeners() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Chat chat = new Chat(chatType, senderUid, senderPhotoUrl, receiverUid,
                        receiverPhotoUrl, receiverName);

                String tempSenderUid;

                tempSenderUid = senderUid;
                senderUid = userID;
                receiverUid = tempSenderUid;
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){

                    chatDatabase.child(chatUid).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                addDataToMessageDB(senderUid, receiverUid);

                            }
                        }


                    });

                }
            }
        });
    }

    private void addDataToMessageDB(String senderUid, String receiverUid) {

        String messageText = messageArea.getText().toString();
        Messages messages = new Messages(senderUid, receiverUid, chatUid, messageText);

        messageDatabase.push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                messageArea.setText("");
            }
        });

    }

    private void setRef() {
        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        tv_listNameInChat = findViewById(R.id.tv_listNameInChat);
        iv_listPhotoInChat = findViewById(R.id.iv_listPhotoInChat);
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(chat_activity_order.this);
        textView.setText(message);
        textView.setTextSize(16);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        textView.setLayoutParams(lp);

        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            textView.setTextColor(Color.WHITE);


        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            textView.setTextColor(Color.BLACK);


        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}