package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import it.gruppoinfor.home2work.entities.Author
import it.gruppoinfor.home2work.entities.Chat
import javax.inject.Inject


class ChatEntityChatMapper @Inject constructor() : Mapper<ChatEntity, Chat>() {
    override fun mapFrom(from: ChatEntity): Chat {

        val user = UserEntityUserMapper().mapFrom(from.user)
        val lastMsg = MessageEntityMessageMapper().mapFrom(from.lastMsg)

        return Chat(
                id = from.id,
                author = Author(user),
                lastMsg = lastMsg,
                unreadCnt = from.unreadCnt
        )
    }
}