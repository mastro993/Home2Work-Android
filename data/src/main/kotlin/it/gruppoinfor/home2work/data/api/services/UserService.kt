package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.UserEntity
import it.gruppoinfor.home2work.domain.entities.UserProfileEntity
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface UserService {

    @GET("user")
    fun get(): Observable<Response<UserEntity>>

    @PUT("user")
    fun edit(@Body user: User): Observable<UserEntity>

    @Multipart
    @POST("user/avatar")
    fun uploadAvatar(
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @GET("user/profile")
    fun getProfile(): Observable<UserProfileEntity>

    @GET("user/{id}")
    fun getUserById(
            @Path("id") id: Long?
    ): Observable<User>

    @GET("user/{id}/profile")
    fun getUserProfileById(@Path("id") id: Long?): Observable<UserProfileEntity>


}