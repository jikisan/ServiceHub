package Adapter_and_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class fragment2Map extends Fragment {

    private FusedLocationProviderClient client;

    private SupportMapFragment supportMapFragment;
    private View view;
    private Double latDouble, longDouble;
    private String projCategory;

    double latitude, longitude;
    private LatLng location;
    private ArrayList<LatLng> arrLoc;
    private ArrayList<String> arrName;
    private ArrayList<String> arrPrice;
    private ArrayList<URL> arrProjImageUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        validatePermission();
        init(inflater, container, savedInstanceState);

        arrLoc = new ArrayList<>();
        arrName = new ArrayList<>();
        arrPrice = new ArrayList<>();
        arrProjImageUrl = new ArrayList<>();

        return view;
    }

    private void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2_map, container, false);

        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
    }

    private void validatePermission() {

        // check condition
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            getCurrentLocation();
        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(
                        @NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        latDouble = location.getLatitude();
                        longDouble = location.getLongitude();
                        asyncMap(latDouble, longDouble);


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
                                Location location1 = locationResult.getLastLocation();
                                latDouble = location1.getLatitude();
                                longDouble = location1.getLongitude();
                                asyncMap(latDouble, longDouble);

                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void asyncMap(Double latDouble, Double longDouble) {
        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng location = new LatLng(latDouble, longDouble);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title("My Location");
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

                //googleMap.addMarker(markerOptions);

                projCategory = getActivity().getIntent().getStringExtra("Category");
                if(projCategory.equals("Marketplace"))
                {
                    generateListingDataFromFirebase(googleMap);
                }
                else if(projCategory.equals("booking"))
                {
                    generateBookingFromFireBase(googleMap);
                }
                else if(projCategory.equals("orders"))
                {
                    generateOrderFromFireBase(googleMap);
                }
                else
                {
                    generateProjDataFromFirebase(googleMap);
                }


            }
        });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void generateOrderFromFireBase(GoogleMap googleMap) {
        String latString = getActivity().getIntent().getStringExtra("latString");
        String longString = getActivity().getIntent().getStringExtra("longString");

        double longitude = Double.parseDouble(longString);
        double latitude = Double.parseDouble(latString);

        LatLng latLng = new LatLng(latitude, longitude);


        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Client Location"));
    }

    private void generateBookingFromFireBase(GoogleMap googleMap) {

        String latString = getActivity().getIntent().getStringExtra("latString");
        String longString = getActivity().getIntent().getStringExtra("longString");
        String title = getActivity().getIntent().getStringExtra("Marker Title");

        double longitude = Double.parseDouble(longString);
        double latitude = Double.parseDouble(latString);

        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    private void generateListingDataFromFirebase(GoogleMap googleMap) {

        DatabaseReference listDatabase = FirebaseDatabase.getInstance().getReference("Listings");


        listDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Listings listings = dataSnapshot.getValue(Listings.class);

                        String imageUrl = listings.getImageUrl().toString();
                        String latString = listings.getLatitude();
                        String longString = listings.getLongitude();
                        String listName = listings.getListName().toString().toUpperCase(Locale.ROOT);
                        String listPrice = listings.getListPrice().toString();

                        latitude = Double.parseDouble(latString);
                        longitude = Double.parseDouble(longString);
                        location = new LatLng(latitude, longitude);

                        arrLoc.add(location);
                        arrName.add(listName);
                        arrPrice.add("₱ " + listPrice);

                        for (int i = 0; i < arrLoc.size(); i++) {

                            // below line is use to add marker to each location of our array list.
                            googleMap.addMarker(new MarkerOptions()
                                    .position(arrLoc.get(i))
                                    .title(arrName.get(i)
                                    + "\n" + arrPrice.get(i)));

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateProjDataFromFirebase(GoogleMap googleMap){

        DatabaseReference projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        projCategory = getActivity().getIntent().getStringExtra("Category");

        Query query = projDatabase
                .orderByChild("category")
                .equalTo(projCategory);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);

                        String imageUrl = projects.getImageUrl().toString();
                        String projName = projects.getProjName().toString().toUpperCase(Locale.ROOT);
                        String latString = projects.getLatitude();
                        String longString = projects.getLongitude();
                        //String projLatLng = projects.getProjLatLng().toString();

                       //String[] pos = projLatLng.split(",");
                        latitude = Double.parseDouble(latString);
                        longitude = Double.parseDouble(longString);
                        location = new LatLng(latitude, longitude);

                        arrLoc.add(location);
                        arrName.add(projName);


                        for (int i = 0; i < arrLoc.size(); i++) {

                            // below line is use to add marker to each location of our array list.
                            googleMap.addMarker(new MarkerOptions()
                                    .position(arrLoc.get(i))
                                    .title(arrName.get(i)));

                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}