package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.GuestData
import it.gruppoinfor.home2work.data.entities.ShareData
import it.gruppoinfor.home2work.domain.entities.Optional
import retrofit2.http.*


interface ShareService {

    @GET("user/share")
    fun getCompletedShareList(): Observable<List<ShareData>>

    @GET("share/{id}")
    fun getCompletedShare(
            @Path("id") shareId: Long?
    ): Observable<Optional<ShareData>>

    @GET("share/{id}/guests")
    fun getShareGuests(
            @Path("id") shareId: Long?
    ): Observable<List<GuestData>>

    @GET("share/current")
    fun getCurrentShare(): Observable<ShareData>

    @POST("share/new")
    fun createShare(): Observable<ShareData>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long,
            @Field("joinLat") joinLat: Double,
            @Field("joinLng") joinLng: Double
    ): Observable<ShareData>

    @FormUrlEncoded
    @POST("share/complete")
    fun completeCurrentShare(
            @Field("completeLat") completeLat: Double,
            @Field("completeLng") completeLng: Double
    ): Observable<Boolean>

    @POST("share/finish")
    fun finishCurrentShare(): Observable<Boolean>

    @POST("share/leave")
    fun leaveCurrentShare(): Observable<Boolean>

    @FormUrlEncoded
    @POST("share/ban")
    fun banGuestFromCurrentShare(
            @Field("guestId") guestId: Long
    ): Observable<Boolean>

    @DELETE("share")
    fun cancelCurrentShare(): Observable<Boolean>
}