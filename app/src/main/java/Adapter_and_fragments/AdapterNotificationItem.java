package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.Notification;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

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

        Notification notification = arr.get(position);
        holder.tv_notifTitle.setText(notification.getNotifTitle());
        holder.tv_notifMessage.setText(notification.getNotifMessage());

        imageUriText = notification.getNotifImage();

        Picasso.get()
                .load(imageUriText)
                .into(holder.iv_notifPhoto);

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
        ImageView iv_notifPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notifTitle = itemView.findViewById(R.id.tv_notifTitle);
            tv_notifMessage = itemView.findViewById(R.id.tv_notifMessage);
            iv_notifPhoto = itemView.findViewById(R.id.iv_notifPhoto);


            if(onItemClickListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }
}
