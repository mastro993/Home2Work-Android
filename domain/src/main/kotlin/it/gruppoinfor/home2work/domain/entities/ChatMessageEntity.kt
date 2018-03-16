package it.gruppoinfor.home2work.chat

import it.gruppoinfor.home2work.domain.entities.UserEntity
import java.util.*


class ChatMessageEntity(
        val id: String,
        val chatId: Long,
        val text: String,
        val date: Date,
        val author: UserEntity
)
