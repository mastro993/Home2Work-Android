package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.model.ClientUser
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


internal interface AuthService {

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<Response<ClientUser>>

    @FormUrlEncoded
    @POST("auth/login/token")
    fun login(
            @Field("token") token: String
    ): Observable<Response<ClientUser>>

}