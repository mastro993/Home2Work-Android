package it.gruppoinfor.home2work.entities

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*


data class ChatMessage(
        var messageId: Long,
        var chatId: Long,
        var messageText: String,
        var date: Date,
        var author: Author
) : IMessage {

    override fun getId(): String {
        return messageId.toString()
    }

    override fun getText(): String {
        return messageText
    }

    override fun getCreatedAt(): Date {
        return date
    }

    override fun getUser(): IUser {
        return author
    }

}
