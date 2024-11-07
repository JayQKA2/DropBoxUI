//package vn.edu.usth.dropboxui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class LoginActivity extends AppCompatActivity {
//    TextView signInTextView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        signInTextView = (TextView) findViewById(R.id.sign_in);
//
//        signInTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//}


package vn.edu.usth.dropboxui.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import vn.edu.usth.dropboxui.API.ApiConfig;
import vn.edu.usth.dropboxui.MainActivity;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "DropboxLoginActivity";
    private static final String CLIENT_ID = "3hps5o66mvbv9g3";
    private static final String CLIENT_SECRET = "n4sagm1rxk8lt8g";
    private static final String REDIRECT_URI = "http://localhost:8080/oauth/callback";

    TextView signInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.sign_up_btn);
        loginButton.setOnClickListener(view -> {
            String authUrl = "https://www.dropbox.com/oauth2/authorize"
                    + "?client_id=" + CLIENT_ID
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&response_type=code";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
            startActivity(browserIntent);
        });

        signInTextView = findViewById(R.id.sign_in);
        signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                getAccessTokenByAuthorizationCode(authorizationCode);
            } else {
                Log.e(TAG, "Authorization code is null");
            }
        }
    }

    private void getAccessTokenByAuthorizationCode(String authorizationCode) {
        String tokenUrl = "https://api.dropbox.com/oauth2/token";
        StringRequest tokenRequest = new StringRequest(
                Request.Method.POST,
                tokenUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String accessToken = jsonResponse.getString("access_token");
                        Log.i(TAG, "Access Token: " + accessToken);
                        onLoginSuccess(accessToken);
                    } catch (JSONException e) {
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
        ApiConfig.setAccessToken(accessToken);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}