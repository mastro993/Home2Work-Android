package it.gruppoinfor.home2work.domain.entities


data class ChatEntity(
        val id: Long,
        val user: UserEntity,
        val lastMsg: ChatMessageEntity,
        val unreadCnt: Int
)