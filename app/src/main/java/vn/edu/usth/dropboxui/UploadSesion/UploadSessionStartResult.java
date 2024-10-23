package vn.edu.usth.dropboxui.UploadSesion;

import com.google.gson.annotations.SerializedName;

public class UploadSessionStartResult {
    @SerializedName("session_id")
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }
}