package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ChatData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import javax.inject.Inject
import javax.inject.Singleton



class ChatEntityDataMapper @Inject constructor() : Mapper<ChatEntity, ChatData>() {
    override fun mapFrom(from: ChatEntity): ChatData {

        val chatMessageData = from.lastMsg?.let {
            ChatMessageEntityDataMapper().mapFrom(it)
        }

        val user = UserEntityDataMapper().mapFrom(from.user)

        return ChatData(
                id = from.id,
                user = user,
                lastMsg = chatMessageData,
                unreadCnt = from.unreadCnt
        )

    }
}