package vn.edu.usth.dropboxui.ui.myfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.usth.dropboxui.DropboxApi;
import vn.edu.usth.dropboxui.ListFloder.ListFolderRequest;
import vn.edu.usth.dropboxui.ListFloder.ListFolderResult;
import vn.edu.usth.dropboxui.ListFloder.Metadata;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.RetrofitClient;

public class MyFileFragment extends Fragment {
    private static final String BASE_URL = "https://api.dropboxapi.com/2/";
    private static final String ACCESS_TOKEN = "Bearer sl.B_S-TZWlzDCVuyqAT0yjFxM_bcqoo59gippdQC8TEjVcVyiD_KggHLflTucAkuKGrjucjLe0EMH7gidyyYbGNrbj-ryk6MIIz86j1-ZLBmBgH5_x5O1Sbr2xz8dYidAiSOlqqAIAGNtRqncBCJoNDlY";

    private long totalSize = 0;
    private long imageSize = 0;
    private long videoSize = 0;
    private long audioSize = 0;
    private long otherSize = 0;
    private int fileCount = 0;
    private int imageCount = 0;
    private int videoCount = 0;
    private int audioCount = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_file, container, false);

        Button seeDetailsButton = root.findViewById(R.id.see_details);
        seeDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.storageFragment);
            }
        });

        calculateTotalSize();

        return root;
    }

    private void calculateTotalSize() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        ListFolderRequest request = new ListFolderRequest("");
        Call<ListFolderResult> call = dropboxApi.listFolder(ACCESS_TOKEN, request);
        call.enqueue(new Callback<ListFolderResult>() {
            @Override
            public void onResponse(Call<ListFolderResult> call, Response<ListFolderResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Metadata> entries = response.body().getEntries();
                    for (Metadata metadata : entries) {
                        long size = metadata.getSize();
                        totalSize += size;
                        String name = metadata.getName().toLowerCase();
                        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
                            imageSize += size;
                        } else if (name.endsWith(".mp4") || name.endsWith(".mkv")) {
                            videoSize += size;
                        } else if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                            audioSize += size;
                        } else {
                            otherSize += size;
                        }
                    }
                    updateUI();
                } else {
                    Log.e("MyFileFragment", "Failed to load files: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ListFolderResult> call, Throwable t) {
                Log.e("MyFileFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        TextView storageCapacityTextView = getView().findViewById(R.id.storage_capacity);
        storageCapacityTextView.setText(formatSize(totalSize) + "/64GB");

        ProgressBar storageProgressBar = getView().findViewById(R.id.storage_progress);
        storageProgressBar.setProgress((int) ((totalSize * 100) / (64 * 1024 * 1024 * 1024L)));

        TextView fileCountTextView = getView().findViewById(R.id.file_count);
        TextView imageCountTextView = getView().findViewById(R.id.image_count);
        TextView videoCountTextView = getView().findViewById(R.id.video_count);
        TextView audioCountTextView = getView().findViewById(R.id.audio_count);

        fileCountTextView.setText(String.valueOf(fileCount));
        imageCountTextView.setText(String.valueOf(imageCount));
        videoCountTextView.setText(String.valueOf(videoCount));
        audioCountTextView.setText(String.valueOf(audioCount));
    }

    private String formatSize(long size) {
        if (size >= 1024 * 1024 * 1024) {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        } else if (size >= 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else if (size >= 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return size + " B";
        }
    }
}
//package vn.edu.usth.dropboxui.ui.myfile;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.navigation.Navigation;
//import vn.edu.usth.dropboxui.R;
//
//public class MyFileFragment extends Fragment {
//
//
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_my_file, container, false);
//
//        Button seeDetailsButton = root.findViewById(R.id.see_details);
//        seeDetailsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.storageFragment);
//            }
//        });
//
//        return root;
//    }
//}