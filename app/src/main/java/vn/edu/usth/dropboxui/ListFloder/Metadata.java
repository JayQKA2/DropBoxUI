// Metadata.java
package vn.edu.usth.dropboxui.ListFloder;

import com.google.gson.annotations.SerializedName;

public class Metadata {
    @SerializedName("name")
    private String name;

    @SerializedName("path_lower")
    private String pathLower;

    // Add getter method for pathLower
    public String getPathLower() {
        return pathLower;
    }

    public String getName() {
        return name;
    }

    // Getters for other fields (if needed)
}