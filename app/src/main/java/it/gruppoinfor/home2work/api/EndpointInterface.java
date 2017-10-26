package it.gruppoinfor.home2work.api;

import java.util.List;

import it.gruppoinfor.home2work.models.Company;
import it.gruppoinfor.home2work.models.RoutePoint;
import it.gruppoinfor.home2work.models.User;
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
    @POST("user")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("user/{id}")
    Call<User> getUser(
            @Path("id") Long id
    );

    @PUT("user")
    Call<User> updateUser(
            @Body User user
    );


    @POST("user/{id}/location")
    Call<List<RoutePoint>> uploadRoutePoint(
            @Path("id") Long id,
            @Body List<RoutePoint> routePoints
    );

    @Multipart
    @POST("users/{id}/avatar")
    Call<ResponseBody> uploadAvatar(
            @Path("id") Long userID,
            @Part MultipartBody.Part file
    );

    @GET("companies")
    Call<List<Company>> getCompanies();


    /*@GET("users/{id}/matches")
    Call<List<MatchItem>> getUserMatches(
            @Path("id") Long id
    );


    @FormUrlEncoded
    @POST("users/{id}/follow/{profileId}")
    Call<List<Long>> followUser(
            @Path("id") Long id,
            @Path("profileId") Long profileId,
            @Field("follow") Boolean follow
    );

    @GET("users/{id}/achievements")
    Call<List<Achievement>> getUserAchievements(
            @Path("id") Long id
    );


    @GET("users/{id}/matches/with/{matchUserId}")
    Call<List<MatchInfo>> getUserMatchesWith(
            @Path("id") Long id,
            @Path("matchUserId") Long matchedUserId
    );

    *//*
    @GET("users/{id}/notifications")
    Call<List<Notification>> getUserNotification(
            @Path("id") Long id
    );
    *//*

    @GET("users/{id}/avatar")
    Call<ResponseBody> getAvatar(
            @Path("id") Long id
    );

    @Multipart
    @POST("users/{id}/avatar")
    Call<ResponseBody> uploadAvatar(
            @Path("id") Long userID,
            @Part MultipartBody.Part file
    );

    @GET("companies/{id}")
    Call<Company> getCompany(
            @Path("id") Long companyID
    );

    @GET("companies/{id}/ranks")
    Call<List<User>> getCompanyRanks(
            @Path("id") Long id
    );

    @GET("matches/{id}")
    Call<MatchInfo> getMatch(
            @Path("id") Long id
    );

    @PUT("matches")
    Call<MatchInfo> editMatch(
            @Body MatchInfo match
    );


    @GET("achievements")
    Call<List<Achievement>> getAchievements(
            String sort
    );

    @GET("achievements/{id}")
    Call<Achievement> getAchievement(
            @Path("id") Long achievementId
    );

    *//*
    @GET("notifications")
    Call<List<Notification>> getNotifications(
            String sort
    );

    @GET("notifications/{id}")
    Call<Notification> getNotification(
            @Path("id") Long achievementId
    );
    */


}
