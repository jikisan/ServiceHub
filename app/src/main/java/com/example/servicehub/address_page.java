package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Adapter_and_fragments.AdapterAddressItem;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class address_page extends AppCompatActivity {

    private FusedLocationProviderClient client;

    private SupportMapFragment supportMapFragment;
    private View view;
    private Double latDouble, longDouble;

    private List<MyAddress> arrMyAddress;
    private AdapterAddressItem adapterAddressItem;
    private DatabaseReference myAddressDatabase;

    private RecyclerView recyclerView_address;
    private TextView tv_addNewAddress, tv_back;
    private ProgressBar progressBar;


    private String userID;
    private LatLng newLocationFromMarker , latLngFromPlaceApi;
    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_page);

        client = LocationServices.getFusedLocationProviderClient(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myAddressDatabase = FirebaseDatabase.getInstance().getReference("Address");
        userID = user.getUid();

        setRef();
        initPlaces();
        generateRecyclerLayout();
        clickListeners();

    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

    }

    private void generateRecyclerLayout() {

        recyclerView_address.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_address.setLayoutManager(linearLayoutManager);

        arrMyAddress = new ArrayList<>();
        adapterAddressItem = new AdapterAddressItem(arrMyAddress);
        recyclerView_address.setAdapter(adapterAddressItem);

        getViewHolderValues();
    }

    private void getViewHolderValues() {

        Query query = myAddressDatabase.orderByChild("custID")
                .startAt(userID).endAt(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    MyAddress myAddress = dataSnapshot.getValue(MyAddress.class);

                    arrMyAddress.add(myAddress);
                }

                progressBar.setVisibility(View.GONE);
                adapterAddressItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(address_page.this, more_page.class);
                startActivity(intent);
            }
        });

        adapterAddressItem.setOnItemClickListener(new AdapterAddressItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arrMyAddress.get(position);

                Toast.makeText(address_page.this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        tv_addNewAddress.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(address_page.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogmap);
                Window w = dialog.getWindow();
                w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.show();

                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(address_page.this);

                mMapView = (MapView) dialog.findViewById(R.id.mapView);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately

                validatePermission(mMapView);

                TextView btn_closeMap = (TextView) dialog.findViewById(R.id.btn_closeMap);
                EditText et_autoCompAddressBar = (EditText) dialog.findViewById(R.id.et_autoCompAddressBar);
                Button btn_addLocation = (Button) dialog.findViewById(R.id.btn_addLocation);

                dialogClickListeners(btn_closeMap, et_autoCompAddressBar, btn_addLocation, dialog, mMapView);



            }
        });
    }

    private void dialogClickListeners(TextView btn_closeMap, EditText et_autoCompAddressBar,
                                      Button btn_addLocation, Dialog dialog, MapView mMapView) {

        btn_closeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {

                        String addressText;

                        Geocoder geocoder = new Geocoder(address_page.this, Locale.getDefault());

                        try {

                            longDouble = newLocationFromMarker.longitude;
                            latDouble = newLocationFromMarker.latitude;

                            List<Address> newMarkerLocation = geocoder.getFromLocation(latDouble, longDouble, 1);

                            addressText =  newMarkerLocation.get(0).getAddressLine(0);

                            new AlertDialog.Builder(address_page.this)
                                    .setIcon(R.drawable.logo)
                                    .setTitle("Add location?")
                                    .setMessage("Add this address?" + "\n\n" + addressText)
                                    .setCancelable(true)
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addAddressToFirebase(addressText);
                                        }
                                    })
                                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                        }
                                    })
                                    .show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        });

        et_autoCompAddressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<com.google.android.libraries.places.api.model.Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(address_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);
            }
        });
    }

    private void addAddressToFirebase(String addressText) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Adding Address...");
        progressDialog.show();

        String size = String.valueOf(arrMyAddress.size() + 1);
        String addressLabel = "Address " + size;
        MyAddress address = new MyAddress(addressLabel, addressText, userID, String.valueOf(longDouble), String.valueOf(latDouble));

        myAddressDatabase.push().setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    new SweetAlertDialog(address_page.this)
                            .setTitleText("Address Added!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(address_page.this, address_page.class);
                                    startActivity(intent);
                                }
                            })
                            .show();

                    Toast.makeText(address_page.this, "Address Added", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(address_page.this, "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validatePermission(MapView mMapView) {

        // check condition
        if (ContextCompat.checkSelfPermission(address_page.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(address_page.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // When permission is granted
            // Call method
            getCurrentLocation(mMapView);
        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(MapView mMapView) {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) address_page.this.getSystemService(Context.LOCATION_SERVICE);
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
                        double myLat = location.getLatitude();
                        double myLong = location.getLongitude();
                        asyncMap(myLat, myLong, mMapView);


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
                                asyncMap(latDouble, longDouble, mMapView);

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

    private void asyncMap(Double myLat, Double myLong, MapView mMapView) {
        // Async map
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                if (ActivityCompat.checkSelfPermission(address_page.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(address_page.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }
                googleMap.setMyLocationEnabled(true);


                //Marking current location
                LatLng location = new LatLng(myLat, myLong);
                newLocationFromMarker = location;
                latDouble = newLocationFromMarker.latitude;
                longDouble = newLocationFromMarker.longitude;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.draggable(true);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 19));
                googleMap.addMarker(markerOptions);

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(@NonNull Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(@NonNull Marker marker) {

                        newLocationFromMarker = marker.getPosition();
                        latDouble = newLocationFromMarker.latitude;
                        longDouble = newLocationFromMarker.longitude;

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(newLocationFromMarker);
                        markerOptions.draggable(true);

                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {

                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        googleMap.clear();
                        newLocationFromMarker = latLng;
                        latDouble = newLocationFromMarker.latitude;
                        longDouble = newLocationFromMarker.longitude;

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.draggable(true);

                        googleMap.addMarker(markerOptions);
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == 100 && resultCode == RESULT_OK){

            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
           Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);


                latDouble = address.get(0).getLatitude();
                longDouble = address.get(0).getLongitude();

                latLngFromPlaceApi = new LatLng(latDouble, longDouble);
                newLocationFromMarker = latLngFromPlaceApi;

                mGoogleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLngFromPlaceApi);
                markerOptions.draggable(true);

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFromPlaceApi, 19));
                mGoogleMap.addMarker(markerOptions);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

       else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
        }

    }

    private void setRef() {

        recyclerView_address = findViewById(R.id.recyclerView_address);
        tv_addNewAddress = findViewById(R.id.tv_addNewAddress);
        tv_back = findViewById(R.id.tv_back);
        progressBar = findViewById(R.id.progressBar);
    }
}