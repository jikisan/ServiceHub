package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Listings;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.ItemViewHolder> {

    List<Listings> arr;
    AdapterOrderItem.OnItemClickListener onItemClickListener;

    public AdapterOrderItem(List<Listings> arr) {
        this.arr = arr;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;

        Listings listings = arr.get(position);
        holder.orderName.setText(listings.getListName());

        imageUriText = listings.getImageUrl();
        Picasso.get()
                .load(imageUriText)
                .into(holder.orderPhoto);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterOrderItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView orderName;
        ImageView orderPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.tv_orderName);
            orderPhoto = itemView.findViewById(R.id.iv_orderPhoto);

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
