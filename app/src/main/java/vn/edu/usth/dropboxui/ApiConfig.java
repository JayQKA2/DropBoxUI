package vn.edu.usth.dropboxui;

public class ApiConfig {
    public static final String BASE_URL = "https://api.dropboxapi.com";
    private static String ACCESS_TOKEN;

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = "Bearer " + accessToken;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }
}