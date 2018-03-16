package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2work.chat.ChatMessageEntity


data class ChatData(
        @SerializedName("Id") val id: Long = -1,
        @SerializedName("User1") val user_1: UserData,
        @SerializedName("User2") val user_2: UserData,
        @SerializedName("LastMessage") val lastMsg: ChatMessageData? = null,
        @SerializedName("UnreadCount") val unreadCnt: Int = -1
)