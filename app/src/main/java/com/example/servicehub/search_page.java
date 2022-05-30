package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterInstallerItem;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class search_page extends AppCompatActivity {

    private View linearLayout10;
    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private TextView tv_back, tv_headerTitle, tv_headerTitle2;
    private SearchView sv_search;
    private RecyclerView recyclerView_searches;
    private ProgressBar progressBar;
    private AutoCompleteTextView auto_complete_txt, auto_complete_txt_sort;
    private String listOfCategory = "", userID;
    private CardView cardViewBottom;
    private final String[] categoryArrayFix = {"All services", "Installation","Repair","Cleaning","Heating","Ventilation","Others"};
    private final String[] sortArrayFix = {"Name (A-Z)", "Name (Z-A)", "Price (Highest - Lowest)", "Price (Lowest - Highest)" , "Ratings (Highest - Lowest)",
            "Ratings (Lowest - Highest)" };

    private ArrayAdapter<CharSequence> adapterCategoryItems;
    private ArrayAdapter<CharSequence> adapterSortItems;
    private AdapterInstallerItem adapterInstallerItem, adapter;
    private DatabaseReference projDatabase;
    private ArrayList<Projects> arrProj, arr;
    private ArrayList<String> arrCategory;
    private ArrayList<DistanceDouble> arrDistance;
    private ArrayList<CurrentLocation> arrCurrentLocaction;
    private FirebaseUser user;

    private FusedLocationProviderClient client;
    private Location currentlocation1;
    private LatLng currentLatLng;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        client = LocationServices.getFusedLocationProviderClient(search_page.this);
        validatePermission();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");


        setRef();
        userValidation();
        generateDataValue();
        generateRecyclerLayout();
        dropDownMenuTextView();
        clickListeners();
        bottomNavTaskbar();

    }

    private void userValidation() {
        String userType = getIntent().getStringExtra("user type");

        if(!(userType == null))
        {
            if(userType.equals("guest"))
            {
                cardViewBottom.setVisibility(View.GONE);
            }
        }
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        auto_complete_txt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                arrProj.clear();


                String category = adapterView.getItemAtPosition(i).toString();
                tv_headerTitle.setText(category);

                if(category == "All services")
                {
                    generateAllProjects();
                }
                else
                {
                    generateRecyclerLayoutByCategory(category);
                }

            }
        });

        auto_complete_txt_sort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String category = adapterView.getItemAtPosition(i).toString();
                tv_headerTitle2.setText(category);


                switch (i){

                    case 0:
                        Collections.sort(arrProj, nameAZ);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 1:
                        Collections.sort(arrProj, nameAZ);
                        Collections.reverse(arrProj);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 2:
                        Collections.sort(arrProj, priceLowestToHighest);
                        Collections.reverse(arrProj);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 3:
                        Collections.sort(arrProj, priceLowestToHighest);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 4:
                        Collections.sort(arrProj, ratingsLowestToHighest);
                        Collections.reverse(arrProj);
                        adapterInstallerItem.notifyDataSetChanged();
                      break;

                    case 5:
                        Collections.sort(arrProj, ratingsLowestToHighest);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;
                    case 6:
                        Collections.sort(arrDistance, distanceNearest);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;
                    case 7:
                        Collections.sort(arrDistance, distanceNearest);
                        adapterInstallerItem.notifyDataSetChanged();
                        Collections.reverse(arrDistance);
                        break;

                    default:
                        Collections.sort(arrProj, nameAZ);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                }

            }


            public Comparator<DistanceDouble> distanceNearest = new Comparator<DistanceDouble>() {
                @Override
                public int compare(DistanceDouble distanceDouble, DistanceDouble t1) {
                    return String.valueOf(distanceDouble.getDistance()).compareToIgnoreCase(String.valueOf(t1.getDistance()));
                }
            };


            public Comparator<Projects> nameAZ = new Comparator<Projects>() {
                @Override
                public int compare(Projects projects, Projects t1) {
                    return projects.getProjName().compareTo(t1.getProjName());
                }
            };

            public Comparator<Projects> priceLowestToHighest = new Comparator<Projects>() {
                @Override
                public int compare(Projects projects, Projects t1) {

                    String p1 = projects.getPrice();
                    String p2 = t1.getPrice();

                    return extractInt(p1) - extractInt(p2);
                }

                int extractInt(String s) {
                    String num = s.replaceAll("\\D", "");
                    // return 0 if no digits found
                    return num.isEmpty() ? 0 : Integer.parseInt(num);
                }
            };

            public Comparator<Projects> ratingsLowestToHighest = new Comparator<Projects>() {
                @Override
                public int compare(Projects projects, Projects t1) {
                    return String.valueOf(projects.getRatingAverage()).compareToIgnoreCase(String.valueOf(t1.getRatingAverage()));
                }
            };


        });

        adapterInstallerItem.setOnItemClickListener(new AdapterInstallerItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                arrProj.get(position);

                if(user == null)
                {
                    goToNextActivityGuest(position);
                }
                else
                {
                    goToNextActivity(position);
                }

            }
        });

        linearLayout10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProject = new Intent(search_page.this, view_in_map.class);
                intentProject.putExtra("Category", "all");
                startActivity(intentProject);
            }
        });

    }

    private void generateRecyclerLayout() {

        recyclerView_searches.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_searches.setLayoutManager(linearLayoutManager);

        adapter = new AdapterInstallerItem(arrCurrentLocaction, arr, getApplicationContext());
        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arrProj, getApplicationContext());
        recyclerView_searches.setAdapter(adapterInstallerItem);
        adapterInstallerItem.notifyDataSetChanged();
    }

    private void generateListOfCategory() {
        projDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);
                        String category = projects.getCategory();
                        listOfCategory = category +","+ listOfCategory;
                        arrCategory.add(category);
                    }

                    dropDownMenuTextView();
                    progressBar.setVisibility(View.GONE);
                    adapterInstallerItem.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateRecyclerLayoutByCategory( String category) {
        Query query = projDatabase
                .orderByChild("category")
                .equalTo(category);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);
                        if(projects.getUserID().equals(userID))
                        {
                            continue;
                        }
                        arrProj.add(projects);

                    }
                    adapterInstallerItem.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataValue() {
        if(projDatabase != null)
        {
            generateAllProjects();

        }
        if(sv_search != null)
        {
            sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return false;
                }
            });

        }
    }

    private void generateAllProjects() {

        projDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);
                        if(projects.getUserID().equals(userID))
                        {
                            continue;
                        }
                        arrProj.add(projects);
                        double lat = Double.parseDouble(projects.getLatitude());
                        double lng =  Double.parseDouble(projects.getLongitude());
                        getDistance(arrCurrentLocaction, lat, lng);
                    }

                    progressBar.setVisibility(View.GONE);
                    adapterInstallerItem.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_page.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDistance(ArrayList<CurrentLocation> arrCurrentLocaction, double lat, double lng) {
        CurrentLocation currentLocation = arrCurrentLocaction.get(0);
        LatLng currentLoc = currentLocation.getCurrentLocation();

        double distance = SphericalUtil.computeDistanceBetween(currentLoc,
                new LatLng(lat, lng));

        DistanceDouble distanceDouble = new DistanceDouble(distance);
        arrDistance.add(distanceDouble);

    }

    private void dropDownMenuTextView() {

        adapterCategoryItems = new ArrayAdapter<CharSequence>(search_page.this, R.layout.list_property, categoryArrayFix);
        auto_complete_txt.setAdapter(adapterCategoryItems);

        adapterSortItems = new ArrayAdapter<CharSequence>(search_page.this, R.layout.list_property, sortArrayFix);
        auto_complete_txt_sort.setAdapter(adapterSortItems);

    }

    private void search(String s) {
        arr = new ArrayList<>();
        for(Projects object : arrProj)
        {
            if(object.getProjName().toLowerCase().contains(s.toLowerCase()))
            {
                arr.add(object);
            }

            adapter = new AdapterInstallerItem(arrCurrentLocaction, arr, getApplicationContext());
            recyclerView_searches.setAdapter(adapter);
        }

        adapter.setOnItemClickListener(new AdapterInstallerItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);


                if(user == null)
                {
                    goToNextActivityGuest(position);
                }
                else
                {
                    goToNextActivity(position);
                }

            }
        });

    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(search_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(search_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(search_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(search_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(search_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void setRef() {

        arrProj = new ArrayList<>();
        arrCategory = new ArrayList<>();
        arrCurrentLocaction = new ArrayList<>();
        arrDistance = new ArrayList<>();

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        tv_back = findViewById(R.id.tv_back);
        tv_headerTitle = findViewById(R.id.tv_headerTitle);
        tv_headerTitle2 = findViewById(R.id.tv_headerTitle2);

        cardViewBottom = findViewById(R.id.cardViewBottom);

        sv_search = findViewById(R.id.sv_search);

        recyclerView_searches = findViewById(R.id.recyclerView_searches);

        progressBar = findViewById(R.id.progressBar);

        auto_complete_txt = findViewById(R.id.auto_complete_txt);
        auto_complete_txt_sort = findViewById(R.id.auto_complete_txt_sort);

        linearLayout10 = findViewById(R.id.linearLayout10);

    }

    private void goToNextActivityGuest(int position) {
        Query query = projDatabase
                .orderByChild("projName")
                .equalTo(arrProj.get(position).getProjName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String projectID = dataSnapshot.getKey().toString();
                    Intent intentProject = new Intent(search_page.this, booking_page_for_guest.class);
                    intentProject.putExtra("Project ID", projectID);
                    startActivity(intentProject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterInstallerItem.notifyItemChanged(position);
    }

    private void goToNextActivity(int position) {
        Query query = projDatabase
                .orderByChild("projName")
                .equalTo(arrProj.get(position).getProjName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String projectID = dataSnapshot.getKey().toString();
                    Intent intentProject = new Intent(search_page.this, booking_page.class);
                    intentProject.putExtra("Project ID", projectID);
                    startActivity(intentProject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterInstallerItem.notifyItemChanged(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validatePermission() {

        // check condition
        if (ContextCompat.checkSelfPermission(search_page.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(search_page.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
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
        LocationManager locationManager = (LocationManager) search_page.this.getSystemService(Context.LOCATION_SERVICE);
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
                        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arrProj, getApplicationContext());
                        recyclerView_searches.setAdapter(adapterInstallerItem);
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
                                adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocaction, arrProj, getApplicationContext());
                                recyclerView_searches.setAdapter(adapterInstallerItem);
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
            Toast.makeText(search_page.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


}