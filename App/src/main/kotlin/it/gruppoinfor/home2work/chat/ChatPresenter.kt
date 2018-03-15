package it.gruppoinfor.home2work.chat

import it.gruppoinfor.home2work.firebase.NewMessageEvent


interface ChatPresenter {
    fun onResume()

    fun setChatId(chatId: Long)

    fun setRecipientId(recipientId: Long)

    fun getMessageList(): ArrayList<Message>

    fun onNewMessageEvent(newMessageEvent: NewMessageEvent)

    fun sendMessage(message: Message)

    fun onPause()
}