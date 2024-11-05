package vn.edu.usth.dropboxui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import vn.edu.usth.dropboxui.model.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "DropboxLoginActivity";
    private static final String CLIENT_ID = "3hps5o66mvbv9g3";
    private static final String CLIENT_SECRET = "n4sagm1rxk8lt8g";
    private static final String REDIRECT_URI = "http://localhost:6060/oauth/callback";

    TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> {
            Log.d(TAG, "Login button clicked");
            String authUrl = "https://www.dropbox.com/oauth2/authorize"
                    + "?client_id=" + CLIENT_ID
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&response_type=code";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
            startActivity(browserIntent);
        });

        signInTextView = findViewById(R.id.sign_in);
        signInTextView.setOnClickListener(v -> {
            Log.d(TAG, "Sign in text view clicked");
            Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: started");

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                Log.d(TAG, "Authorization code received: " + authorizationCode);
                getAccessTokenByAuthorizationCode(authorizationCode);
            } else {
                Log.e(TAG, "Authorization code is null");
            }
        }
    }

    private void getAccessTokenByAuthorizationCode(String authorizationCode) {
        Log.d(TAG, "getAccessTokenByAuthorizationCode: started");
        String tokenUrl = "https://api.dropbox.com/oauth2/token";
        StringRequest tokenRequest = new StringRequest(
                Request.Method.POST,
                tokenUrl,
                response -> {
                    Log.d(TAG, "Access token response received");
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String accessToken = jsonResponse.getString("access_token");
                        Log.i(TAG, "Access Token: " + accessToken);
                        onLoginSuccess(accessToken);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    error.printStackTrace();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d(TAG, "getParams: started");
                Map<String, String> params = new HashMap<>();
                params.put("code", authorizationCode);
                params.put("grant_type", "authorization_code");
                params.put("redirect_uri", REDIRECT_URI);
                params.put("client_id", CLIENT_ID);
                params.put("client_secret", CLIENT_SECRET);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(tokenRequest);
    }

    private void onLoginSuccess(String accessToken) {
        Log.d(TAG, "onLoginSuccess: started");
        ApiConfig.setAccessToken(accessToken);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}