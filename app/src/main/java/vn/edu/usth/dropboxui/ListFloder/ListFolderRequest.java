package vn.edu.usth.dropboxui.ListFloder;

import com.google.gson.annotations.SerializedName;

public class ListFolderRequest {
    @SerializedName("path")
    private String path;

    public ListFolderRequest(String path) {
        this.path = path;
    }
}

