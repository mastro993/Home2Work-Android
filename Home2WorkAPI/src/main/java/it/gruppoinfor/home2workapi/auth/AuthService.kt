package it.gruppoinfor.home2workapi.auth

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AuthService {

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<Response<AuthUser>>

    @FormUrlEncoded
    @POST("auth/login/token")
    fun login(
            @Field("token") token: String
    ): Observable<Response<AuthUser>>

}