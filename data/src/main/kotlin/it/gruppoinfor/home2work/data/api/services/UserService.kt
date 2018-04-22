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

    @GET("user")
    fun get(
            @Query("id") id: Long? = null
    ): Observable<UserData>

    @Multipart
    @POST("user/avatar")
    fun uploadAvatar(
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @GET("user/profile")
    fun getProfile(
            @Query("id") id: Long? = null
    ): Observable<ProfileData>

    @FormUrlEncoded
    @POST("user/status")
    fun updateStatus(
            @Field("status") status: String
    ): Observable<Boolean>

    @DELETE("user/status")
    fun hideStatus(): Observable<Boolean>


}