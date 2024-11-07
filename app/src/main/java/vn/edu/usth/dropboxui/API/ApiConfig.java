package vn.edu.usth.dropboxui.API;

public class ApiConfig {
    public static String accessToken;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }
}