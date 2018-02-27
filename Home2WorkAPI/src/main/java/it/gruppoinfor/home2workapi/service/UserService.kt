package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.inbox.Chat
import it.gruppoinfor.home2workapi.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


internal interface UserService {

    @GET("user")
    fun get(): Observable<Response<ClientUser>>

    @PUT("user")
    fun edit(@Body user: User): Observable<Response<ClientUser>>

    @Multipart
    @POST("user/avatar")
    fun uploadAvatar(
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("user/fcmtoken")
    fun updateFCMToken(
            @Field("token") fcmToken: String
    ): Observable<Response<ResponseBody>>

    @GET("user/profile")
    fun getProfile(): Observable<Response<UserProfile>>

    @GET("user/chat")
    fun getChatList(): Observable<Response<List<Chat>>>

    @POST("user/location")
    fun uploadLocations(
            @Body routeLocations: List<RouteLocation>
    ): Observable<Response<List<RouteLocation>>>

    @GET("user/match")
    fun getMatchList(): Observable<Response<ArrayList<Match>>>

    @GET("user/share")
    fun getShareList(): Observable<Response<ArrayList<Share>>>


    // ####################################################################


    @GET("user/{id}")
    fun getUserById(
            @Path("id") id: Long?
    ): Observable<Response<User>>

    @GET("user/{id}/profile")
    fun getUserProfileById(@Path("id") id: Long?): Observable<Response<UserProfile>>



}