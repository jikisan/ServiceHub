package Adapter_and_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.servicehub.Photos;
import com.example.servicehub.R;
import com.example.servicehub.add_photos;
import com.example.servicehub.edit_project_page;
import com.example.servicehub.intro_logo;
import com.example.servicehub.more_page;
import com.example.servicehub.photo_fullscreen_view_page;
import com.example.servicehub.photo_viewer;
import com.example.servicehub.tech_dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterViewPagerPhotoFullscreen extends PagerAdapter {

    private Context context;
    private List<Photos> arrUrl = new ArrayList<Photos>();
    private ProgressDialog progressDialog;
    private int currentPosition;
    private String category;


    LayoutInflater mLayoutInflater;

    public AdapterViewPagerPhotoFullscreen() {
    }

    public AdapterViewPagerPhotoFullscreen(Context context, List<Photos> arrUrl, int currentPosition, String category) {
        this.context = context;
        this.arrUrl = arrUrl;
        this.currentPosition = currentPosition;
        this.category = category;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_photo_fullscreen, container, false);


        // referencing the image view from the item.xml file
        ImageView iv_fullScreenPhoto = (ImageView) itemView.findViewById(R.id.iv_fullScreenPhoto);

        // setting the image in the imageView

        Photos photos = arrUrl.get(position);

        String projID = photos.getProjID();
        String imageUrl = photos.getLink();
        String imageName = photos.getPhotoName();


        Picasso.get()
                .load(imageUrl)
                .into(iv_fullScreenPhoto);


        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
