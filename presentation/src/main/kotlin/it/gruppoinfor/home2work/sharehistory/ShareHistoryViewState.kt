package it.gruppoinfor.home2work.sharehistory

import it.gruppoinfor.home2work.common.views.ScreenState
import it.gruppoinfor.home2work.entities.Share


data class ShareHistoryViewState(
        var screenState: ScreenState? = null,
        var isRefreshing: Boolean = false,
        var sharesHistory: List<Share>? = null
)