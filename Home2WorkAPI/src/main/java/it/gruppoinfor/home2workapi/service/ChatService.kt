package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.inbox.Chat
import it.gruppoinfor.home2workapi.inbox.Message
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


internal interface ChatService {


    @GET("chat/{chatId}")
    fun getChatMessageList(
            @Path("chatId") chatId: String?
    ): Observable<Response<List<Message>>>

    @FormUrlEncoded
    @POST("chat/{chatId}")
    fun sendMessageToChat(
            @Path("chatId") chatId: String?,
            @Field("text") text: String?
    ): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("chat/new")
    fun newChat(
            @Field("recipientId") recipientId: String?
    ): Observable<Response<Chat>>
}