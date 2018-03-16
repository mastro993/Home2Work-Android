package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ShareEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ShareService {

    @GET("user/share")
    fun getShareList(): Observable<List<ShareEntity>>

    @GET("share/{id}")
    fun getShare(
            @Path("id") shareId: Long?
    ): Observable<Response<ShareEntity>>

    @GET("share/ongoing")
    fun getCurrentShare(): Observable<ShareEntity>

    @POST("share/new")
    fun createShare(): Observable<ShareEntity>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<ShareEntity>

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    fun completeCurrentShare(
            @Field("location") locationString: String
    ): Observable<ShareEntity>

    @POST("share/{shareId}/finish")
    fun finishCurrentShare(): Observable<ShareEntity>

    @POST("share/{shareId}/leave")
    fun leaveCurrentShare(): Observable<ShareEntity>

    @FormUrlEncoded
    @POST("share/{shareId}/ban")
    fun banGuestFromCurrentShare(
            @Field("guestId") guestId: Long
    ): Observable<ShareEntity>

    @DELETE("share/{shareId}")
    fun cancelCurrentShare(): Observable<ResponseBody>
}