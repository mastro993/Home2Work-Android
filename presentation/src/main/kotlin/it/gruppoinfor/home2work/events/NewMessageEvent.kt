package it.gruppoinfor.home2work.events


data class NewMessageEvent(
        val chatId: Long,
        val text: String)