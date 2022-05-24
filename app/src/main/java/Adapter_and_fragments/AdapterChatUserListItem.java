package Adapter_and_fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.Chat;
import com.example.servicehub.Messages;
import com.example.servicehub.R;
import com.example.servicehub.cart_page;
import com.example.servicehub.message_page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterChatUserListItem extends RecyclerView.Adapter<AdapterChatUserListItem.ItemViewHolder> {

    private List<Chat> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterChatUserListItem() {
    }

    public AdapterChatUserListItem(List<Chat> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterChatUserListItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_profile,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Chat chat = arr.get(position);

        String senderUid = chat.getSenderUid();
        String receiverUid = chat.getReceiverUid();
        String receiverName = chat.getReceiverName();
        String chatId = senderUid + "_" + receiverUid + "_" + receiverName;

        holder.tv_chatName.setText(chat.getReceiverName());

        String imageUriText = chat.getReceiverPhotoUrl();

        Picasso.get()
                .load(imageUriText)
                .into(holder.iv_chatProfilePhoto);

        holder.iv_deleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference("Chats");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove message?")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                chatDatabase.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {

                                                dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                        deleteMessages(chatId);
                                                    }
                                                });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            private void deleteMessages(String chatId) {
                                DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

                                Query query = messageDatabase
                                        .orderByChild("chatUid")
                                        .equalTo(chatId);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                    Toast.makeText(view.getContext(), "Chat Removed", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(view.getContext(), message_page.class);
                                                    view.getContext().startActivity(intent);

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setContentText("Remove this in the chat?")
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_chatProfilePhoto, iv_deleteChat;
        TextView tv_chatName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_chatProfilePhoto = itemView.findViewById(R.id.iv_chatProfilePhoto);
            iv_deleteChat = itemView.findViewById(R.id.iv_deleteChat);
            tv_chatName = itemView.findViewById(R.id.tv_chatName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }

                }
            });

        }
    }
}
