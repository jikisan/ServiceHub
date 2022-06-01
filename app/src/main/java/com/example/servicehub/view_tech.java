package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterInstallerItem;

public class view_tech extends AppCompatActivity {

    private List<Projects> arr;
    private AdapterInstallerItem adapterInstallerItem;
    private ArrayList<CurrentLocation> arrCurrentLocaction;

    private TextView tv_back, tv_techName;
    private ImageView iv_techPhoto;
    private RecyclerView rv_proj;
    private ProgressBar progressBar;

    private FusedLocationProviderClient client;
    private String userID, custID;
    private Location currentlocation1;
    private LatLng currentLatLng;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_tech);

        client = LocationServices.getFusedLocationProviderClient(view_tech.this);
        validatePermission();
        arr = new ArrayList<>();
        arrCurrentLocaction = new ArrayList<>();
        String techId = getIntent().getStringExtra("tech id");

        setRef();
        generateTechData(techId);
        generateRecyclerLayout(techId);
        clickListeners();

    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setRef() {
        iv_techPhoto = findViewById(R.id.iv_techPhoto);
        tv_back = findViewById(R.id.tv_back);
        tv_techName = findViewById(R.id.tv_techName);
        rv_proj = findViewById(R.id.rv_proj);
        progressBar = findViewById(R.id.progressBar);
    }

    private void generateTechData(String techId) {
        DatabaseReference techDatabase = FirebaseDatabase.getInstance().getReference("Users");

        techDatabase.child(techId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users tech = snapshot.getValue(Users.class);

                    String sp_imageUrl = tech.imageUrl;
                    String sp_fname = tech.firstName;
                    String sp_lname = tech.lastName;
                    String techName = sp_fname + " " + sp_lname;

                    Picasso.get()
                            .load(sp_imageUrl)
                            .into(iv_techPhoto);

                    tv_techName.setText(techName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecyclerLayout(String techId) {

        rv_proj.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_proj.setLayoutManager(linearLayoutManager);


        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arr, getApplicationContext());
        rv_proj.setAdapter(adapterInstallerItem);
        adapterInstallerItem.notifyDataSetChanged();

        getViewHolderValues(techId);
    }

    private void getViewHolderValues(String techId) {
        DatabaseReference projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        Query query = projDatabase.orderByChild("userID")
                .equalTo(techId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);
                        arr.add(projects);
                    }

                    progressBar.setVisibility(View.GONE);
                    adapterInstallerItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(view_tech.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validatePermission() {

        // check condition
        if (ContextCompat.checkSelfPermission(view_tech.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(view_tech.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // When permission is granted
            // Call method
            getCurrentLocation();
        }
        else
        {
            // When permission is not granted
            // Call method

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) view_tech.this.getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(
                        @NonNull Task<Location> task) {


                    // Initialize location
                    Location location  = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        double latDouble = location.getLatitude();
                        double longDouble = location.getLongitude();
                        currentLatLng = new LatLng(latDouble, longDouble);
                        currentlocation1 = location;
                        CurrentLocation currentLocation = new CurrentLocation(currentLatLng);
                        arrCurrentLocaction.add(currentLocation);
                        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arr, getApplicationContext());
                        rv_proj.setAdapter(adapterInstallerItem);
                        adapterInstallerItem.notifyDataSetChanged();

                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void
                            onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1  = locationResult.getLastLocation();
                                double latDouble = location1.getLatitude();
                                double longDouble = location1.getLongitude();
                                currentLatLng = new LatLng(latDouble, longDouble);
                                currentlocation1 = location1;
                                CurrentLocation currentLocation = new CurrentLocation(currentLatLng);
                                arrCurrentLocaction.add(currentLocation);
                                adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arr, getApplicationContext());
                                rv_proj.setAdapter(adapterInstallerItem);
                                adapterInstallerItem.notifyDataSetChanged();
                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        }
        else
        {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check condition
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call  method
            getCurrentLocation();
        } else {
            // When permission are denied
            // Display toast
            Toast.makeText(view_tech.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}