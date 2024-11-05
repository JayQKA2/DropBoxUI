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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.dropboxui.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;
import vn.edu.usth.dropboxui.model.PicturesViewModel;

public class PicturesFragment extends Fragment {
    private static final String TAG = "PictureFragment";
    private RecyclerView recyclerView;
    private PicturesAdapter adapter;
    private List<Metadata> pictureUrls;
    private PicturesViewModel picturesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        String accessToken = ApiConfig.getAccessToken();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        recyclerView = view.findViewById(R.id.PicturesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pictureUrls = new ArrayList<>();
        adapter = new PicturesAdapter(getContext(), pictureUrls);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = view.findViewById(R.id.uploadButton5);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        picturesViewModel = new ViewModelProvider(this).get(PicturesViewModel.class);
        picturesViewModel.getPicturesLiveData().observe(getViewLifecycleOwner(), pictures -> {
            adapter.setPictures(pictures);
            if (pictures.isEmpty()) {
                view.findViewById(R.id.dont_have_pictures).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.dont_have_pictures).setVisibility(View.INVISIBLE);
            }
            adapter.notifyDataSetChanged();
        });
        picturesViewModel.fetchPictures(accessToken);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            picturesViewModel.fetchPictures(accessToken);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchPictures();

        return view;
    }

    private void fetchPictures() {
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        new Thread(() -> {
            try {
                DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
                DbxClientV2 client = new DbxClientV2(config, accessToken);

                List<com.dropbox.core.v2.files.Metadata> metadataList = client.files().listFolder("").getEntries();
                List<Metadata> pictureMetadata = new ArrayList<>();
                for (com.dropbox.core.v2.files.Metadata metadata : metadataList) {
                    if (metadata instanceof FileMetadata) {
                        String pathLower = ((FileMetadata) metadata).getPathLower();
                        if (pathLower.endsWith(".jpg") || pathLower.endsWith(".jpeg") || pathLower.endsWith(".png")) {
                            pictureMetadata.add(new Metadata(metadata.getName(), pathLower, "image/jpeg", false));
                        }
                    }
                }

                getActivity().runOnUiThread(() -> {
                    pictureUrls.clear();
                    pictureUrls.addAll(pictureMetadata);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching pictures: " + e.getMessage());
            }
        }).start();
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

                String fileName = "uploaded_image_" + System.currentTimeMillis() + ".jpg"; // Generate a unique file name
                FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
                        .uploadAndFinish(inputStream);

                Log.d(TAG, "File uploaded: " + metadata.getPathLower());
                // Add the new image URL to the list and update the adapter
                pictureUrls.add(new Metadata(fileName, metadata.getPathLower(), "image/jpeg", false));
                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file: " + e.getMessage());
            }
        }).start();
    }
}