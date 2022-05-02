package Adapter_and_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Listings;
import com.example.servicehub.R;
import com.example.servicehub.edit_listing_page;
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
 * Use the {@link fragment1Listings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment1Listings extends Fragment {

    private List<Listings> arr;
    private AdapterListingsItem adapterListingsItem;
    private RecyclerView recyclerViewListings;
    private DatabaseReference listDatabase;
    private FirebaseUser user;
    private String userID;
    private String listingtID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment1Listings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentListings.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment1Listings newInstance(String param1, String param2) {
        fragment1Listings fragment = new fragment1Listings();
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

        View rootView = inflater.inflate(R.layout.fragment_listings, container, false);

        recyclerViewListings = rootView.findViewById(R.id.recyclerViewListings);
        user = FirebaseAuth.getInstance().getCurrentUser();
        listDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        userID = user.getUid();
        recyclerViewListings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewListings.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterListingsItem = new AdapterListingsItem(arr);
        recyclerViewListings.setAdapter(adapterListingsItem);

        Query query = listDatabase
                .orderByChild("userID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Listings listings = dataSnapshot.getValue(Listings.class);
                    arr.add(listings);
                }

                adapterListingsItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterListingsItem.setOnItemClickListener(new AdapterListingsItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);

                Query query = FirebaseDatabase.getInstance().getReference("Listings")
                        .orderByChild("listName")
                        .equalTo(arr.get(position).getListName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {

                     for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                         listingtID = dataSnapshot.getKey().toString();
                         Intent intentListing = new Intent(getContext(), edit_listing_page.class);
                         intentListing.putExtra("Listing ID", listingtID);
                         startActivity(intentListing);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error)
                 {

                 }
             });

                adapterListingsItem.notifyItemChanged(position);

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}