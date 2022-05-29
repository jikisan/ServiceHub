package Adapter_and_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.servicehub.Photos;
import com.example.servicehub.R;
import com.example.servicehub.photo_fullscreen_view_page;
import com.google.firebase.database.ChildEventListener;
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
 * Use the {@link fragment1Photos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment1Photos extends Fragment {

    private List<Photos> arrUrl = new ArrayList<Photos>();
    private AdapterPhotoItem adapterPhotoItem;
    private String projectIdFromIntent;
    private DatabaseReference photoDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment1Photos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment1Photos.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment1Photos newInstance(String param1, String param2) {
        fragment1Photos fragment = new fragment1Photos();
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

        View view = inflater.inflate(R.layout.fragment1photos, container, false);

        projectIdFromIntent = getActivity().getIntent().getStringExtra("project ID");
        photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        RecyclerView rv_photos = view.findViewById(R.id.rv_photos);
        rv_photos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        rv_photos.setLayoutManager(gridLayoutManager);

        adapterPhotoItem = new AdapterPhotoItem(arrUrl);
        rv_photos.setAdapter(adapterPhotoItem);

        adapterPhotoItem.notifyDataSetChanged();

        getViewHolderValues(rv_photos);

        adapterPhotoItem.setOnItemClickListener(new AdapterPhotoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position)
            {
                Intent intent = new Intent(getContext(), photo_fullscreen_view_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                intent.putExtra("current position", position);
                intent.putExtra("category", "viewer");
                startActivity(intent);
            }
        });

        return view;
    }
    private void getViewHolderValues(RecyclerView rv_photos) {


            Query query1 = photoDatabase
                .orderByChild("projID")
                .equalTo(projectIdFromIntent);

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Photos photos = dataSnapshot.getValue(Photos.class);
                        arrUrl.add(photos);
                    }

                    adapterPhotoItem.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

}