package it.gruppoinfor.home2work.domain.entities

import it.gruppoinfor.home2work.chat.ChatMessageEntity


data class ChatEntity(
        val id: Long,
        val user: UserEntity,
        val lastMsg: ChatMessageEntity,
        val unreadCnt: Int
)