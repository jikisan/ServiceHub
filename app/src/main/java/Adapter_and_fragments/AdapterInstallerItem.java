package Adapter_and_fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterInstallerItem extends RecyclerView.Adapter<AdapterInstallerItem.ItemViewHolder> {

    List<Projects> arr;
    OnItemClickListener onItemClickListener;


    private Context context;
    private Location currentlocation;
    private LatLng currentLatLng;


    public AdapterInstallerItem() {
    }

    public AdapterInstallerItem(LatLng currentLatLng, List<Projects> arr, Context context) {
        this.currentLatLng = currentLatLng;
        this.arr = arr;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Projects project = arr.get(position);

        double priceDouble = Double.parseDouble(project.getPrice());
        double lat = Double.parseDouble(project.getLatitude());
        double lng =  Double.parseDouble(project.getLongitude());

       if(currentLatLng != null)
       {
           double distance = SphericalUtil.computeDistanceBetween(currentLatLng,
                   new LatLng(lat, lng));

           DecimalFormat df = new DecimalFormat("#.00");
           df.format(distance);

           if (distance > 1000) {
               double kilometers = distance / 1000;
               holder.txtLocationDistance.setText(df.format(kilometers) + " KM");
           } else {
               holder.txtLocationDistance.setText(distance + " Meters");
           }
       }


        holder.projName.setText(project.getProjName());
        holder.projPrice.setText("" + priceDouble);
        holder.tv_userRatingCount.setText("(" + project.getRatingCount() + ")");
        holder.rb_userRating.setRating((float) project.getRatingAverage());

        String imageUriText = project.getImageUrl();

        Picasso.get().load(imageUriText)
                .into(holder.projectImage);
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

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView projName, projRatings, projPrice, tv_userRatingCount, txtLocationDistance;
        ImageView projectImage;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            projRatings = itemView.findViewById(R.id.tv_projRatings);
            projPrice = itemView.findViewById(R.id.tv_price);
            tv_userRatingCount = itemView.findViewById(R.id.tv_userRatingCount);
            txtLocationDistance = itemView.findViewById(R.id.txtLocationDistance);
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
