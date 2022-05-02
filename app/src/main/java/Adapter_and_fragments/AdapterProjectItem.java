package Adapter_and_fragments;

import android.graphics.Bitmap;
import android.provider.MediaStore;
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

public class AdapterProjectItem extends RecyclerView.Adapter<AdapterProjectItem.ItemViewHolder>{

    List<Projects> arr;
    OnItemClickListener onItemClickListener;

    public AdapterProjectItem(List<Projects> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String imageUriText = null;


        Projects project = arr.get(position);
        holder.projName.setText(project.getProjName());
        holder.ratings.setText(project.getRatings());

        imageUriText = project.getImageUrl();
        Picasso.get().load(project.getImageUrl())
                .into(holder.projectImage);

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



    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView projName, ratings;
        ImageView projectImage;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            ratings = itemView.findViewById(R.id.tv_projItemRatings);

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
