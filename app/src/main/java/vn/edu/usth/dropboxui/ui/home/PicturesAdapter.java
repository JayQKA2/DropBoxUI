package vn.edu.usth.dropboxui.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PictureViewHolder> {
    private Context context;
    private List<Metadata> pictureUrls;

    public PicturesAdapter(Context context, List<Metadata> pictureUrls) {
        this.context = context;
        this.pictureUrls = pictureUrls;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        Metadata metadata = pictureUrls.get(position);
        Picasso.get().load(metadata.getPathDisplay()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return pictureUrls.size();
    }

    public static class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}