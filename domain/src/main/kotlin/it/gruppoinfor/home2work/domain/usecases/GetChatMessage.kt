package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository


class GetChatMessage(
        transformer: Transformer<ChatMessageEntity>,
        private val chatRepository: ChatRepository
) : UseCase<ChatMessageEntity>(transformer) {

    companion object {
        private const val PARAM_MESSAGE_ID = "param:messageId"
    }

    fun getById(messageId: Long): Observable<ChatMessageEntity> {
        val data = HashMap<String, Long>()
        data[PARAM_MESSAGE_ID] = messageId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ChatMessageEntity> {
        val messageId = data?.get(PARAM_MESSAGE_ID)

        messageId?.let {
            return chatRepository.getChatMessage(messageId as Long)
        } ?: return Observable.error(IllegalArgumentException("messageId must be provided."))

    }
}