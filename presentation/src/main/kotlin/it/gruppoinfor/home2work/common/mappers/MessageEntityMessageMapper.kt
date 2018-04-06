package it.gruppoinfor.home2work.common.mappers

import it.gruppoinfor.home2work.domain.entities.ChatMessageEntity
import it.gruppoinfor.home2work.domain.Mapper
import it.gruppoinfor.home2work.entities.Author
import it.gruppoinfor.home2work.entities.ChatMessage
import javax.inject.Inject


class MessageEntityMessageMapper @Inject constructor() : Mapper<ChatMessageEntity, ChatMessage>() {
    override fun mapFrom(from: ChatMessageEntity): ChatMessage {

        val user = UserEntityUserMapper().mapFrom(from.author)

        return ChatMessage(
                messageId = from.id,
                chatId = from.chatId,
                messageText = from.text,
                date = from.date,
                author = Author(user)
        )
    }
}