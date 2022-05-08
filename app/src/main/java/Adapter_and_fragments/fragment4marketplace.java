package Adapter_and_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.example.servicehub.place_order_page;
import com.example.servicehub.view_in_map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment4marketplace#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment4marketplace extends Fragment {

    private List<Listings> arrListings;
    private AdapterMarketPlaceItem adapterMarketPlaceItem;
    private String userID,  listingID, listCategory;
    private FirebaseUser user;
    private DatabaseReference  marketDatabase;
    private ImageView iv_sort, iv_Location;
    private TextView tv_category;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment4marketplace() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment4marketplace.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment4marketplace newInstance(String param1, String param2) {
        fragment4marketplace fragment = new fragment4marketplace();
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



        View view = inflater.inflate(R.layout.fragment1_installer, container, false);

        iv_sort = (ImageView) view.findViewById(R.id.iv_sort);
        iv_Location = (ImageView) view.findViewById(R.id.iv_Location);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        listCategory = "Marketplace";

        user = FirebaseAuth.getInstance().getCurrentUser();
        marketDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        userID = user.getUid();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewInstallers);
        recyclerView.setHasFixedSize(true);


        arrListings = new ArrayList<>();
        adapterMarketPlaceItem = new AdapterMarketPlaceItem(arrListings);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapterMarketPlaceItem);

        getMarketPlaceItem();
        onClickToGetKeyList();

        clickListeners();

        // Inflate the layout for this fragment
        return view;
    }

    private void clickListeners() {
        iv_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProject = new Intent(getContext(), view_in_map.class);
                intentProject.putExtra("Category", listCategory);
                startActivity(intentProject);
            }
        });

        iv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sort Clicked4", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMarketPlaceItem() {
        marketDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Listings listingsData = dataSnapshot.getValue(Listings.class);
                        arrListings.add(listingsData);
                    }

                    adapterMarketPlaceItem.notifyDataSetChanged();
                }

                tv_category.setText(listCategory);
                System.out.println("Category: " + listCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onClickToGetKeyList() {

        adapterMarketPlaceItem.setOnItemClickListener(new AdapterMarketPlaceItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arrListings.get(position);

                Query query = marketDatabase
                        .orderByChild("listName")
                        .equalTo(arrListings.get(position).getListName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            listingID = dataSnapshot.getKey().toString();
                            Intent intentProject = new Intent(getContext(), place_order_page.class);
                            intentProject.putExtra("Listing ID", listingID);
                            startActivity(intentProject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                adapterMarketPlaceItem.notifyItemChanged(position);
            }
        });
    }
}