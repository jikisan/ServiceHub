package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Chat;
import com.example.servicehub.Messages;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

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

        holder.tv_chatName.setText(chat.getReceiverName());

        String imageUriText = chat.getReceiverPhotoUrl();


        Picasso.get()
                .load(imageUriText)
                .into(holder.iv_chatProfilePhoto);

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

        ImageView iv_chatProfilePhoto;
        TextView tv_chatName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_chatProfilePhoto = itemView.findViewById(R.id.iv_chatProfilePhoto);
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
