package vn.edu.usth.dropboxui.API;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import vn.edu.usth.dropboxui.ListFloder.ListFolderRequest;
import vn.edu.usth.dropboxui.ListFloder.ListFolderResult;
import vn.edu.usth.dropboxui.model.UserInfo;

public interface DropboxApi {
    @POST("2/files/list_folder")
    Call<ListFolderResult> listFolder(@Header("Authorization") String accessToken, @Body ListFolderRequest body);

    @GET("2/files/list_folder")
    Call<List<String>> getImages(@Header("Authorization") String accessToken);

    @Headers("Content-Type: application/octet-stream")
    @POST("2/files/upload")
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String accessToken,
            @Header("Dropbox-API-Arg") String dropboxApiArg,
            @Header("Content-Type") String contentType,
            @Body RequestBody file
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Header("Authorization") String accessToken, @Url String fileUrl);


    @POST("2/files/upload_session/append_v2")
    Call<Void> appendUploadSession(
            @Header("Authorization") String accessToken,
            @Header("Dropbox-API-Arg") String dropboxApiArg,
            @Body RequestBody file
    );

    @POST("2/files/upload_session/finish")
    Call<Void> finishUploadSession(
            @Header("Authorization") String accessToken,
            @Header("Dropbox-API-Arg") String dropboxApiArg,
            @Body RequestBody file
    );

    @POST("2/files/delete_v2")
    Call<Void> deleteFile(
            @Header("Authorization") String authorization,
            @Body RequestBody requestBody
    );
    @POST("users/get_current_account")
    Call<UserInfo> getCurrentAccount(@Header("Authorization") String authHeader);

}