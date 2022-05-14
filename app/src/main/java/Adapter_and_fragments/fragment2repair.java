package Adapter_and_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.example.servicehub.edit_project_page;
import com.example.servicehub.view_in_map;
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
import java.util.List;

public class fragment2repair extends Fragment {

    private List<Projects> arrProjects;
    private AdapterInstallerItem adapterInstallerItem;
    private String userID, projectID, projCategory;
    private FirebaseUser user;
    private DatabaseReference projDatabase, marketDatabase;
    private ImageView iv_sort, iv_Location;
    private TextView tv_category;

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



        View view = inflater.inflate(R.layout.fragment1_installer, container, false);

        iv_sort = (ImageView) view.findViewById(R.id.iv_sort);
        iv_Location = (ImageView) view.findViewById(R.id.iv_Location);
        tv_category = (TextView) view.findViewById(R.id.tv_category);
        projCategory = "Repair";

        user = FirebaseAuth.getInstance().getCurrentUser();
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");
        marketDatabase = FirebaseDatabase.getInstance().getReference("Listings");
        userID = user.getUid();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewInstallers);
        recyclerView.setHasFixedSize(true);


            arrProjects = new ArrayList<>();
            adapterInstallerItem = new AdapterInstallerItem(arrProjects);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapterInstallerItem);

            onClickToGetKeyProj();
            getProjByCategory();


        clickListeners();
        // Inflate the layout for this fragment
        return view;
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

        iv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sort Clicked 2", Toast.LENGTH_SHORT).show();
            }
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


}