package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.entities.ChatMessage
import javax.inject.Inject


class MessageMessageEntityMapper @Inject constructor() : Mapper<ChatMessage, ChatMessageEntity>() {
    override fun mapFrom(from: ChatMessage): ChatMessageEntity {

        val user = UserUserEntityMapper().mapFrom(from.author.user)

        return ChatMessageEntity(
                chatId = from.chatId,
                id = from.messageId,
                text = from.messageText,
                date = from.date,
                author = user
        )
    }
}