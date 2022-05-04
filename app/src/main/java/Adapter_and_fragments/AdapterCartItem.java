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

import com.example.servicehub.Booking;
import com.example.servicehub.Cart;
import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.ItemViewHolder> {

    private List<Cart> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterCartItem(List<Cart> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterCartItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;

        Cart cart = arr.get(position);
        holder.listName.setText(cart.getListName());
        holder.listPrice.setText(cart.getListPrice());
        holder.listRatings.setText(cart.getListRatings());

        imageUriText = cart.getListImageUrl();


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

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView listName, listPrice, listRatings;
        ImageView listImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage = itemView.findViewById(R.id.iv_listingImage);
            listName = itemView.findViewById(R.id.tv_itemName);
            listPrice = itemView.findViewById(R.id.tv_price);
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
