package vn.edu.usth.dropboxui.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

public class FileFragment extends Fragment {
    private static final String BASE_URL = "https://content.dropboxapi.com/";
    private static final String ACCESS_TOKEN = "Bearer sl.B-83JF6FRPGlvCFq4yqi-RIj5XGWaW7dZkIzBsMeSCxj5_ccoJfEesKU2-HqVZkS-jEcyVru07np0VJLOQKA8sBvD4hu0sShD2xJDJdLOydL-Q5Yw6KCnSN9rMW_0UvgOX21bKDbv6yGjP2KRx8o1Fs";
    private static final int PICK_FILE_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private FileAdapter adapter;
    private List<Metadata> fileMetadataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fileMetadataList = new ArrayList<>();
        adapter = new FileAdapter(getContext(), fileMetadataList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton uploadButton = view.findViewById(R.id.uploadButton3);
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });

        loadFilesFromApi();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                uploadFile(getContext(), uri, "/path/in/dropbox");
            }
        }
    }

    private void loadFilesFromApi() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        ListFolderRequest request = new ListFolderRequest("");
        Call<ListFolderResult> call = dropboxApi.listFolder(ACCESS_TOKEN, request);
        call.enqueue(new Callback<ListFolderResult>() {
            @Override
            public void onResponse(Call<ListFolderResult> call, Response<ListFolderResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fileMetadataList.clear();
                    fileMetadataList.addAll(response.body().getEntries());
                    adapter.notifyDataSetChanged();
                    Log.d("FileFragment", "Files loaded successfully");
                } else {
                    Log.e("FileFragment", "Failed to load files: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ListFolderResult> call, Throwable t) {
                Log.e("FileFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private void uploadFile(Context context, Uri uri, String dropboxPath) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buffer, 0, bytesRead);
                String dropboxApiArg = "{\"path\": \"" + dropboxPath + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";

                Call<Metadata> call = dropboxApi.uploadFile(ACCESS_TOKEN, dropboxApiArg, "application/octet-stream", requestBody);
                call.enqueue(new Callback<Metadata>() {
                    @Override
                    public void onResponse(Call<Metadata> call, Response<Metadata> response) {
                        if (response.isSuccessful()) {
                            Log.d("FileFragment", "File uploaded successfully");
                            loadFilesFromApi();
                        } else {
                            Log.e("FileFragment", "Failed to upload file: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Metadata> call, Throwable t) {
                        Log.e("FileFragment", "Network error: " + t.getMessage());
                    }
                });
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String dropboxPath) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        String json = "{\"path\": \"" + dropboxPath + "\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Call<Void> call = dropboxApi.deleteFile(ACCESS_TOKEN, requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FileFragment", "File deleted successfully");
                    loadFilesFromApi();
                } else {
                    Log.e("FileFragment", "Failed to delete file: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FileFragment", "Network error: " + t.getMessage());
            }
        });
    }
    private void downloadFile(String fileUrl, String localPath) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        Call<ResponseBody> call = dropboxApi.downloadFile(ACCESS_TOKEN, fileUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new SaveFileTask().execute(response.body(), localPath);
                } else {
                    Log.e("FileFragment", "Failed to download file: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FileFragment", "Network error: " + t.getMessage());
            }
        });
    }

    private class SaveFileTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            ResponseBody body = (ResponseBody) params[0];
            String localPath = (String) params[1];

            try (InputStream inputStream = body.byteStream();
                 FileOutputStream outputStream = new FileOutputStream(localPath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("FileFragment", "File downloaded successfully");
                // Update UI to display the downloaded file
                loadFilesFromApi();
            } else {
                Log.e("FileFragment", "Failed to save the file");
            }
        }
    }
}