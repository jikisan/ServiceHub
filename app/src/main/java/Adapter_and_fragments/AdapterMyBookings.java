package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Booking;
import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdapterMyBookings extends RecyclerView.Adapter<AdapterMyBookings.ItemViewHolder> {

    private List<Booking> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterMyBookings(List<Booking> arr) {
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


        Booking booking = arr.get(position);

        holder.tv_bookingName.setText(booking.getProjName());
        holder.tv_bookingTime.setText(booking.getBookingTime());

        String sp_bookingDate = booking.getBookingDate();
        String[] parts = sp_bookingDate.split("/");


        holder.tv_bookingMonth.setText(parts[0]);
        holder.tv_bookingDate.setText(parts[1]);
        holder.tv_bookingDay.setText(parts[3]);


        imageUriText = booking.getImageUrl();
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

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tv_bookingName, tv_bookingDay,tv_bookingDate, tv_bookingMonth, tv_bookingTime;
        ImageView iv_bookingPhoto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_bookingName = itemView.findViewById(R.id.tv_bookingName);
            tv_bookingTime = itemView.findViewById(R.id.tv_bookingTime);
            tv_bookingMonth = itemView.findViewById(R.id.tv_bookingMonth);
            tv_bookingDay = itemView.findViewById(R.id.tv_bookingDay);
            tv_bookingDate = itemView.findViewById(R.id.tv_bookingDate);
            iv_bookingPhoto = itemView.findViewById(R.id.iv_bookingPhoto);

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