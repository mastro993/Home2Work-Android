package it.gruppoinfor.home2work.chat

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.ChatMessage


data class ChatViewState(
        var screenState: ScreenState? = null,
        var messageList: List<ChatMessage>? = null
)