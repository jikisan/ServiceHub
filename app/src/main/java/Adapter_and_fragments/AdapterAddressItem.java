package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.MyAddress;
import com.example.servicehub.R;

import java.util.List;

public class AdapterAddressItem extends RecyclerView.Adapter<AdapterAddressItem.ItemViewHolder>{

    private List<MyAddress> arr;

    public AdapterAddressItem() {
    }

    public AdapterAddressItem(List<MyAddress> arr) {
        this.arr = arr;
    }

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterAddressItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_addresses,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        MyAddress myAddress = arr.get(position);
        String addressLabel = "Address " + String.valueOf(position + 1);
        holder.tv_addrssLabel.setText(addressLabel);
        holder.tv_addressValue.setText(myAddress.getAddressValue());

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

        TextView tv_addrssLabel, tv_addressValue;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_addrssLabel = itemView.findViewById(R.id.tv_addrssLabel);
            tv_addressValue = itemView.findViewById(R.id.tv_addressValue);

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
