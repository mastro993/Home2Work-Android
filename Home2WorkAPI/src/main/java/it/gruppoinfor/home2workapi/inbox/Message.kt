package it.gruppoinfor.home2workapi.inbox

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2workapi.model.User
import java.util.*


class Message : IMessage {

    @SerializedName("mId")
    @Expose
    var messageId: String = ""
    @SerializedName("text")
    @Expose
    var messageText: String = ""
    @SerializedName("date")
    @Expose
    var messageDate: Date = Date()
    @SerializedName("user")
    @Expose
    var messageUser: User = User()

    override fun getId(): String {
        return messageId
    }

    override fun getText(): String {
        return messageText
    }

    override fun getCreatedAt(): Date {
        return messageDate
    }

    override fun getUser(): IUser {
        return messageUser
    }

}
