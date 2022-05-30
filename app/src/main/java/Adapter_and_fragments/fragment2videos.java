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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.servicehub.Photos;
import com.example.servicehub.R;
import com.example.servicehub.Videos;
import com.example.servicehub.add_videos;
import com.example.servicehub.video_view_page;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment2videos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment2videos extends Fragment {

    private List<Videos> arrUrl = new ArrayList<Videos>();
    private AdapterVideoItem adapterVideoItem;
    private String projectIdFromIntent;
    private DatabaseReference videoDatabase;
    private ProgressBar progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment2videos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2videos.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment2videos newInstance(String param1, String param2) {
        fragment2videos fragment = new fragment2videos();
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment2videos, container, false);



        projectIdFromIntent = getActivity().getIntent().getStringExtra("project ID");
        videoDatabase = FirebaseDatabase.getInstance().getReference("Videos");

        RecyclerView rv_videos = view.findViewById(R.id.rv_videos);
        progressBar = view.findViewById(R.id.progressBar);
        TextView empty_view = view.findViewById(R.id.empty_view);

        rv_videos.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        rv_videos.setLayoutManager(gridLayoutManager);

        adapterVideoItem = new AdapterVideoItem(arrUrl, getContext());
        rv_videos.setAdapter(adapterVideoItem);

        adapterVideoItem.notifyDataSetChanged();

        getViewHolderValues(rv_videos);

        adapterVideoItem.setOnItemClickListener(new AdapterVideoItem.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(getContext(), video_view_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                intent.putExtra("current position", position);
                intent.putExtra("category", "add");
                startActivity(intent);
            }
        });

        return view;
    }

    private void getViewHolderValues(RecyclerView rv_videos) {

        Query query = videoDatabase
                .orderByChild("projID")
                .equalTo(projectIdFromIntent);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.exists())
                {
                    Videos videos = snapshot.getValue(Videos.class);
                    arrUrl.add(videos);
                    rv_videos.setAdapter(adapterVideoItem);
                    adapterVideoItem.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                rv_videos.setAdapter(adapterVideoItem);
                adapterVideoItem.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}