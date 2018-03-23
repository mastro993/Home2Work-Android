package it.gruppoinfor.home2work.entities

import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser


data class Chat(
        var id: Long,
        var author: Author,
        var lastMsg: ChatMessage,
        var unreadCnt: Int = 0
) : IDialog<ChatMessage> {


    override fun getId(): String {
        return "$id"
    }

    override fun getDialogPhoto(): String {
        return author.avatar
    }

    override fun getDialogName(): String {
        return author.name
    }

    override fun getUsers(): List<IUser> {
        return listOf(author)
    }

    override fun getLastMessage(): ChatMessage {
        return lastMsg
    }

    override fun getUnreadCount(): Int {
        return unreadCnt
    }

    override fun setLastMessage(message: ChatMessage) {
        lastMsg = message
    }


}