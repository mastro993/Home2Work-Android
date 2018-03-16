package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ChatData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.entities.UserEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatDataEntityMapper @Inject constructor() : Mapper<ChatData, ChatEntity>() {
    override fun mapFrom(from: ChatData): ChatEntity {

        val chatMessageEntity = from.lastMsg?.let {
            ChatMessageDataEntityMapper().mapFrom(it)
        }

        val users = ArrayList<UserEntity>()
        users + UserDataEntityMapper().mapFrom(from.user_1)
        users + UserDataEntityMapper().mapFrom(from.user_2)

        return ChatEntity(
                id = from.id,
                users = users,
                lastMsg = chatMessageEntity,
                unreadCnt = from.unreadCnt
        )

    }
}