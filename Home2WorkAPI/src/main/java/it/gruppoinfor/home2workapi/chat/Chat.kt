package it.gruppoinfor.home2workapi.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable


class Chat : IDialog<Message>, Serializable {

    @SerializedName("Id")
    @Expose
    var chatId: Long = 0L
    @SerializedName("User1")
    @Expose
    var user1: Author = Author()
    @SerializedName("User2")
    @Expose
    var user2: Author = Author()
    @SerializedName("LastMessage")
    @Expose
    var lastMsg: Message = Message()
    @SerializedName("UnreadCount")
    @Expose
    var unreadCnt: Int = 0

    override fun getId(): String {
        return chatId.toString()
    }

    override fun getDialogPhoto(): String {
        return when (HomeToWorkClient.user?.id) {
            user1.id -> user2.avatar
            else -> user1.avatar
        }
    }

    override fun getDialogName(): String {
        return when (HomeToWorkClient.user?.id) {
            user1.id -> user2.name
            else -> user1.name
        }
    }

    override fun getUsers(): List<IUser> {
        return when (HomeToWorkClient.user?.id) {
            user1.id -> listOf(user2)
            else -> listOf(user1)
        }
    }

    override fun getLastMessage(): Message {
        return lastMsg
    }

    override fun getUnreadCount(): Int {
        return unreadCnt
    }

    override fun setLastMessage(message: Message) {
        lastMsg = message
    }


}