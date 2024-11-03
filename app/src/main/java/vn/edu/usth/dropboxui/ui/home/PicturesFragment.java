package vn.edu.usth.dropboxui.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.dropboxui.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;

public class PicturesFragment extends Fragment {
    private static final String TAG = "PictureFragment";
    private RecyclerView recyclerView;
    private PicturesAdapter adapter;
    private List<Metadata> pictureUrls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pictureUrls = new ArrayList<>();
        adapter = new PicturesAdapter(getContext(), pictureUrls);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.uploadButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        fetchPictures();

        return view;
    }

    private void fetchPictures() {
        // Implement the method to fetch picture URLs from Dropbox and update the adapter
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Upload the image to Dropbox
                uploadFileToDropbox(selectedImageUri);
            }
        }
    }

    private void uploadFileToDropbox(Uri fileUri) {
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        new Thread(() -> {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    Log.e(TAG, "Input stream is null");
                    return;
                }

                DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
                DbxClientV2 client = new DbxClientV2(config, accessToken);

                String fileName = "uploaded_image.jpg"; // You can generate a unique file name here
                FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
                        .uploadAndFinish(inputStream);

                Log.d(TAG, "File uploaded: " + metadata.getPathLower());
                // Add the new image URL to the list and update the adapter
                pictureUrls.add(new Metadata(fileName, metadata.getPathLower()));
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file: " + e.getMessage());
            }
        }).start();
    }
}