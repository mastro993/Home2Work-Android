package it.gruppoinfor.home2workapi.inbox

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IUser


class Thread : IDialog<Message> {

    @SerializedName("tId")
    @Expose
    var threadId: String = ""
    @SerializedName("photo")
    @Expose
    var threadPhoto: String = ""
    @SerializedName("name_")
    @Expose
    var threadName: String = ""
    @SerializedName("users")
    @Expose
    var threadUsers: ArrayList<User> = ArrayList()
    @SerializedName("lastMessage")
    @Expose
    var threadLastMsg: Message = Message()
    @SerializedName("unreadCount")
    @Expose
    var threadUnreadCnt: Int = 0

    override fun getId(): String {
        return threadId
    }

    override fun getDialogPhoto(): String {
        return threadPhoto
    }

    override fun getDialogName(): String {
        return threadName
    }

    override fun getUsers(): MutableList<out IUser> {
        return threadUsers
    }

    override fun getLastMessage(): Message {
        return threadLastMsg
    }

    override fun getUnreadCount(): Int {
        return threadUnreadCnt
    }

    override fun setLastMessage(message: Message) {
        threadLastMsg = message
    }


}