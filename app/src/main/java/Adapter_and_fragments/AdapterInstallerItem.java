package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterInstallerItem extends RecyclerView.Adapter<AdapterInstallerItem.ItemViewHolder> {

    List<Projects> arr;
    AdapterProjectItem.OnItemClickListener onItemClickListener;

    public AdapterInstallerItem(List<Projects> arr) {
        this.arr = arr;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installer,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        String imageUriText = null;

        Projects project = arr.get(position);
        holder.projName.setText(project.getProjName());
        holder.projRatings.setText(project.getRatings());
        holder.projPrice.setText(project.getPrice());

        imageUriText = project.getImageUrl();

        Picasso.get().load(imageUriText)
                .into(holder.projectImage);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AdapterProjectItem.OnItemClickListener listener){
        onItemClickListener = listener;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView projName, projRatings, projPrice;
        ImageView projectImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            projRatings = itemView.findViewById(R.id.tv_projItemRatings);
            projPrice = itemView.findViewById(R.id.tv_price);

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
