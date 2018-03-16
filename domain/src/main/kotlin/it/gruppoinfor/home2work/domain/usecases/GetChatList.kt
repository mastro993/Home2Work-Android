package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository


class GetChatList(
        transformer: Transformer<List<ChatEntity>>,
        private val chatRepository: ChatRepository
) : UseCase<List<ChatEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<ChatEntity>> {
        return chatRepository.getChatList()

    }
}