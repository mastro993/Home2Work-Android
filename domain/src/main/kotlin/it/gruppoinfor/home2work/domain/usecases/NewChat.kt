package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.interfaces.ChatRepository


class NewChat(
        transformer: Transformer<ChatEntity>,
        private val chatRepository: ChatRepository
) : UseCase<ChatEntity>(transformer) {

    companion object {
        private const val PARAM_USER_ID = "param:userId"
    }

    fun withUserId(userId: Long): Observable<ChatEntity> {
        val data = HashMap<String, Long>()
        data[PARAM_USER_ID] = userId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<ChatEntity> {

        val userId = data?.get(PARAM_USER_ID)

        userId?.let {
            return chatRepository.newChat(userId as Long)
        } ?: return Observable.error(IllegalArgumentException("userId must be provided."))

    }
}