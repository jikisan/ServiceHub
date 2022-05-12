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

import com.example.servicehub.Orders;
import com.example.servicehub.R;
import com.example.servicehub.order_details_page;
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
 * Use the {@link fragment2Order#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment2Order extends Fragment {

    private List<Orders> arr;
    private AdapterOrderItem adapterOrderItem;
    private String userID;
    private String orderID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment2Order() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2Order.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment2Order newInstance(String param1, String param2) {
        fragment2Order fragment = new fragment2Order();
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

        View view = inflater.inflate(R.layout.fragment_fragment2_order, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        userID = user.getUid();

        RecyclerView recyclerViewOrders = view.findViewById(R.id.recyclerViewOrder);
        recyclerViewOrders.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrders.setLayoutManager(linearLayoutManager);

        arr = new ArrayList<>();
        adapterOrderItem = new AdapterOrderItem(arr);
        recyclerViewOrders.setAdapter(adapterOrderItem);

        Query query = orderDatabase
                .orderByChild("sellerID")
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Orders orders = dataSnapshot.getValue(Orders.class);
                    arr.add(orders);

                }

                adapterOrderItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterOrderItem.setOnItemClickListener(new AdapterOrderItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                arr.get(position);

                Query query = orderDatabase
                        .orderByChild("itemName")
                        .equalTo(arr.get(position).getItemName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            orderID = dataSnapshot.getKey().toString();
                            Intent intentListing = new Intent(getContext(), order_details_page.class);
                            intentListing.putExtra("Order ID", orderID);
                            startActivity(intentListing);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });

                adapterOrderItem.notifyItemChanged(position);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}