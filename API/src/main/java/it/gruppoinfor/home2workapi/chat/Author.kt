package it.gruppoinfor.home2workapi.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable

class Author : IUser, Serializable {

    @SerializedName("Id")
    @Expose
    var id: Long = 0
    @SerializedName("Name")
    @Expose
    var userName: String = ""

    constructor()

    constructor(id: Long?) {
        this.id = id!!
    }

    override fun getAvatar(): String {
        return "${HomeToWorkClient.AVATAR_BASE_URL}$id.jpg"
    }

    override fun getId(): String {
        return id.toString()
    }

    override fun getName(): String {
        return userName
    }
}