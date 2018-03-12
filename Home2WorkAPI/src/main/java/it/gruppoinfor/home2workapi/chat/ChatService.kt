package it.gruppoinfor.home2workapi.chat

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*


interface ChatService {


    @GET("chat/{chatId}")
    fun getChatMessageList(
            @Path("chatId") chatId: Long?
    ): Observable<List<Message>>

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
    ): Observable<Chat>
}