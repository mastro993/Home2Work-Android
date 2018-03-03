package it.gruppoinfor.home2workapi.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.io.Serializable
import java.util.*


class Message : IMessage, Serializable {

    @SerializedName("Id")
    @Expose
    var messageId: String = ""
    @SerializedName("Text")
    @Expose
    var messageText: String = ""
    @SerializedName("Time")
    @Expose
    var messageDate: Date = Date()
    @SerializedName("Sender")
    @Expose
    var messageUser: Author = Author()

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
