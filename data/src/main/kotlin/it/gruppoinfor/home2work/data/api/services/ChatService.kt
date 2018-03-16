package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.chat.ChatMessageEntity
import okhttp3.ResponseBody
import retrofit2.http.*


interface ChatService {


    @GET("chat/{chatId}")
    fun getChatMessageList(
            @Path("chatId") chatId: Long?
    ): Observable<List<ChatMessageEntity>>

    @FormUrlEncoded
    @POST("chat/{chatId}")
    fun sendMessageToChat(
            @Path("chatId") chatId: Long?,
            @Field("text") text: String?
    ): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("chat/new")
    fun newChat(
            @Field("recipientId") recipientId: Long?
    ): Observable<ChatEntity>

    @GET("user/chat")
    fun getChatList(): Observable<List<ChatEntity>>
}