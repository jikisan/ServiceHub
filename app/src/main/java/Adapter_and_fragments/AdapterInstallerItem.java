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

public class AdapterInstallerItem extends RecyclerView.Adapter<AdapterInstallerItem.ItemViewHolder> {

    List<Projects> arr;
    OnItemClickListener onItemClickListener;

    public AdapterInstallerItem() {
    }

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
        holder.projPrice.setText(project.getPrice());
        holder.tv_userRating.setText("("+project.getRatingCount()+")");
        holder.rb_userRating.setRating((float) project.getRatingAverage());

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

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView projName, projRatings, projPrice, tv_userRating;
        ImageView projectImage;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            projectImage = itemView.findViewById(R.id.iv_projectPhoto);
            projName = itemView.findViewById(R.id.tv_projName);
            projRatings = itemView.findViewById(R.id.tv_projRatings);
            projPrice = itemView.findViewById(R.id.tv_price);
            tv_userRating = itemView.findViewById(R.id.tv_userRating);
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
