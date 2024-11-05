package vn.edu.usth.dropboxui;

public class ApiConfig {
    private static String accessToken;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }
}