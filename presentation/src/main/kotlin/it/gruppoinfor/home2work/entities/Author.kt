package it.gruppoinfor.home2work.entities

import com.stfalcon.chatkit.commons.models.IUser

data class Author(
        var user: User
) : IUser {

    override fun getAvatar(): String {
        return user.avatarUrl
    }

    override fun getId(): String {
        return user.id.toString()
    }

    override fun getName(): String {
        return user.fullName
    }
}