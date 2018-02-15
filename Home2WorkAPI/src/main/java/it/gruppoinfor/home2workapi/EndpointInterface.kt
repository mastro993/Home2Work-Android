package it.gruppoinfor.home2workapi

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

internal interface EndpointInterface {

    @get:GET("company")
    val companies: Observable<Response<List<Company>>>

    @FormUrlEncoded
    @POST("user/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("token") tokenMode: Boolean
    ): Observable<Response<User>>

    @PUT("user")
    fun updateUser(
            @Body user: User
    ): Observable<Response<User>>

    @GET("user/{id}")
    fun getUser(
            @Path("id") id: Long?
    ): Observable<Response<User>>

    @Multipart
    @POST("user/{id}/avatar")
    fun uploadAvatar(
            @Path("id") userID: Long?,
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @GET("company/{id}")
    fun getCompany(
            @Path("id") id: Long?
    ): Observable<Response<Company>>

    @POST("user/{id}/location")
    fun uploadLocations(
            @Path("id") id: Long?,
            @Body routeLocations: List<RouteLocation>
    ): Observable<Response<List<RouteLocation>>>

    @GET("user/{id}/match")
    fun getMatches(
            @Path("id") id: Long?
    ): Observable<Response<ArrayList<Match>>>

    @GET("match/{id}")
    fun getMatch(
            @Path("id") id: Long?
    ): Observable<Response<Match>>

    @PUT("match")
    fun editMatch(
            @Body match: Match
    ): Observable<Response<Match>>

    @FormUrlEncoded
    @POST("user/FCMToken")
    fun setFCMToken(
            @Field("userId") userID: Long?,
            @Field("token") token: String
    ): Observable<Response<ResponseBody>>

    @GET("user/{id}/shares")
    fun getShares(
            @Path("id") userId: Long?
    ): Observable<Response<ArrayList<Share>>>

    @GET("share/{id}")
    fun getShare(
            @Path("id") shareId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/new")
    fun createShare(
            @Field("hostId") hostId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long?,
            @Field("guestId") guestId: Long?,
            @Field("location") locationString: String
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    fun completeShare(
            @Path("shareId") shareId: Long?,
            @Field("guestId") guestId: Long?,
            @Field("location") locationString: String
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/finish")
    fun finishShare(
            @Path("shareId") shareId: Long?,
            @Field("hostId") hostId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/leave")
    fun leaveShare(
            @Path("shareId") shareId: Long?,
            @Field("guestId") guestId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/cancel")
    fun cancelShare(
            @Path("shareId") shareId: Long?,
            @Field("hostId") guestId: Long?
    ): Observable<Response<ResponseBody>>

    @GET("user/{id}/profile")
    fun getUserProfile(
            @Path("id") userId: Long?
    ): Observable<Response<UserProfile>>


}
