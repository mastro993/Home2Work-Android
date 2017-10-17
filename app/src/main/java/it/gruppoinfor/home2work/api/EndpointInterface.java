package it.gruppoinfor.home2work.api;

import java.util.List;

import it.gruppoinfor.home2work.models.Achievement;
import it.gruppoinfor.home2work.models.Company;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.Route;
import it.gruppoinfor.home2work.models.SearchResults;
import it.gruppoinfor.home2work.models.Share;
import it.gruppoinfor.home2work.models.ShareRequest;
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

    @GET("search/{term}")
    Call<SearchResults> search(
            @Path("term") String term
    );


    @FormUrlEncoded
    @POST("register")
    Call<User> register(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("users/{id}")
    Call<User> getUser(
            @Path("id") Long id
    );

    @FormUrlEncoded
    @POST("users/{id}/follow/{profileId}")
    Call<List<Long>> followUser(
            @Path("id") Long id,
            @Path("profileId") Long profileId,
            @Field("follow") Boolean follow
    );

    @GET("users/{id}/followers")
    Call<List<User>> getFollowers(
            @Path("id") Long id
    );

    @GET("users/{id}/follows")
    Call<List<User>> getFollowees(
            @Path("id") Long id
    );

    @GET("users/{id}/achievements")
    Call<List<Achievement>> getUserAchievements(
            @Path("id") Long id
    );

    @GET("users/{id}/routes")
    Call<List<Route>> getUserRoutes(
            @Path("id") Long id
    );

    @GET("users/{id}/matches")
    Call<List<Match>> getUserMatches(
            @Path("id") Long id
    );

    @GET("users/{id}/matches/with/{matchUserId}")
    Call<List<Match>> getUserMatchesWith(
            @Path("id") Long id,
            @Path("matchUserId") Long matchedUserId
    );

    @GET("users/{id}/shares")
    Call<List<Share>> getUserShares(
            @Path("id") Long id
    );

    @GET("users/{id}/shares/with/{shareUserId}")
    Call<List<Share>> getUserSharesWith(
            @Path("id") Long id,
            @Path("shareUserId") Long shareUserId
    );

    /*
    @GET("users/{id}/notifications")
    Call<List<Notification>> getUserNotification(
            @Path("id") Long id
    );
    */

    @PUT("users")
    Call<User> updateUser(
            @Body User user
    );

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

    @GET("companies")
    Call<List<Company>> getCompanies();

    @GET("companies/{id}/ranks")
    Call<List<User>> getCompanyRanks(
            @Path("id") Long id
    );

    @GET("matches/{id}")
    Call<Match> getMatch(
            @Path("id") Long id
    );

    @PUT("matches")
    Call<Match> editMatch(
            @Body Match match
    );

    @POST("matches/{id}/request")
    Call<ShareRequest> requestShare(
            @Path("id") Long matchId
    );

    @GET("requests/{id}")
    Call<ShareRequest> getRequest(
            @Path("id") Long id
    );

    @FormUrlEncoded
    @POST("requests/{id}")
    Call<Share> respondToRequest(
            @Path("id") Long requestId,
            @Field("accepted") Boolean accepted
    );

    @GET("shares")
    Call<List<Share>> getShares(
            String sort
    );

    @GET("shares/{id}")
    Call<Share> getShare(
            @Path("id") Long shareId
    );

    @FormUrlEncoded
    @POST("routes")
    Call<Route> uploadRoute(
            @Field("userID") Long userID,
            @Field("date") Long date,
            @Field("routePoints") String routePoints

    );

    @GET("routes/{id}")
    Call<Route> getRoute(
            @Path("id") Long routeId
    );

    @PUT("routes")
    Call<Route> editRoute(
            @Body Route route
    );

    @GET("achievements")
    Call<List<Achievement>> getAchievements(
            String sort
    );

    @GET("achievements/{id}")
    Call<Achievement> getAchievement(
            @Path("id") Long achievementId
    );

    /*
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
