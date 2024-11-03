package vn.edu.usth.dropboxui.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.dropboxui.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.MySingleton;

public class MovieFragment extends Fragment {
    private static final String TAG = "MovieFragment";
    private static final String API_URL = "https://api.dropboxapi.com/2/files/list_folder";
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

        FloatingActionButton addButton = view.findViewById(R.id.uploadButton2);
        addButton.setOnClickListener(v -> {
            // Handle add movie button click
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, 3);
        });

        fetchMovies();

        return view;
    }

    private void fetchMovies() {
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        StringRequest movieRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray entries = jsonResponse.getJSONArray("entries");
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject entry = entries.getJSONObject(i);
                            String movieUrl = entry.getString("path_display");
                            movieUrls.add(movieUrl);
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

        MySingleton.getInstance(getContext()).addToRequestQueue(movieRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == getActivity().RESULT_OK && data != null) {
            // Handle the movie selected from gallery
            // You can upload the movie to Dropbox here
        }
    }
}