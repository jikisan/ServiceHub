package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterBookingItem extends RecyclerView.Adapter<AdapterBookingItem.ItemViewHolder> {

    List<Projects> arr;
    AdapterBookingItem.OnItemClickListener onItemClickListener;

    public AdapterBookingItem(List<Projects> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;

        Projects projects = arr.get(position);
        holder.tv_bookingName.setText(projects.getProjName());

        imageUriText = projects.getImageUrl();
        Picasso.get()
                .load(imageUriText)
                .into(holder.iv_bookingPhoto);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterBookingItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tv_bookingName, tv_bookingTimeDifferece, tv_bookingDay,tv_bookingDate;
        ImageView iv_bookingPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_bookingName = itemView.findViewById(R.id.tv_bookingName);
            tv_bookingTimeDifferece = itemView.findViewById(R.id.tv_bookingTimeDifferece);
            tv_bookingDay = itemView.findViewById(R.id.tv_bookingDay);
            tv_bookingDate = itemView.findViewById(R.id.tv_bookingDate);
            iv_bookingPhoto = itemView.findViewById(R.id.iv_bookingPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                    }   }
                }
            });


        }
    }

}