// ListFolderResult.java
package vn.edu.usth.dropboxui.ListFloder;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListFolderResult {
    @SerializedName("entries")
    private List<Metadata> entries;

    @SerializedName("cursor")
    private String cursor;

    @SerializedName("has_more")
    private boolean hasMore;

    // Add getter method for entries
    public List<Metadata> getEntries() {
        return entries;
    }

    // Getters for other fields (if needed)
}