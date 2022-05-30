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

import com.example.servicehub.CurrentLocation;
import com.example.servicehub.DistanceDouble;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.example.servicehub.booking_page_for_guest;
import com.example.servicehub.search_page;
import com.firebase.client.FirebaseError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterInstallerItem extends RecyclerView.Adapter<AdapterInstallerItem.ItemViewHolder> {

    List<Projects> arr;
    List<CurrentLocation> arrCurrentLocaction;
    List<DistanceDouble> arrDistance;
    AdapterInstallerItem.OnItemClickListener onItemClickListener;

    private Context context;
    private Location currentlocation;
    private LatLng currentLatLng;
    private FirebaseUser user;

    public AdapterInstallerItem() {
    }

    public AdapterInstallerItem(List<CurrentLocation> arrCurrentLocaction, List<Projects> arr, Context context) {
        this.arrCurrentLocaction = arrCurrentLocaction;
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

        CurrentLocation currentLocation = arrCurrentLocaction.get(0);
        user = FirebaseAuth.getInstance().getCurrentUser();

        currentLatLng = currentLocation.getCurrentLocation();
        double priceDouble = Double.parseDouble(project.getPrice());
        double lat = Double.parseDouble(project.getLatitude());
        double lng =  Double.parseDouble(project.getLongitude());




       if(currentLatLng != null)
       {
           double distance = SphericalUtil.computeDistanceBetween(currentLatLng,
                   new LatLng(lat, lng));

           DistanceDouble distanceDouble = new DistanceDouble(distance);

           double finalDistance = distanceDouble.getDistance();

           DecimalFormat df = new DecimalFormat("#.00");
           df.format(finalDistance);

           if (finalDistance > 1000) {
               double kilometers = finalDistance / 1000;
               holder.txtLocationDistance.setText(df.format(kilometers) + " KM");
           } else {
               holder.txtLocationDistance.setText(df.format(finalDistance) + " m");
           }
       }

        String projName = project.getProjName();
        holder.projName.setText(projName);
        holder.projPrice.setText("" + priceDouble);
        holder.tv_userRatingCount.setText("(" + project.getRatingCount() + ")");
        holder.rb_userRating.setRating((float) project.getRatingAverage());

        String imageUriText = project.getImageUrl();

        Picasso.get().load(imageUriText)
                .into(holder.projectImage);

        DatabaseReference projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user == null)
                {
                    goToNextActivityGuest(projName, view);
                }
                else
                {
                    goToNextActivity(projName, view);
                }


            }

            private void goToNextActivityGuest(String projName, View view) {
                Query query = projDatabase
                        .orderByChild("projName")
                        .equalTo(projName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            String projectID = dataSnapshot.getKey().toString();
                            Intent intentProject = new Intent(view.getContext(), booking_page_for_guest.class);
                            intentProject.putExtra("Project ID", projectID);
                            view.getContext().startActivity(intentProject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            private void goToNextActivity(String projName, View view) {
                Query query = projDatabase
                        .orderByChild("projName")
                        .equalTo(projName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            String projectID = dataSnapshot.getKey().toString();
                            Intent intentProject = new Intent(view.getContext(), booking_page.class);
                            intentProject.putExtra("Project ID", projectID);
                            view.getContext().startActivity(intentProject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterInstallerItem.OnItemClickListener listener){
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
