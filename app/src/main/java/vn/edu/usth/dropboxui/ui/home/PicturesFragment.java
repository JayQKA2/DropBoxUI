package vn.edu.usth.dropboxui.ui.home;

import static vn.edu.usth.dropboxui.API.ApiConfig.accessToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.dropboxui.API.ApiConfig;
import vn.edu.usth.dropboxui.API.DropboxApi;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;
import vn.edu.usth.dropboxui.model.MySingleton;
import vn.edu.usth.dropboxui.ui.Adapter.PicturesAdapter;

public class PicturesFragment extends Fragment {
    private static final String TAG = "PicturesFragment";
    private static final String API_URL = "https://api.dropboxapi.com/2/files/list_folder";
    private static final int PICK_FILE_REQUEST = 1;
    private RecyclerView recyclerView;
    private PicturesAdapter adapter;
    private List<Metadata> pictureUrls;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private DropboxApi dropboxApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri selectedImageUri = data.getData();
                                uploadFileToDropbox(selectedImageUri, "Bearer " + accessToken , "/my_folder/selected_file");
                            }
                        }
                    }
                }
        );

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pictureUrls = new ArrayList<>();
        adapter = new PicturesAdapter(getContext(), pictureUrls);
        recyclerView.setAdapter(adapter);

        FloatingActionButton uploadButton = view.findViewById(R.id.uploadButton5);
        uploadButton.setOnClickListener(v -> {
            Log.d(TAG, "Upload button clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        fetchPictures();

        return view;
    }

    private void fetchPictures() {
        Log.d(TAG, "fetchPictures: started");
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        StringRequest fileRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                response -> {
                    Log.d(TAG, "fetchPictures: response received");
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray entries = jsonResponse.getJSONArray("entries");
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            String name = entry.getString("name");
                            if (isImageFile(name)) {
                                Metadata metadata = new Metadata(name, entry.getString("path_display"), entry.getString(".tag"), entry.getString(".tag").equals("folder"));
                                pictureUrls.add(metadata);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";
            }

            @Override
            public byte[] getBody() {
                String requestBody = "{\"path\": \"\"}";
                Log.d(TAG, "getBody: " + requestBody);
                return requestBody.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Log.d(TAG, "getHeaders: started");
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(fileRequest);
    }

    private boolean isImageFile(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        return lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".png") ||
                lowerCaseName.endsWith(".gif") || lowerCaseName.endsWith(".bmp") || lowerCaseName.endsWith(".webp") ||
                lowerCaseName.endsWith(".tiff") || lowerCaseName.endsWith(".svg") || lowerCaseName.endsWith(".heic");
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("/");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select a file"));
    }

    private void uploadFileToDropbox(Uri fileUri, String authToken, String dropboxFilePath) {
        String filePath = getRealPathFromURI(fileUri);
        if (filePath == null) {
            Log.e("Upload", "Unable to get file path");
            return;
        }

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        String dropboxApiArg = String.format(
                "{\"path\": \"%s\", \"mode\": \"add\", \"autorename\": true, \"mute\": false}",
                dropboxFilePath
        );

        Call<ResponseBody> call = dropboxApi.uploadFile("Bearer " + authToken, dropboxApiArg, "application/octet-stream", requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Upload", "Upload successful");
                } else {
                    Log.e("Upload", "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload", "Upload error: " + t.getMessage());
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
}