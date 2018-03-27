package it.gruppoinfor.home2work.inbox

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.Chat


data class InboxViewState(
        var isRefreshing: Boolean = false,
        var screenState: ScreenState? = null,
        var chatList: List<Chat>? = null
)