package it.gruppoinfor.home2workapi.user

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.auth.AuthUser
import it.gruppoinfor.home2workapi.chat.Chat
import it.gruppoinfor.home2workapi.location.Location
import it.gruppoinfor.home2workapi.match.Match
import it.gruppoinfor.home2workapi.share.Share
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface UserService {

    @GET("user")
    fun get(): Observable<Response<AuthUser>>

    @PUT("user")
    fun edit(@Body user: User): Observable<AuthUser>

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
    fun getProfile(): Observable<UserProfile>

    @GET("user/chat")
    fun getChatList(): Observable<List<Chat>>

    @POST("user/location")
    fun uploadLocations(
            @Body locations: List<Location>
    ): Observable<Response<List<Location>>>

    @GET("user/match")
    fun getMatchList(): Observable<ArrayList<Match>>

    @GET("user/share")
    fun getShareList(): Observable<List<Share>>

    @GET("user/{id}")
    fun getUserById(
            @Path("id") id: Long?
    ): Observable<Response<User>>

    @GET("user/{id}/profile")
    fun getUserProfileById(@Path("id") id: Long?): Observable<UserProfile>


}