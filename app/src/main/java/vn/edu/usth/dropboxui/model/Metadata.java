package vn.edu.usth.dropboxui.model;

public class Metadata {
    private String name;
    private String pathDisplay;

    public Metadata(String name, String pathDisplay) {
        this.name = name;
        this.pathDisplay = pathDisplay;
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
}