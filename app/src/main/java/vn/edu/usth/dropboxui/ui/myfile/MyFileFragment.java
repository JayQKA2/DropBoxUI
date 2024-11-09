package vn.edu.usth.dropboxui.ui.myfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.usth.dropboxui.API.DropboxApi;
import vn.edu.usth.dropboxui.ListFloder.ListFolderRequest;
import vn.edu.usth.dropboxui.ListFloder.ListFolderResult;
import vn.edu.usth.dropboxui.ListFloder.Metadata;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.RetrofitClient;

public class MyFileFragment extends Fragment {
    private static final String BASE_URL = "https://api.dropboxapi.com/2/";
    private static final long TOTAL_STORAGE = 64L * 1024L * 1024L * 1024L; // 64GB in bytes

    private static long totalSize = 0;
    private static long imageSize = 0;
    private static long videoSize = 0;
    private static long audioSize = 0;
    private long otherSize = 0;
    private int fileCount = 0;
    private int imageCount = 0;
    private int videoCount = 0;
    private int audioCount = 0;

    public static long getTotalSize() {
        return totalSize;
    }

    public static long getImageSize() {
        return imageSize;
    }

    public static long getVideoSize() {
        return videoSize;
    }

    public static long getAudioSize() {
        return audioSize;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_file, container, false);

        Button seeDetailsButton = root.findViewById(R.id.see_details);
        seeDetailsButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("totalSize", totalSize);
            bundle.putLong("imageSize", imageSize);
            bundle.putLong("videoSize", videoSize);
            bundle.putLong("audioSize", audioSize);
            bundle.putLong("otherSize", otherSize);
            bundle.putInt("fileCount", fileCount);
            bundle.putInt("imageCount", imageCount);
            bundle.putInt("videoCount", videoCount);
            bundle.putInt("audioCount", audioCount);
            Navigation.findNavController(v).navigate(R.id.storageFragment, bundle);
        });

        calculateTotalSize();

        return root;
    }

    private void calculateTotalSize() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        ListFolderRequest request = new ListFolderRequest("");
        Call<ListFolderResult> call = dropboxApi.listFolder(getAccessToken(), request);
        call.enqueue(new Callback<ListFolderResult>() {
            @Override
            public void onResponse(Call<ListFolderResult> call, Response<ListFolderResult> response) {
                if (response.isSuccessful()) {
                    ListFolderResult result = response.body();
                    if (result != null) {
                        for (Metadata metadata : result.getEntries()) {
                            String name = metadata.getName();
                            long size = metadata.getSize();
                            totalSize += size;
                            fileCount++;
                            if (isImageFile(name)) {
                                imageSize += size;
                                imageCount++;
                            } else if (isVideoFile(name)) {
                                videoSize += size;
                                videoCount++;
                            } else if (isAudioFile(name)) {
                                audioSize += size;
                                audioCount++;
                            } else {
                                otherSize += size;
                            }
                        }
                        updateUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<ListFolderResult> call, Throwable t) {
                Log.e("MyFileFragment", "Error fetching data", t);
            }
        });
    }

    private boolean isImageFile(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        return lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".png") ||
                lowerCaseName.endsWith(".gif") || lowerCaseName.endsWith(".bmp") || lowerCaseName.endsWith(".webp") ||
                lowerCaseName.endsWith(".tiff") || lowerCaseName.endsWith(".svg") || lowerCaseName.endsWith(".heic");
    }

    private boolean isVideoFile(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        return lowerCaseName.endsWith(".mp4") || lowerCaseName.endsWith(".mkv") || lowerCaseName.endsWith(".avi") ||
                lowerCaseName.endsWith(".mov") || lowerCaseName.endsWith(".wmv") || lowerCaseName.endsWith(".flv") ||
                lowerCaseName.endsWith(".webm") || lowerCaseName.endsWith(".mpeg");
    }

    private boolean isAudioFile(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        return lowerCaseName.endsWith(".mp3") || lowerCaseName.endsWith(".wav") || lowerCaseName.endsWith(".flac") ||
                lowerCaseName.endsWith(".aac") || lowerCaseName.endsWith(".ogg") || lowerCaseName.endsWith(".m4a");
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private void updateUI() {
        TextView storageCapacityTextView = getView().findViewById(R.id.storage_capacity);
        storageCapacityTextView.setText(formatSize(totalSize) + "/64GB");

        ProgressBar storageProgressBar = getView().findViewById(R.id.storage_progress);
        storageProgressBar.setProgress((int) ((totalSize * 100) / TOTAL_STORAGE));

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
            return String.format("%d B", size);
        }
    }
}