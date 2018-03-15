package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Share
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ShareService {

    @GET("user/share")
    fun getShareList(): Observable<List<Share>>

    @GET("share/{id}")
    fun getShare(
            @Path("id") shareId: Long?
    ): Observable<Response<Share>>

    @GET("share/ongoing")
    fun getCurrentShare(): Observable<Share>

    @POST("share/new")
    fun createShare(): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    fun completeCurrentShare(
            @Field("location") locationString: String
    ): Observable<Share>

    @POST("share/{shareId}/finish")
    fun finishCurrentShare(): Observable<Share>

    @POST("share/{shareId}/leave")
    fun leaveCurrentShare(): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/ban")
    fun banGuestFromCurrentShare(
            @Field("guestId") guestId: Long
    ): Observable<Share>

    @DELETE("share/{shareId}")
    fun cancelCurrentShare(): Observable<ResponseBody>
}