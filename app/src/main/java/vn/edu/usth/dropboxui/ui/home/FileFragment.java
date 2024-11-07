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
import vn.edu.usth.dropboxui.ui.Adapter.FileAdapter;

public class FileFragment extends Fragment {
    private static final String TAG = "FileFragment";
    private static final String API_URL = "https://api.dropboxapi.com/2/files/list_folder";
    private static final int PICK_FILE_REQUEST = 1;
    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<Metadata> fileUrls;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private DropboxApi dropboxApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_file, container, false);

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
        fileUrls = new ArrayList<>();
        adapter = new FileAdapter(getContext(), fileUrls);
        recyclerView.setAdapter(adapter);
        


        FloatingActionButton uploadButton = view.findViewById(R.id.uploadButton3);
        uploadButton.setOnClickListener(v -> {
            Log.d(TAG, "Upload button clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });


        fetchFiles();


        return view;
    }

    private void fetchFiles() {
        Log.d(TAG, "fetchFiles: started");
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        StringRequest fileRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                response -> {
                    Log.d(TAG, "fetchFiles: response received");
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray entries = jsonResponse.getJSONArray("entries");
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            Metadata metadata = new Metadata(entry.getString("name"), entry.getString("path_display"), entry.getString(".tag"), entry.getString(".tag").equals("folder"));
                            fileUrls.add(metadata);
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

//    private void uploadFileToDropbox(Uri fileUri) {
//        Log.d(TAG, "uploadFileToDropbox: started with URI: " + fileUri.toString());
//        String accessToken = getAccessToken();
//        if (accessToken == null) {
//            Log.e(TAG, "Access token is null");
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
//                if (inputStream == null) {
//                    Log.e(TAG, "Input stream is null");
//                    return;
//                }
//
//                DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
//                DbxClientV2 client = new DbxClientV2(config, accessToken);
//
//                String fileName = "uploaded_file_" + System.currentTimeMillis();
//                FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
//                        .uploadAndFinish(inputStream);
//
//                Log.d(TAG, "File uploaded: " + metadata.getPathLower());
//                fileUrls.add(new Metadata(fileName, metadata.getPathLower(), "file", false));
//                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
//            } catch (Exception e) {
//                Log.e(TAG, "Error uploading file: " + e.getMessage());
//            }
//        }).start();
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_FILE_REQUEST && resultCode == getActivity().RESULT_OK) {
//            if (data != null) {
//                if (data.getClipData() != null) {
//                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
//                        Log.d(TAG, "File selected: " + fileUri.toString());
//                        uploadFileToDropbox(fileUri);
//                    }
//                } else if (data.getData() != null) {
//                    Uri fileUri = data.getData();
//                    Log.d(TAG, "File selected: " + fileUri.toString());
//                    uploadFileToDropbox(fileUri);
//                }
//            } else {
//                Log.e(TAG, "Failed to select file");
//            }
//        }
//    }
}