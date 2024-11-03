// APICall.java
package vn.edu.usth.dropboxui;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.edu.usth.dropboxui.ListFloder.ListFolderRequest;
import vn.edu.usth.dropboxui.ListFloder.ListFolderResult;
import vn.edu.usth.dropboxui.ListFloder.Metadata;

public class APICall extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadFileToDropbox("/path/to/your/file.jpg", "/dropbox/path/file.jpg");
    }

    private void uploadFileToDropbox(String localFilePath, String dropboxPath) {
        Retrofit retrofit = RetrofitClient.getClient(ApiConfig.BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        File file = new File(localFilePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        String dropboxApiArg = "{\"path\": \"" + dropboxPath + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";

        String accessToken = ApiConfig.getAccessToken();
        if (accessToken == null) {
            Log.e("APICall", "Access token is null");
            return;
        }

        Call<Metadata> call = dropboxApi.uploadFile(accessToken, dropboxApiArg, "application/octet-stream", requestBody);
        call.enqueue(new Callback<Metadata>() {
            @Override
            public void onResponse(Call<Metadata> call, Response<Metadata> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    Log.i("APICall", "File uploaded successfully: " + response.body());
                } else {
                    Log.e("APICall", "Upload failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Metadata> call, Throwable t) {
                Log.e("APICall", "Network error: " + t.getMessage());
            }
        });
    }
}