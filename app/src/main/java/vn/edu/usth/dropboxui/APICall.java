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
    private static final String BASE_URL = "https://api.dropboxapi.com/";
    private static final String ACCESS_TOKEN = "Bearer sl.B-83JF6FRPGlvCFq4yqi-RIj5XGWaW7dZkIzBsMeSCxj5_ccoJfEesKU2-HqVZkS-jEcyVru07np0VJLOQKA8sBvD4hu0sShD2xJDJdLOydL-Q5Yw6KCnSN9rMW_0UvgOX21bKDbv6yGjP2KRx8o1Fs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadFileToDropbox("/path/to/your/file.jpg", "/dropbox/path/file.jpg");
    }

    private void uploadFileToDropbox(String localFilePath, String dropboxPath) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        DropboxApi dropboxApi = retrofit.create(DropboxApi.class);

        File file = new File(localFilePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        String dropboxApiArg = "{\"path\": \"" + dropboxPath + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\": false}";

        Call<Metadata> call = dropboxApi.uploadFile(ACCESS_TOKEN, dropboxApiArg, "application/octet-stream", requestBody);
        call.enqueue(new Callback<Metadata>() {
            @Override
            public void onResponse(Call<Metadata> call, Response<Metadata> response) {
                if (response.isSuccessful()) {
                    Log.d("APICall", "File uploaded successfully");
                } else {
                    Log.e("APICall", "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Metadata> call, Throwable t) {
                Log.e("APICall", "Network error: " + t.getMessage());
            }
        });
    }
}