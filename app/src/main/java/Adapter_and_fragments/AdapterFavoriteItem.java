package Adapter_and_fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Cart;
import com.example.servicehub.Favorites;
import com.example.servicehub.R;
import com.example.servicehub.cart_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterFavoriteItem extends RecyclerView.Adapter<AdapterFavoriteItem.ItemViewHolder> {

   private List<Favorites> arr;
   private OnItemClickListener onItemClickListener;

    public AdapterFavoriteItem(List<Favorites> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterFavoriteItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;
        String userID;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Favorites favorites = arr.get(position);
        holder.projName.setText(favorites.getProjName());
        holder.projPrice.setText(favorites.getProjPrice());
        holder.projRatings.setText(favorites.getProjRatings());

        imageUriText = favorites.getProjImageUrl();


        Picasso.get()
                .load(imageUriText)
                .into(holder.projectImage);

        holder.projDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference favDatabase = FirebaseDatabase.getInstance().getReference("Favorites");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove " + favorites.getProjName().toUpperCase(Locale.ROOT) + "?")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Query query = favDatabase.orderByChild("projName")
                                        .startAt(favorites.getProjName()).endAt(favorites.getProjName());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            Favorites c = dataSnapshot.getValue(Favorites.class);
                                            if( c.getCustID().equals(userID) )
                                            {
                                                dataSnapshot.getRef().removeValue();
                                                Toast.makeText(view.getContext(), "Service Removed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(view.getContext(), cart_page.class);
                                                view.getContext().startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        })
                        .setContentText("Remove this in the Favorites?")
                        .show();
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

        TextView projName, projRatings, projPrice;
        ImageView projectImage, projDeleteBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            projRatings = itemView.findViewById(R.id.tv_projRatings);
            projPrice = itemView.findViewById(R.id.tv_price);
            projDeleteBtn = itemView.findViewById(R.id.iv_deleteBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
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
