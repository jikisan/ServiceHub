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

import com.example.servicehub.Booking;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.tech_booking_details;
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
 * Use the {@link fragment2Booking#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment2Booking extends Fragment {

    private List<Booking> arr;
    private AdapterBookingItem adapterBookingItem;
    private String userID;
    private String bookingID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment2Booking() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment2Booking newInstance(String param1, String param2) {
        fragment2Booking fragment = new fragment2Booking();
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

        View view = inflater.inflate(R.layout.fragment2booking, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference bookingDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        userID = user.getUid();

        RecyclerView recyclerViewBookings = view.findViewById(R.id.recyclerViewBooking);
        recyclerViewBookings.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewBookings.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterBookingItem = new AdapterBookingItem(arr);
        recyclerViewBookings.setAdapter(adapterBookingItem);

        Query query = bookingDatabase
                .orderByChild("techID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Booking bookings = dataSnapshot.getValue(Booking.class);
                    arr.add(bookings);
                }

                adapterBookingItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterBookingItem.setOnItemClickListener(new AdapterBookingItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);

                Query query = bookingDatabase
                        .orderByChild("projName")
                        .equalTo(arr.get(position).getProjName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            bookingID = dataSnapshot.getKey().toString();
                            Intent intentProject = new Intent(getContext(), tech_booking_details.class);
                            intentProject.putExtra("Booking ID", bookingID);
                            startActivity(intentProject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                adapterBookingItem.notifyItemChanged(position);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}