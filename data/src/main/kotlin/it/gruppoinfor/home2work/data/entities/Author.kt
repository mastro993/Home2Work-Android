package it.gruppoinfor.home2work.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2work.data.AVATAR_BASE_URL

class Author : IUser {

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
        return "${AVATAR_BASE_URL}$id.jpg"
    }

    override fun getId(): String {
        return id.toString()
    }

    override fun getName(): String {
        return userName
    }
}