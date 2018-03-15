package it.gruppoinfor.home2work.chat

import it.gruppoinfor.home2work.firebase.NewMessageEvent
import it.gruppoinfor.home2workapi.chat.Message


interface ChatPresenter {
    fun onResume()

    fun setChatId(chatId: Long)

    fun setRecipientId(recipientId: Long)

    fun getMessageList(): ArrayList<Message>

    fun onNewMessageEvent(newMessageEvent: NewMessageEvent)

    fun sendMessage(message: Message)

    fun onPause()
}