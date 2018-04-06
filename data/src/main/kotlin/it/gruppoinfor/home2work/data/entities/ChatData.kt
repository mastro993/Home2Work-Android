package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName


data class ChatData(
        @SerializedName("Id") val id: Long = -1,
        @SerializedName("User") val user: UserData,
        @SerializedName("LastMessage") val lastMsg: ChatMessageData,
        @SerializedName("UnreadCount") val unreadCnt: Int = -1
)