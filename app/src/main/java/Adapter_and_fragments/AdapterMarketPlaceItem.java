package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Listings;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMarketPlaceItem extends RecyclerView.Adapter<AdapterMarketPlaceItem.ItemViewHolder> {

    List<Listings> arr;
    OnItemClickListener onItemClickListener;

    public AdapterMarketPlaceItem(List<Listings> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterMarketPlaceItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_marketplace,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;

        Listings listings = arr.get(position);
        holder.tv_itemName.setText(listings.getListName());
        holder.tv_price.setText(listings.getListPrice());

        imageUriText = listings.getImageUrl();

        Picasso.get().load(imageUriText)
                .into(holder.iv_listingImage);
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


        TextView tv_itemName, tv_itemRatings, tv_price, tv_userRating;
        ImageView iv_listingImage;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_price = itemView.findViewById(R.id.tv_price);
            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            iv_listingImage = itemView.findViewById(R.id.iv_listingImage);
            tv_userRating = itemView.findViewById(R.id.tv_userRating);
            rb_userRating = itemView.findViewById(R.id.rb_userRating);

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
