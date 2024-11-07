package vn.edu.usth.dropboxui.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.edu.usth.dropboxui.API.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.MySingleton;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    private static final String USER_INFO_URL = "https://api.dropboxapi.com/2/users/get_current_account";
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView myFileOption;
    private TextView fileRequest;
    private TextView offlineOption;
    private TextView shareOption;
    private TextView settingOption;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);

        myFileOption = view.findViewById(R.id.my_files_option);
        myFileOption.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_MyFileFragment)
        );

        fileRequest = view.findViewById(R.id.file_requests_option);
        fileRequest.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_FileFragment)
        );

        offlineOption = view.findViewById(R.id.offline_option);
        offlineOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Account offline", Toast.LENGTH_SHORT).show();
            }
        });

        shareOption = view.findViewById(R.id.share_option);
        shareOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Share feature is now disabled", Toast.LENGTH_SHORT).show();
            }
        });

        settingOption = view.findViewById(R.id.settings_option);
        settingOption.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_SettingFragment)
        );



        fetchUserInfo();
        return view;
    }

    private void fetchUserInfo() {
        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e(TAG, "Access token is null");
            return;
        }

        StringRequest userInfoRequest = new StringRequest(
                Request.Method.POST,
                USER_INFO_URL,
                response -> {
                    Log.d(TAG, "Response: " + response);
                    try {
                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject nameObject = jsonResponse.optJSONObject("name");
                        if (nameObject != null) {
                            String userName = nameObject.optString("display_name", "Unknown User");
                            String userEmail = jsonResponse.optString("email", "No email provided");

                            // Update TextViews
                            userNameTextView.setText(userName);
                            userEmailTextView.setText(userEmail);
                        } else {
                            Log.e(TAG, "Name object not found in response");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching user info: " + error.getMessage());
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add request to queue
        MySingleton.getInstance(getContext()).addToRequestQueue(userInfoRequest);
    }
}
