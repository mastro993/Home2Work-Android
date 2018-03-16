package it.gruppoinfor.home2work.data.entities

import com.google.gson.annotations.SerializedName
import java.util.*


data class ChatMessageData(
        @SerializedName("Id") var id: Long = -1L,
        @SerializedName("ChatId") var chatId: Long = -1L,
        @SerializedName("Text") var text: String,
        @SerializedName("Time") var date: Date,
        @SerializedName("Sender") var author: UserData
)