package vn.edu.usth.dropboxui.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.usth.dropboxui.model.Metadata;
import vn.edu.usth.dropboxui.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private Context context;
    private List<Metadata> fileMetadataList;

    public FileAdapter(Context context, List<Metadata> fileMetadataList) {
        this.context = context;
        this.fileMetadataList = fileMetadataList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Metadata metadata = fileMetadataList.get(position);
        holder.fileName.setText(metadata.getName());
    }

    @Override
    public int getItemCount() {
        return fileMetadataList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}