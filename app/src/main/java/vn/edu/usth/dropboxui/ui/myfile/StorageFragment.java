package vn.edu.usth.dropboxui.ui.myfile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import vn.edu.usth.dropboxui.R;

public class StorageFragment extends Fragment {
    private static final long TOTAL_STORAGE = 64L * 1024L * 1024L * 1024L;

    private long totalSize;
    private long imageSize;
    private long videoSize;
    private long audioSize;
    private long otherSize;
    private int fileCount;
    private int imageCount;
    private int videoCount;
    private int audioCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        updateUI(view);
        return view;
    }

    private void updateUI(View root) {
        TextView storageUsedTextView = root.findViewById(R.id.storage_used);
        TextView storageTotalTextView = root.findViewById(R.id.storage_total);
        TextView filePercentageTextView = root.findViewById(R.id.file_percentage);
        TextView imagePercentageTextView = root.findViewById(R.id.image_percentage);
        TextView videoPercentageTextView = root.findViewById(R.id.video_percentage);
        TextView audioPercentageTextView = root.findViewById(R.id.audio_percentage);
        ProgressBar circularProgressBar = root.findViewById(R.id.circular_progress);

        totalSize = 46L * 1024L * 1024L * 1024L;
        imageSize = 6L * 1024L * 1024L * 1024L;
        videoSize = 8L * 1024L * 1024L * 1024L;
        audioSize = 5L * 1024L * 1024L * 1024L;
        otherSize = totalSize - (imageSize + videoSize + audioSize);

        storageUsedTextView.setText(formatSize(totalSize));
        storageTotalTextView.setText(formatSize(TOTAL_STORAGE));
        filePercentageTextView.setText(String.format("%.2f%%", (otherSize * 100.0) / totalSize));
        imagePercentageTextView.setText(String.format("%.2f%%", (imageSize * 100.0) / totalSize));
        videoPercentageTextView.setText(String.format("%.2f%%", (videoSize * 100.0) / totalSize));
        audioPercentageTextView.setText(String.format("%.2f%%", (audioSize * 100.0) / totalSize));
        circularProgressBar.setProgress((int) ((totalSize * 100) / TOTAL_STORAGE));
    }

    private String formatSize(long size) {
        if (size >= 1024 * 1024 * 1024) {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        } else if (size >= 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else if (size >= 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%d B", size);
        }
    }
}