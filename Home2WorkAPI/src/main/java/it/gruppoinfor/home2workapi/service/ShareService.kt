package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.model.Share
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ShareService{


    @GET("share/{id}")
    fun getShareById(
            @Path("id") shareId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/new")
    fun createNewShare(
            @Field("hostId") hostId: Long?
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/join")
    fun joinShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/complete")
    fun completeShare(
            @Path("shareId") shareId: Long,
            @Field("location") locationString: String
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/finish")
    fun finishShare(
            @Path("shareId") shareId: Long
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/leave")
    fun leaveShare(
            @Path("shareId") shareId: Long
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/ban")
    fun banGuestFromShare(
            @Path("shareId") shareId: Long,
            @Field("guestId") guestId: Long
    ): Observable<Response<Share>>

    @FormUrlEncoded
    @POST("share/{shareId}/cancel")
    fun cancelShare(
            @Path("shareId") shareId: Long?
    ): Observable<Response<ResponseBody>>
}