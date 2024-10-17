package vn.edu.usth.dropboxui.ui.home;

import android.content.Context;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.usth.dropboxui.DropboxApi;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.RetrofitClient;
import vn.edu.usth.dropboxui.UploadSesion.UploadSessionStartResult;

public class MovieFragment extends Fragment {
    private static final String BASE_URL = "https://content.dropboxapi.com/";
    private static final String ACCESS_TOKEN = "Bearer sl.B-5WbYsKiRFduxZyEHguFH_j3opgoB6p5DrVZUsFbek6bK0AUtamnGujahioTVTJ-T6-3dYpMt6-xhcCmg_CGTJ35gSLBuyAz11VPfGawF-mtcjMMAb3c6ynhR0Gy6gXfZhxgBkG0xXSDlpzSCJ1H7g";
    private static final int PICK_MOVIE_REQUEST_CODE = 3;

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<String> movieUrls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        movieUrls = new ArrayList<>();
        adapter = new MovieAdapter(getContext(), movieUrls);
        recyclerView.setAdapter(adapter);

        FloatingActionButton uploadButton = view.findViewById(R.id.uploadButton2);
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_MOVIE_REQUEST_CODE);
        });

        loadMoviesFromApi();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MOVIE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                uploadMovie(getContext(), uri, "/path/in/dropbox");
            }
        }
    }

    public void uploadMovie(Context context, Uri uri, String dropboxPath) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Process the buffer
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buffer, 0, bytesRead);
                String dropboxApiArg = "{\"path\": \"" + dropboxPath + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";

                Call<UploadSessionStartResult> startCall = dropboxApi.startUploadSession(ACCESS_TOKEN, "{}", requestBody);
                startCall.enqueue(new Callback<UploadSessionStartResult>() {
                    @Override
                    public void onResponse(Call<UploadSessionStartResult> call, Response<UploadSessionStartResult> response) {
                        if (response.isSuccessful()) {
                            String sessionId = response.body().getSessionId();
                            Log.d("MovieFragment", "Upload session started: " + sessionId);

                            final int[] offset = {0};
                            int chunkSize = 1024 * 1024; // 1MB chunk size
                            while (offset[0] < buffer.length) {
                                int remaining = buffer.length - offset[0];
                                int currentChunkSize = Math.min(chunkSize, remaining);
                                byte[] chunk = new byte[currentChunkSize];
                                System.arraycopy(buffer, offset[0], chunk, 0, currentChunkSize);

                                RequestBody chunkRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), chunk);
                                String appendArg = "{\"cursor\": {\"session_id\": \"" + sessionId + "\", \"offset\": " + offset[0] + "}}";

                                Call<Void> appendCall = dropboxApi.appendUploadSession(ACCESS_TOKEN, appendArg, chunkRequestBody);
                                appendCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            offset[0] += currentChunkSize;
                                        } else {
                                            Log.e("MovieFragment", "Append upload session failed: " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("MovieFragment", "Network error: " + t.getMessage());
                                    }
                                });
                            }

                            String finishArg = "{\"cursor\": {\"session_id\": \"" + sessionId + "\", \"offset\": " + buffer.length + "}, \"commit\": {\"path\": \"" + dropboxPath + "\", \"mode\": \"add\", \"autorename\": true, \"mute\": false, \"strict_conflict\": false}}";
                            Call<Void> finishCall = dropboxApi.finishUploadSession(ACCESS_TOKEN, finishArg, requestBody);
                            finishCall.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("MovieFragment", "Upload session finished");
                                    } else {
                                        Log.e("MovieFragment", "Finish upload session failed: " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("MovieFragment", "Network error: " + t.getMessage());
                                }
                            });
                        } else {
                            try {
                                Log.e("MovieFragment", "Start upload session failed: " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("MovieFragment", "Error reading error body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadSessionStartResult> call, Throwable t) {
                        Log.e("MovieFragment", "Network error: " + t.getMessage());
                    }
                });
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMoviesFromApi() {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        Call<List<String>> call = dropboxApi.getImages(ACCESS_TOKEN);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieUrls.clear();
                    movieUrls.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("MovieFragment", "Movies loaded successfully");
                } else {
                    Log.e("MovieFragment", "Failed to load movies: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("MovieFragment", "Network error: " + t.getMessage());
            }
        });
    }
}