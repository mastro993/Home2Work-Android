package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.ProfileData
import it.gruppoinfor.home2work.data.entities.UserData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface UserService {

    @FormUrlEncoded
    @POST("user/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<UserData>

    @FormUrlEncoded
    @POST("user/login/token")
    fun login(
            @Field("token") token: String
    ): Observable<UserData>

    @GET("user")
    fun get(): Observable<UserData>

    @PUT("user")
    fun edit(@Body user: UserData): Observable<UserData>

    @Multipart
    @POST("user/avatar")
    fun uploadAvatar(
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @GET("user/profile")
    fun getProfile(): Observable<ProfileData>

    @GET("user/{id}")
    fun getUserById(
            @Path("id") id: Long?
    ): Observable<UserData>

    @GET("user/{id}/profile")
    fun getUserProfileById(@Path("id") id: Long?): Observable<ProfileData>


}