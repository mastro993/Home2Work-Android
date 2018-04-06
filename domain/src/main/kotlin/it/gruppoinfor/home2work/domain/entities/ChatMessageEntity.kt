package it.gruppoinfor.home2work.domain.entities

import java.util.*


class ChatMessageEntity(
        val id: Long,
        val chatId: Long,
        val text: String,
        val date: Date,
        val author: UserEntity
)
