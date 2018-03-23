package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ChatData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.domain.entities.UserEntity
import javax.inject.Inject
import javax.inject.Singleton



class ChatDataEntityMapper @Inject constructor() : Mapper<ChatData, ChatEntity>() {
    override fun mapFrom(from: ChatData): ChatEntity {

        val chatMessageEntity = ChatMessageDataEntityMapper().mapFrom(from.lastMsg)

        val user = UserDataEntityMapper().mapFrom(from.user)

        return ChatEntity(
                id = from.id,
                user = user,
                lastMsg = chatMessageEntity,
                unreadCnt = from.unreadCnt
        )

    }
}