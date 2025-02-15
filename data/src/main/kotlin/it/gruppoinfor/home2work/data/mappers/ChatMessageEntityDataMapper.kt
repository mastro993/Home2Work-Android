package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.data.entities.ChatMessageData
import it.gruppoinfor.home2work.domain.Mapper
import javax.inject.Inject


class ChatMessageEntityDataMapper @Inject constructor() : Mapper<ChatMessageEntity, ChatMessageData>() {
    override fun mapFrom(from: ChatMessageEntity): ChatMessageData {
        val userEntity = UserEntityDataMapper().mapFrom(from.author)
        return ChatMessageData(
                id = from.id,
                chatId = from.chatId,
                text = from.text,
                date = from.date,
                author = userEntity
        )
    }
}