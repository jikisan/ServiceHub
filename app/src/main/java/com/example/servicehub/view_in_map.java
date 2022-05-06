package com.example.servicehub;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import Adapter_and_fragments.fragment2Map;

public class view_in_map extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FASTEST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION = 99;

    private TextView tv_back;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallBack;
    private Location currentLocation;
    private List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_in_map);

        //setAllPropertiesOfLocalRequest();
       // updateGPS();

        Fragment fragment2Map = new fragment2Map();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment2Map)
                .commit();

        setRef();
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
        tv_back = findViewById(R.id.tv_back);
    }

    private void setAllPropertiesOfLocalRequest() {
        //current location
        //list of saved location
        // set all properties of LocalRequest
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // event is triggered whenever update interval is met
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location

            }
        };
    }

    private void updateGPS(){
        //get permissions from the user to track GPS
        //get the current location from the fused client
        //update the UI

        try {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view_in_map.this);
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // we got permission. Put the values of location.
                        currentLocation = location;
                        Toast.makeText(view_in_map.this, "VIM: " + currentLocation, Toast.LENGTH_SHORT).show();

                        Geocoder geocoder = new Geocoder(view_in_map.this);
                        try{
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String addressLine = (addresses.get(0).getAddressLine(0));

                            System.out.println("Current loc: " + currentLocation.getLatitude() + ":" + currentLocation.getLongitude());
                            String latlngString = currentLocation.getLatitude() + ":" + currentLocation.getLongitude();
                            System.out.println(addressLine);

                            Bundle bundle = new Bundle();
                            bundle.putString("latlng", latlngString);
                            fragment2Map fragobj = new fragment2Map();
                            fragobj.setArguments(bundle);
                        }
                        catch (Exception e){

                        }
                    }
                });
            }
            else
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

        }catch (Exception e){
            System.out.println("Log: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    } //end of updateGPS method

}