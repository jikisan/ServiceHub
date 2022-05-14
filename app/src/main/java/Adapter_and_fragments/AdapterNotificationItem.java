package Adapter_and_fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.Notification;
import com.example.servicehub.R;
import com.example.servicehub.cart_page;
import com.example.servicehub.notification_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class AdapterNotificationItem extends RecyclerView.Adapter<AdapterNotificationItem.ItemViewHolder> {

    private List<Notification> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterNotificationItem() {
    }

    public AdapterNotificationItem(List<Notification> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterNotificationItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;
        String userID;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Notification notification = arr.get(position);
        holder.tv_notifTitle.setText(notification.getNotifTitle());
        holder.tv_notifMessage.setText(notification.getNotifMessage());

        imageUriText = notification.getNotifImage();

        Picasso.get()
                .load(imageUriText)
                .into(holder.iv_notifPhoto);

        holder.iv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference notifDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove Notification")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Query query = notifDatabase.orderByChild("notifMessage")
                                        .startAt(notification.getNotifMessage()).endAt(notification.getNotifMessage());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            Notification c = dataSnapshot.getValue(Notification.class);
                                            if( c.getUserID().equals(userID) )
                                            {
                                                dataSnapshot.getRef().removeValue();
                                                Toast.makeText(view.getContext(), "Notification Removed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(view.getContext(), notification_page.class);
                                                view.getContext().startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        })
                        .setContentText("Remove this notification?")
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

        TextView tv_notifTitle, tv_notifMessage;
        ImageView iv_notifPhoto, iv_deleteBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notifTitle = itemView.findViewById(R.id.tv_notifTitle);
            tv_notifMessage = itemView.findViewById(R.id.tv_notifMessage);
            iv_notifPhoto = itemView.findViewById(R.id.iv_notifPhoto);
            iv_deleteBtn = itemView.findViewById(R.id.iv_deleteBtn);


            if(onItemClickListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }
}
