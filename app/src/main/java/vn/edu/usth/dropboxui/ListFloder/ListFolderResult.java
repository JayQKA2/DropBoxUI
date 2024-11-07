
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


    public List<Metadata> getEntries() {
        return entries;
    }


}