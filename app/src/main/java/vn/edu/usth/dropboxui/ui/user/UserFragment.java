package vn.edu.usth.dropboxui.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.usth.dropboxui.API.ApiConfig;
import vn.edu.usth.dropboxui.API.DropboxApi;
import vn.edu.usth.dropboxui.R;
import vn.edu.usth.dropboxui.model.UserInfo;

public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    private static final String BASE_URL = "https://api.dropboxapi.com/2/";
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView myFileOption;
    private TextView fileRequest;
    private TextView offlineOption;
    private TextView shareOption;
    private TextView settingOption;
    private Button signOutButton;

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
        offlineOption.setOnClickListener(v ->
                Toast.makeText(getContext(), "Account offline", Toast.LENGTH_SHORT).show()
        );

        shareOption = view.findViewById(R.id.share_option);
        shareOption.setOnClickListener(v ->
                Toast.makeText(getContext(), "Share feature is now disabled", Toast.LENGTH_SHORT).show()
        );

        settingOption = view.findViewById(R.id.settings_option);
        settingOption.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_SettingFragment)
        );

        signOutButton = view.findViewById(R.id.sign_out_btn);
        signOutButton.setOnClickListener(v -> {
            ApiConfig.setAccessToken(null);
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.action_userFragment_to_LoginActivity);
        });

        fetchUserInfo();
        return view;
    }

    private void fetchUserInfo() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);
        String accessToken = "Bearer " + ApiConfig.getAccessToken();

        Call<UserInfo> call = dropboxApi.getCurrentAccount(accessToken);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfo userInfo = response.body();
                    userNameTextView.setText(userInfo.getName().getDisplayName());
                    userEmailTextView.setText(userInfo.getEmail());
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e(TAG, "Error fetching user info: " + t.getMessage());
            }
        });
    }
}