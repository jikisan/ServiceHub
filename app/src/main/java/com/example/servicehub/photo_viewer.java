package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter_and_fragments.AdapterPhotoItem;
import Adapter_and_fragments.fragmentAdapter;
import Adapter_and_fragments.fragmentAdapterPhotos;

public class photo_viewer extends AppCompatActivity {

    private TextView tv_back;

    private TabLayout tabLayout;
    private ViewPager2 vp_viewPager2;

    private AdapterPhotoItem adapterPhotoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer);

        String projectIdFromIntent = getIntent().getStringExtra("project ID");
        DatabaseReference photoDatabase = FirebaseDatabase.getInstance().getReference("Photos");

        setRef();
        generateTabLayout();
        clickListeners();
    }

    private void setRef() {

        tv_back = findViewById(R.id.tv_back);
        tabLayout = findViewById(R.id.tab_layout);
        vp_viewPager2 = findViewById(R.id.vp_viewPager2);

    }

    private void generateTabLayout() {

        tabLayout.addTab(tabLayout.newTab().setText("Photos"));
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Adapter_and_fragments.fragmentAdapterPhotos fragmentAdapterPhotos = new fragmentAdapterPhotos(fragmentManager, getLifecycle());
        vp_viewPager2.setAdapter(fragmentAdapterPhotos);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void clickListeners() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });



    }
}