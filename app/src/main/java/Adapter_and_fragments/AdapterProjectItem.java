package Adapter_and_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicehub.Projects;
import com.example.servicehub.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProjectItem extends RecyclerView.Adapter<AdapterProjectItem.ItemViewHolder>{

    private List<Projects> arr;
    private OnItemClickListener onItemClickListener;

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
        holder.tv_userRatingCount.setText("(" + project.getRatingCount() + ")");
        holder.rb_userRating.setRating((float) project.getRatingAverage());

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

        TextView projName, tv_userRatingCount;
        ImageView projectImage;
        RatingBar rb_userRating;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            tv_userRatingCount = itemView.findViewById(R.id.tv_userRatingCount);
            rb_userRating = itemView.findViewById(R.id.rb_userRating);

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
