package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.chat.ChatMessageEntity
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository


class SendMessage(
        transformer: Transformer<Boolean>,
        private val chatRepository: ChatRepository
) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_CHAT_ID = "param:chatId"
        private const val PARAM_MESSAGE = "param:message"
    }

    fun send(chatId: Long, message: String): Observable<Boolean> {
        val data = HashMap<String, Any>()
        data[PARAM_CHAT_ID] = chatId
        data[PARAM_MESSAGE] = message
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {

        val message = data?.get(PARAM_MESSAGE)
        val chatId = data?.get(PARAM_CHAT_ID)

        chatId?.let {id ->
            message?.let {message->
                return chatRepository.sendMessage(id as Long, message as String)
            } ?: return Observable.error(IllegalArgumentException("message must be provided."))
        }?: return Observable.error(IllegalArgumentException("chatId must be provided."))

    }
}