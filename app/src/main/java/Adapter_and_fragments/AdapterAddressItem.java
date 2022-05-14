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
import com.example.servicehub.MyAddress;
import com.example.servicehub.Notification;
import com.example.servicehub.R;
import com.example.servicehub.address_page;
import com.example.servicehub.notification_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterAddressItem extends RecyclerView.Adapter<AdapterAddressItem.ItemViewHolder>{

    private List<MyAddress> arr;

    public AdapterAddressItem() {
    }

    public AdapterAddressItem(List<MyAddress> arr) {
        this.arr = arr;
    }

    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterAddressItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_addresses,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String userID;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        MyAddress myAddress = arr.get(position);
        String addressLabel = "Address " + String.valueOf(position + 1);
        holder.tv_addrssLabel.setText(addressLabel);
        holder.tv_addressValue.setText(myAddress.getAddressValue());

        holder.iv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference addressDatabase = FirebaseDatabase.getInstance().getReference("Address");

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Remove this Address?")
                        .setCancelText("Back")
                        .setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Query query = addressDatabase.orderByChild("addressValue")
                                        .startAt(myAddress.getAddressValue()).endAt(myAddress.getAddressValue());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {
                                            MyAddress c = dataSnapshot.getValue(MyAddress.class);
                                            if( c.getCustID().equals(userID) )
                                            {
                                                dataSnapshot.getRef().removeValue();
                                                Toast.makeText(view.getContext(), "Address Removed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(view.getContext(), address_page.class);
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
                        .setContentText(myAddress.getAddressValue())
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

        TextView tv_addrssLabel, tv_addressValue;
        ImageView iv_deleteBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_addrssLabel = itemView.findViewById(R.id.tv_addrssLabel);
            tv_addressValue = itemView.findViewById(R.id.tv_addressValue);
            iv_deleteBtn = itemView.findViewById(R.id.iv_deleteBtn);

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
