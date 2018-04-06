package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.data.entities.ChatData
import it.gruppoinfor.home2work.data.entities.ChatMessageData
import retrofit2.http.*


interface ChatService {


    @GET("chat/{chatId}")
    fun getChatMessageList(
            @Path("chatId") chatId: Long?
    ): Observable<List<ChatMessageData>>

    @FormUrlEncoded
    @POST("chat/{chatId}")
    fun sendMessageToChat(
            @Path("chatId") chatId: Long?,
            @Field("text") text: String?
    ): Observable<ChatMessageData>

    @FormUrlEncoded
    @POST("chat/new")
    fun newChat(
            @Field("recipientId") userId: Long?
    ): Observable<ChatData>

    @GET("user/chat")
    fun getChatList(): Observable<List<ChatData>>
}