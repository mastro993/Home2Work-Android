package it.gruppoinfor.home2workapi.chat

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.chat.Chat
import it.gruppoinfor.home2workapi.chat.Message
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


internal interface ChatService {


    @GET("chat/{chatId}")
    fun getChatMessageList(
            @Path("chatId") chatId: Long?
    ): Observable<Response<List<Message>>>

    @FormUrlEncoded
    @POST("chat/{chatId}")
    fun sendMessageToChat(
            @Path("chatId") chatId: Long?,
            @Field("text") text: String?
    ): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("chat/new")
    fun newChat(
            @Field("recipientId") recipientId: Long?
    ): Observable<Response<Chat>>
}