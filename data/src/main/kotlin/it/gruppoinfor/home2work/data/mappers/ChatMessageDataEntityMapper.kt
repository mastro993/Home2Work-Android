package it.gruppoinfor.home2work.data.mappers

import it.gruppoinfor.home2work.chat.ChatMessageEntity
import it.gruppoinfor.home2work.data.entities.ChatMessageData
import it.gruppoinfor.home2work.domain.Mapper
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatMessageDataEntityMapper @Inject constructor() : Mapper<ChatMessageData, ChatMessageEntity>() {
    override fun mapFrom(from: ChatMessageData): ChatMessageEntity {
        val userEntity = UserDataEntityMapper().mapFrom(from.author)
        return ChatMessageEntity(
                id = from.id,
                chatId = from.chatId,
                text = from.text,
                date = from.date,
                author = userEntity
        )
    }
}