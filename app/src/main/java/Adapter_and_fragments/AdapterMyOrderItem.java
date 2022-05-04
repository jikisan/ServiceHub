package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Booking;
import com.example.servicehub.Listings;
import com.example.servicehub.Orders;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMyOrderItem extends RecyclerView.Adapter<AdapterMyOrderItem.ItemViewHolder> {

    private List<Orders> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterMyOrderItem() {
    }

    public AdapterMyOrderItem(List<Orders> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Orders orders = arr.get(position);
        String imageUrl = orders.getImageUrl();
        holder.orderName.setText(orders.getItemName());
        holder.orderPrice.setText(orders.getTotalPayment());

        Picasso.get()
                .load(imageUrl)
                .into(holder.orderPhoto);
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

        TextView orderName, orderPrice;
        ImageView orderPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            orderPhoto = itemView.findViewById(R.id.iv_orderPhoto);
            orderName = itemView.findViewById(R.id.tv_orderName);
            orderPrice = itemView.findViewById(R.id.tv_orderPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null) onItemClickListener.onItemClick(position);


                }
            });
        }
    }
}
