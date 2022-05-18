package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.Ratings;
import com.example.servicehub.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRatingsItem extends RecyclerView.Adapter<AdapterRatingsItem.ItemViewHolder> {

    private List<Ratings> arr;
    private OnItemClickListener onItemClickListener;
    private String imageUrl;

    public AdapterRatingsItem() {
    }

    public AdapterRatingsItem(List<Ratings> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterRatingsItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratings,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Ratings ratings = arr.get(position);

        String raterID = ratings.getRatingById();
        holder.tv_ratingNameAdapter.setText(ratings.getRatingByName());
        holder.tv_ratingMessageAdapter.setText(ratings.getRatingMessage());
        holder.ratingBarAdapter.setRating((float) ratings.getRatingValue());

        generateRaterData(raterID, holder);



    }

    private void generateRaterData(String raterID, ItemViewHolder holder) {
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        userDatabase.child(raterID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    imageUrl = users.getImageUrl();

                    Picasso.get().load(imageUrl)
                            .into(holder.iv_raterPhoto);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_raterPhoto;
        TextView tv_ratingNameAdapter, tv_ratingMessageAdapter;
        RatingBar ratingBarAdapter;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_raterPhoto = itemView.findViewById(R.id.iv_raterPhoto);
            tv_ratingNameAdapter = itemView.findViewById(R.id.tv_ratingNameAdapter);
            tv_ratingMessageAdapter = itemView.findViewById(R.id.tv_ratingMessageAdapter);
            ratingBarAdapter = itemView.findViewById(R.id.ratingBarAdapter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }

                }
            });
        }
    }
}
