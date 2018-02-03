package it.gruppoinfor.home2workapi;

import java.util.List;

import io.reactivex.Observable;
import it.gruppoinfor.home2workapi.model.Company;
import it.gruppoinfor.home2workapi.model.Location;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;
import it.gruppoinfor.home2workapi.model.UserProfile;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

interface EndpointInterface {

    @FormUrlEncoded
    @POST("user/login")
    Observable<Response<User>> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("token") boolean tokenMode
    );

    @PUT("user")
    Observable<Response<User>> updateUser(
            @Body User user
    );

    @GET("user/{id}")
    Observable<Response<User>> getUser(
            @Path("id") Long id
    );

    @Multipart
    @POST("user/{id}/avatar")
    Observable<Response<ResponseBody>> uploadAvatar(
            @Path("id") Long userID,
            @Part MultipartBody.Part file
    );

    @GET("company")
    Observable<Response<List<Company>>> getCompanies();

    @GET("company/{id}")
    Observable<Response<Company>> getCompany(
            @Path("id") Long id
    );

    @POST("user/{id}/location")
    Observable<Response<List<Location>>> uploadLocations(
            @Path("id") Long id,
            @Body List<Location> locations
    );

    @GET("user/{id}/match")
    Observable<Response<List<Match>>> getMatches(
            @Path("id") Long id
    );

    @GET("match/{id}")
    Observable<Response<Match>> getMatch(
            @Path("id") Long id
    );

    @PUT("match")
    Observable<Response<Match>> editMatch(
            @Body Match match
    );

    @FormUrlEncoded
    @POST("user/FCMToken")
    Observable<Response<ResponseBody>> setFCMToken(
            @Field("userId") Long userID,
            @Field("token") String token
    );

    @GET("user/{id}/shares")
    Observable<Response<List<Share>>> getShares(
            @Path("id") Long userId
    );

    @GET("share/{id}")
    Observable<Response<Share>> getShare(
            @Path("id") Long shareId
    );

    @FormUrlEncoded
    @POST("share/new")
    Observable<Response<Share>> createShare(
            @Field("hostId") Long hostId
    );

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    Observable<Response<Share>> joinShare(
            @Path("shareId") Long shareId,
            @Field("guestId") Long guestId,
            @Field("location") String locationString
    );

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    Observable<Response<Share>> completeShare(
            @Path("shareId") Long shareId,
            @Field("guestId") Long guestId,
            @Field("location") String locationString
    );

    @FormUrlEncoded
    @POST("share/{shareId}/finish")
    Observable<Response<Share>> finishShare(
            @Path("shareId") Long shareId,
            @Field("hostId") Long hostId
    );

    @FormUrlEncoded
    @POST("share/{shareId}/leave")
    Observable<Response<Share>> leaveShare(
            @Path("shareId") Long shareId,
            @Field("guestId") Long guestId
    );

    @FormUrlEncoded
    @POST("share/{shareId}/cancel")
    Observable<Response<ResponseBody>> cancelShare(
            @Path("shareId") Long shareId,
            @Field("hostId") Long guestId
    );

    @GET("user/{id}/profile")
    Observable<Response<UserProfile>> getUserProfile(
            @Path("id") Long userId
    );


}
