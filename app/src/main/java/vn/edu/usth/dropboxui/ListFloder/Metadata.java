// Metadata.java
package vn.edu.usth.dropboxui.ListFloder;

import com.google.gson.annotations.SerializedName;

public class Metadata {
    @SerializedName("name")
    private String name;

    @SerializedName("path_lower")
    private String pathLower;

    public String getPathLower() {
        return pathLower;
    }

    public String getName() {
        return name;
    }
        private long size;

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }


}