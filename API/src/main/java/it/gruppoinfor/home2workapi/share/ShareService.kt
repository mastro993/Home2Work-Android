package it.gruppoinfor.home2workapi.share

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ShareService{


    @GET("share/{id}")
    fun getShareById(
            @Path("id") shareId: Long?
    ): Observable<Response<Share>>

    @GET("share/ongoing")
    fun getOngoingShare(): Observable<Share>

    @POST("share/new")
    fun createNewShare(): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    fun completeShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<Share>

    @POST("share/{shareId}/finish")
    fun finishShare(
            @Path("shareId") shareId: Long
    ): Observable<Share>

    @POST("share/{shareId}/leave")
    fun leaveShare(
            @Path("shareId") shareId: Long
    ): Observable<Share>

    @FormUrlEncoded
    @POST("share/{shareId}/ban")
    fun banGuestFromShare(
            @Path("shareId") shareId: Long,
            @Field("guestId") guestId: Long
    ): Observable<Share>

    @DELETE("share/{shareId}")
    fun cancelShare(
            @Path("shareId") shareId: Long?
    ): Observable<ResponseBody>
}