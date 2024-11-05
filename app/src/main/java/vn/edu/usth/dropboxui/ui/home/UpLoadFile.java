package vn.edu.usth.dropboxui.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class UpLoadFile extends AsyncTask<Void, Long, Boolean> {
    private static final String TAG = "UploadFile";
    private Context context;
    private String accessToken;
    private String dir;
    private File file;

    public UpLoadFile(Context context, String accessToken, String dir, File file) {
        this.context = context;
        this.accessToken = accessToken;
        this.dir = dir;
        this.file = file;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        try (InputStream in = new FileInputStream(file)) {
            FileMetadata metadata = client.files().uploadBuilder(dir + "/" + file.getName())
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);
            Log.d(TAG, "File uploaded: " + metadata.getPathLower());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error uploading file: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.d(TAG, "File upload successful");
        } else {
            Log.d(TAG, "File upload failed");
        }
    }
}