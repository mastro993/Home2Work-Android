package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository


class GetChatMessageList(
        transformer: Transformer<List<ChatMessageEntity>>,
        private val chatRepository: ChatRepository
) : UseCase<List<ChatMessageEntity>>(transformer) {

    companion object {
        private const val PARAM_CHAT_ID = "param:chatId"
    }

    fun getById(shareId: Long): Observable<List<ChatMessageEntity>>{
        val data = HashMap<String, Long>()
        data[PARAM_CHAT_ID] = shareId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<List<ChatMessageEntity>> {

        val chatId = data?.get(PARAM_CHAT_ID)

        chatId?.let {
            return chatRepository.getChatMessageList(chatId as Long)
        } ?: return Observable.error(IllegalArgumentException("chatId must be provided."))

    }
}