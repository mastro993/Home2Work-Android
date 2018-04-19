package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FCMTokenService {
    @FormUrlEncoded
    @POST("firebase/token")
    fun updateFCMToken(
            @Field("token") fcmToken: String
    ): Observable<Boolean>
}