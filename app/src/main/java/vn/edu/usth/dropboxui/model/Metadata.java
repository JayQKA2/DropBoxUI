package vn.edu.usth.dropboxui.model;

public class Metadata {
    private String name;
    private String pathDisplay;
    private String path;
    private boolean isFolder;

    public Metadata(String name, String pathDisplay,String path, boolean isFolder) {
        this.name = name;
        this.pathDisplay = pathDisplay;
        this.path = path;
        this.isFolder = isFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathDisplay() {
        return pathDisplay;
    }

    public void setPathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
    }
    public String getPath() {
        return path;
    }

    public boolean isFolder() {
        return isFolder;
    }
}