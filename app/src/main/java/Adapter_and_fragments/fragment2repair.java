package Adapter_and_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.CurrentLocation;
import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.example.servicehub.edit_project_page;
import com.example.servicehub.view_in_map;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class fragment2repair extends Fragment {

    private ArrayAdapter<CharSequence> adapterSortItems;
    private List<Projects> arrProjects;
    private List<CurrentLocation> arrCurrentLocation;
    private AdapterInstallerItem adapterInstallerItem;
    private String userID, projectID, projCategory;
    private FirebaseUser user;
    private DatabaseReference projDatabase, marketDatabase;
    private ImageView iv_sort, iv_Location;
    private TextView tv_category, tv_headerTitle2;
    private RecyclerView recyclerView;
    private AutoCompleteTextView auto_complete_txt_sort;

    private FusedLocationProviderClient client;
    private Location currentlocation1;
    private LatLng currentLatLng;
    private final String[] sortArrayFix = {"Name (A-Z)", "Name (Z-A)", "Price (Highest - Lowest)", "Price (Lowest - Highest)" , "Ratings (Highest - Lowest)",
            "Ratings (Lowest - Highest)" };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment2repair() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2repair.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment2repair newInstance(String param1, String param2) {
        fragment2repair fragment = new fragment2repair();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        client = LocationServices.getFusedLocationProviderClient(getActivity());


        View view = inflater.inflate(R.layout.fragment1_installer, container, false);

        auto_complete_txt_sort = (AutoCompleteTextView) view.findViewById(R.id.auto_complete_txt_sort);
        iv_Location = (ImageView) view.findViewById(R.id.iv_Location);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        tv_headerTitle2 = (TextView) view.findViewById(R.id.tv_headerTitle2);

        projCategory = "Repair";

        user = FirebaseAuth.getInstance().getCurrentUser();
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        marketDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        userID = user.getUid();

        generateRecyclerLayout(view);
        onClickToGetKeyProj();
        getProjByCategory();
        dropDownMenuTextView();
        clickListeners();
        // Inflate the layout for this fragment
        return view;
    }

    private void generateRecyclerLayout(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewInstallers);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        arrProjects = new ArrayList<>();
        arrCurrentLocation = new ArrayList<>();
        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocation, arrProjects, getContext());


        recyclerView.setAdapter(adapterInstallerItem);

        validatePermission();
    }

    private void clickListeners() {
        iv_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProject = new Intent(getContext(), view_in_map.class);
                intentProject.putExtra("Category", projCategory);
                startActivity(intentProject);
            }
        });

        auto_complete_txt_sort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String category = adapterView.getItemAtPosition(i).toString();
                tv_headerTitle2.setText(category);


                switch (i){

                    case 0:
                        Collections.sort(arrProjects, nameAZ);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 1:
                        Collections.sort(arrProjects, nameAZ);
                        Collections.reverse(arrProjects);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 2:
                        Collections.sort(arrProjects, priceLowestToHighest);
                        Collections.reverse(arrProjects);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 3:
                        Collections.sort(arrProjects, priceLowestToHighest);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 4:
                        Collections.sort(arrProjects, ratingsLowestToHighest);
                        Collections.reverse(arrProjects);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    case 5:
                        Collections.sort(arrProjects, ratingsLowestToHighest);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                    default:
                        Collections.sort(arrProjects, nameAZ);
                        adapterInstallerItem.notifyDataSetChanged();
                        break;

                }

            }




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

    }

    private void getProjByCategory() {
        Query query = projDatabase
                .orderByChild("category")
                .equalTo("Repair");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Projects projData = snapshot.getValue(Projects.class);

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Projects projects = dataSnapshot.getValue(Projects.class);
                        if(projects.getUserID().equals(userID))
                        {
                            continue;
                        }
                        arrProjects.add(projects);
                    }

                    adapterInstallerItem.notifyDataSetChanged();
                }

                tv_category.setText(projCategory);
                System.out.println("Category: " + "Repair");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onClickToGetKeyProj() {

        adapterInstallerItem.setOnItemClickListener(new AdapterInstallerItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arrProjects.get(position);

                Query query = projDatabase
                        .orderByChild("projName")
                        .equalTo(arrProjects.get(position).getProjName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            projectID = dataSnapshot.getKey().toString();
                            Intent intentProject = new Intent(getContext(), booking_page.class);
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
        });

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
                    Location location  = task.getResult();                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        double latDouble = location.getLatitude();
                        double longDouble = location.getLongitude();
                        currentLatLng = new LatLng(latDouble, longDouble);
                        currentlocation1 = location;
                        CurrentLocation currentLocation = new CurrentLocation(currentLatLng);
                        arrCurrentLocation.add(currentLocation);
                        adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocation, arrProjects, getContext());
                        recyclerView.setAdapter(adapterInstallerItem);
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
                                arrCurrentLocation.add(currentLocation);
                                adapterInstallerItem = new AdapterInstallerItem(arrCurrentLocation, arrProjects, getContext());
                                recyclerView.setAdapter(adapterInstallerItem);
                                adapterInstallerItem.notifyDataSetChanged();

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

    private void dropDownMenuTextView() {

        adapterSortItems = new ArrayAdapter<CharSequence>(getContext(), R.layout.list_property, sortArrayFix);
        auto_complete_txt_sort.setAdapter(adapterSortItems);

    }
}