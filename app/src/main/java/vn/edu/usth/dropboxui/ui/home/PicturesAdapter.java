package vn.edu.usth.dropboxui.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;
import vn.edu.usth.dropboxui.model.PictureModel;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PictureViewHolder> {
    private Context context;
    private ArrayList<PictureModel> pictures;

    public PicturesAdapter(Context context, List<Metadata> pictureUrls) {
        this.context = context;
        this.pictures = new ArrayList<>();
    }

    public void setPictures(ArrayList<PictureModel> pictures) {
        this.pictures.clear();
        for (PictureModel model : pictures) {
            if (isImageUrl(model.getUrl())) {
                this.pictures.add(model);
            }
        }
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.picture_cardview, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, int position) {
        int startIndex = position * 4;
        int endIndex = Math.min(startIndex + 4, pictures.size());
        ArrayList<PictureModel> images = new ArrayList<>(pictures.subList(startIndex, endIndex));

        ImageView[] imageViews = {holder.picture1, holder.picture2, holder.picture3, holder.picture4};
        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            if (i < images.size()) {
                imageViews[index].setVisibility(View.VISIBLE);
                Picasso.get().load(images.get(index).getUrl()).into(imageViews[index]);
                imageViews[index].setOnClickListener(v -> showFullScreenImageDialog(images.get(index).getUrl()));
            } else {
                imageViews[index].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil(pictures.size() / 4.0);
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView picture1, picture2, picture3, picture4;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            picture1 = itemView.findViewById(R.id.imageView1);
            picture2 = itemView.findViewById(R.id.imageView2);
            picture3 = itemView.findViewById(R.id.imageView3);
            picture4 = itemView.findViewById(R.id.imageView4);
        }
    }

    private boolean isImageUrl(String url) {
        return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") ||
                url.endsWith(".gif") || url.endsWith(".bmp") || url.endsWith(".webp");
    }

    private void showFullScreenImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.fullscreen_image_view);

        PhotoView photoView = dialog.findViewById(R.id.fullScreenImageView);
        Picasso.get().load(imageUrl).into(photoView);

        photoView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}