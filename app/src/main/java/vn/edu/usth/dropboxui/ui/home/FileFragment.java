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

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.dropboxui.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.Metadata;
import vn.edu.usth.dropboxui.model.MySingleton;

public class FileFragment extends Fragment {
    private static final String TAG = "FileFragment";
    private static final String API_URL = "https://api.dropboxapi.com/2/files/list_folder";
    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<Metadata> fileUrls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileUrls = new ArrayList<>();
        adapter = new FileAdapter(getContext(), fileUrls);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = view.findViewById(R.id.uploadButton3);
        addButton.setOnClickListener(v -> {
            // Handle add file button click
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("*/*");
            startActivityForResult(intent, 4);
        });

        fetchFiles();

        return view;
    }

    private void fetchFiles() {
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        StringRequest fileRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray entries = jsonResponse.getJSONArray("entries");
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            Metadata metadata = new Metadata(entry.getString("name"), entry.getString("path_display"));
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
                return requestBody.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(fileRequest);
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

                String fileName = "uploaded_file"; // You can generate a unique file name here
                FileMetadata metadata = client.files().uploadBuilder("/" + fileName)
                        .uploadAndFinish(inputStream);

                Log.d(TAG, "File uploaded: " + metadata.getPathLower());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // Handle the file selected from the device
                readFileContent(selectedFileUri);
            }
        }
    }

    private void readFileContent(Uri fileUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
                String fileContent = stringBuilder.toString();
                Log.d(TAG, "File content: " + fileContent);
                // You can now upload the file content to Dropbox or handle it as needed
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading file: " + e.getMessage());
        }
    }
}