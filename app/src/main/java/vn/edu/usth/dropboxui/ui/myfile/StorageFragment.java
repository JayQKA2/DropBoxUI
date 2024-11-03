package vn.edu.usth.dropboxui.ui.myfile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import vn.edu.usth.dropboxui.R;

public class StorageFragment extends Fragment {
    private long totalSize;
    private long imageSize;
    private long videoSize;
    private long audioSize;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_storage, container, false);

        totalSize = 0;
        imageSize = 0;
        videoSize = 0;
        audioSize = 0;

        updateUI(root);

        return root;
    }

    private void updateUI(View root) {
        TextView imagePercentageTextView = root.findViewById(R.id.image_percentage);
        TextView videoPercentageTextView = root.findViewById(R.id.video_percentage);
        TextView audioPercentageTextView = root.findViewById(R.id.audio_percentage);

        imagePercentageTextView.setText(String.format("%.2f%%", (imageSize * 100.0) / totalSize));
        videoPercentageTextView.setText(String.format("%.2f%%", (videoSize * 100.0) / totalSize));
        audioPercentageTextView.setText(String.format("%.2f%%", (audioSize * 100.0) / totalSize));
    }
}

//public class StorageFragment extends Fragment {
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.fragment_storage, container, false);
//    }
//}