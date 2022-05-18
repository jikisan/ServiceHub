package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import Adapter_and_fragments.AdapterCartItem;
import Adapter_and_fragments.AdapterInstallerItem;
import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class search_page extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private TextView tv_back, tv_headerTitle;
    private SearchView sv_search;
    private RecyclerView recyclerView_searches;
    private ProgressBar progressBar;
    private AutoCompleteTextView auto_complete_txt;
    private String listOfCategory = "";
    private String[] categoryArrayFix = {"All services", "Installation","Repair","Cleaning","Heating","Ventilation","Others"};

    private ArrayAdapter<CharSequence> adapterCategoryItems;
    private AdapterInstallerItem adapterInstallerItem, adapter;
    private DatabaseReference projDatabase;
    private ArrayList<Projects> arrProj, arr;
    private ArrayList<String> arrCategory;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        projDatabase = FirebaseDatabase.getInstance().getReference("Projects");

        setRef();
        generateRecyclerLayout();
        generateListOfCategory();
        generateDataValue();
        clickListeners();
        bottomNavTaskbar();
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


    }

    private void generateRecyclerLayout() {

        recyclerView_searches.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_searches.setLayoutManager(linearLayoutManager);

        adapterInstallerItem = new AdapterInstallerItem(arrProj);
        adapter = new AdapterInstallerItem(arr);
        recyclerView_searches.setAdapter(adapterInstallerItem);

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
                        arrProj.add(projects);

                    }
                    adapterInstallerItem.notifyDataSetChanged();
                }


//                AdapterInstallerItem adapter = new AdapterInstallerItem(arrProj);
//                recyclerView_searches.setAdapter(adapter);


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
                        arrProj.add(projects);
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

    private void dropDownMenuTextView() {

        String[] categoryArray = listOfCategory.split(",");
        adapterCategoryItems = new ArrayAdapter<CharSequence>(search_page.this, R.layout.list_property, categoryArrayFix);
        auto_complete_txt.setAdapter(adapterCategoryItems);
    }

    private void search(String s) {
        arr = new ArrayList<>();
        for(Projects object : arrProj)
        {
            if(object.getProjName().toLowerCase().contains(s.toLowerCase()))
            {
                arr.add(object);
            }

            adapter = new AdapterInstallerItem(arr);
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

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        tv_back = findViewById(R.id.tv_back);
        tv_headerTitle = findViewById(R.id.tv_headerTitle);

        sv_search = findViewById(R.id.sv_search);

        recyclerView_searches = findViewById(R.id.recyclerView_searches);

        progressBar = findViewById(R.id.progressBar);

        auto_complete_txt = findViewById(R.id.auto_complete_txt);

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

}