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
        private const val PARAM_MESSAGE = "param:message"
    }

    fun send(message: ChatMessageEntity): Observable<Boolean> {
        val data = HashMap<String, ChatMessageEntity>()
        data[PARAM_MESSAGE] = message
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {

        val message = data?.get(PARAM_MESSAGE)

        message?.let {
            return chatRepository.sendMessage(message as ChatMessageEntity)
        } ?: return Observable.error(IllegalArgumentException("message must be provided."))

    }
}