package it.gruppoinfor.home2work.singlechat

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.ChatMessage


data class SingleChatViewState(
        var screenState: ScreenState? = null,
        var messageList: List<ChatMessage>? = null
)