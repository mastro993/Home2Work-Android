package it.gruppoinfor.home2workapi.inbox

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2workapi.model.User


class Conversation : IDialog<Message> {

    @SerializedName("Id")
    @Expose
    var conversationId: String = ""
    @SerializedName("Photo")
    @Expose
    var conversationPhoto: String = ""
    @SerializedName("Name")
    @Expose
    var conversationName: String = ""
    @SerializedName("Users")
    @Expose
    var conversationUsers: ArrayList<User> = ArrayList()
    @SerializedName("LastMessage")
    @Expose
    var conversationLastMsg: Message = Message()
    @SerializedName("UnreadCount")
    @Expose
    var conversationUnreadCnt: Int = 0

    override fun getId(): String {
        return conversationId
    }

    override fun getDialogPhoto(): String {
        return conversationPhoto
    }

    override fun getDialogName(): String {
        return conversationName
    }

    override fun getUsers(): MutableList<out IUser> {
        return conversationUsers
    }

    override fun getLastMessage(): Message {
        return conversationLastMsg
    }

    override fun getUnreadCount(): Int {
        return conversationUnreadCnt
    }

    override fun setLastMessage(message: Message) {
        conversationLastMsg = message
    }


}