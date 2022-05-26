package Adapter_and_fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Booking;
import com.example.servicehub.Cart;
import com.example.servicehub.Listings;
import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.example.servicehub.booking_page;
import com.example.servicehub.cart_page;
import com.example.servicehub.place_order_page;
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

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.ItemViewHolder> {

    private List<Cart> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterCartItem() {
    }

    public AdapterCartItem(List<Cart> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterCartItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;
        String userID;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        Cart cart = arr.get(position);
        holder.listName.setText(cart.getListName());
        holder.listPrice.setText(cart.getListPrice());
        holder.listRatings.setText(cart.getListRatings());

        imageUriText = cart.getListImageUrl();


        Picasso.get()
                .load(imageUriText)
                .into(holder.listImage);

        holder.listDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove " + cart.getListName().toUpperCase(Locale.ROOT) + "?")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Query query = cartDatabase.orderByChild("listName")
                                        .startAt(cart.getListName()).endAt(cart.getListName());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            Cart c = dataSnapshot.getValue(Cart.class);
                                            if( c.getCustID().equals(userID) )
                                            {
                                                dataSnapshot.getRef().removeValue();
                                                Toast.makeText(view.getContext(), "Item Removed", Toast.LENGTH_SHORT).show();
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
                        .setContentText("Remove this in the cart?")
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

        TextView listName, listPrice, listRatings;
        ImageView listImage, listDeleteBtn;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage = itemView.findViewById(R.id.iv_listingImage);
            listName = itemView.findViewById(R.id.tv_itemName);
            listPrice = itemView.findViewById(R.id.tv_price);
            listRatings = itemView.findViewById(R.id.tv_listingRatings);
            listDeleteBtn = itemView.findViewById(R.id.iv_deleteBtn);

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
