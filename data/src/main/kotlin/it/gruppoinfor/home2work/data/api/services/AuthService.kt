package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.UserEntity
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AuthService {

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<UserEntity>

    @FormUrlEncoded
    @POST("auth/login/token")
    fun login(
            @Field("token") token: String
    ): Observable<UserEntity>

}