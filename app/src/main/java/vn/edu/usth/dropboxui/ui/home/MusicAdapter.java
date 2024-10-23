
package vn.edu.usth.dropboxui.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.usth.dropboxui.R;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private Context context;
    private List<String> musicUrls;

    public MusicAdapter(Context context, List<String> musicUrls) {
        this.context = context;
        this.musicUrls = musicUrls;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        String musicUrl = musicUrls.get(position);
        holder.musicTitle.setText(musicUrl); // Assuming musicUrl is the title, adjust as needed
    }

    @Override
    public int getItemCount() {
        return musicUrls.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView musicTitle;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.musicTitle);
        }
    }
}