package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ClientUser
import it.gruppoinfor.home2work.domain.entities.User
import it.gruppoinfor.home2work.domain.entities.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface UserService {

    @GET("user")
    fun get(): Observable<Response<ClientUser>>

    @PUT("user")
    fun edit(@Body user: User): Observable<ClientUser>

    @Multipart
    @POST("user/avatar")
    fun uploadAvatar(
            @Part file: MultipartBody.Part
    ): Observable<Response<ResponseBody>>

    @GET("user/profile")
    fun getProfile(): Observable<UserProfile>

    @GET("user/{id}")
    fun getUserById(
            @Path("id") id: Long?
    ): Observable<User>

    @GET("user/{id}/profile")
    fun getUserProfileById(@Path("id") id: Long?): Observable<UserProfile>


}