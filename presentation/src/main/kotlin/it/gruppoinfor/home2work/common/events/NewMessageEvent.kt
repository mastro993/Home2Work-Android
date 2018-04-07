package it.gruppoinfor.home2work.common.events


data class NewMessageEvent(
        val chatId: Long?,
        val messageId: Long?
)