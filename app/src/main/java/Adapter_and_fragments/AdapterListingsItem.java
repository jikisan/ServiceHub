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

public class AdapterListingsItem extends RecyclerView.Adapter<AdapterListingsItem.ItemViewHolder>{

    List<Listings> arr;
    AdapterListingsItem.OnItemClickListener onItemClickListener;


    public AdapterListingsItem(List<Listings> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;


        Listings listings = arr.get(position);
        holder.listName.setText(listings.getListName());
        holder.listRatings.setText(listings.getRatings());

        imageUriText = listings.getImageUrl();


        Picasso.get()
                .load(imageUriText)
                .into(holder.listImage);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterListingsItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView listName, listRatings;
        ImageView listImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage = itemView.findViewById(R.id.iv_projectPhoto);
            listName = itemView.findViewById(R.id.tv_projName);
            listRatings = itemView.findViewById(R.id.tv_listingRatings);

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
