package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.data.entities.ChatData
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.domain.entities.ChatEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatEntityDataMapper @Inject constructor() : Mapper<ChatEntity, ChatData>() {
    override fun mapFrom(from: ChatEntity): ChatData {

        val chatMessageData = from.lastMsg?.let {
            ChatMessageEntityDataMapper().mapFrom(it)
        }

        val user_1 = UserEntityDataMapper().mapFrom(from.users[0])
        val user_2 = UserEntityDataMapper().mapFrom(from.users[1])

        return ChatData(
                id = from.id,
                user_1 = user_1,
                user_2 = user_2,
                lastMsg = chatMessageData,
                unreadCnt = from.unreadCnt
        )

    }
}