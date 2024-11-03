package vn.edu.usth.dropboxui.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.edu.usth.dropboxui.ApiConfig;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.MySingleton;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    private static final String USER_INFO_URL = "https://api.dropboxapi.com/2/users/get_current_account";
    private TextView userNameTextView;
    private TextView userEmailTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);
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
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject nameObject = jsonResponse.getJSONObject("name");
                        String userName = nameObject.getString("display_name");
                        String userEmail = jsonResponse.getString("email");
                        Log.d(TAG, "User Name: " + userName);
                        Log.d(TAG, "User Email: " + userEmail);
                        userNameTextView.setText(userName);
                        userEmailTextView.setText(userEmail);
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(userInfoRequest);
    }
}