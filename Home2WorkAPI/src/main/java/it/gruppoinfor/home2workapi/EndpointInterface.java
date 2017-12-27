package it.gruppoinfor.home2workapi;

import java.util.List;

import it.gruppoinfor.home2workapi.model.Company;
import it.gruppoinfor.home2workapi.model.Location;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface EndpointInterface {

    @FormUrlEncoded
    @POST("user/login")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("token") boolean tokenMode
    );

    @PUT("user")
    Call<User> updateUser(
            @Body User user
    );

    @GET("user/{id}")
    Call<User> getUser(
            @Path("id") Long id
    );

    @Multipart
    @POST("user/{id}/avatar")
    Call<ResponseBody> uploadAvatar(
            @Path("id") Long userID,
            @Part MultipartBody.Part file
    );

    @GET("company")
    Call<List<Company>> getCompanies();

    @GET("company/{id}")
    Call<Company> getCompany(
            @Path("id") Long id
    );

    @POST("user/{id}/location")
    Call<List<Location>> uploadLocations(
            @Path("id") Long id,
            @Body List<Location> locations
    );

    @GET("user/{id}/match")
    Call<List<Match>> getMatches(
            @Path("id") Long id
    );

    @GET("match/{id}")
    Call<Match> getMatch(
            @Path("id") Long id
    );

    @PUT("match")
    Call<Match> editMatch(
            @Body Match match
    );

    @FormUrlEncoded
    @POST("user/FCMToken")
    Call<ResponseBody> setFCMToken(
            @Field("userId") Long userID,
            @Field("token") String token
    );

    @GET("user/{id}/shares")
    Call<List<Share>> getShares(
            @Path("id") Long userId
    );

    @FormUrlEncoded
    @POST("share/new")
    Call<Share> createShare(
            @Field("hostId") Long hostId
    );

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    Call<ResponseBody> joinShare(
            @Path("shareId") Long shareId,
            @Field("guestId") Long guestId,
            @Field("location") String locationString
    );


}
