package it.gruppoinfor.home2work.match

import it.gruppoinfor.home2work.common.ScreenState
import it.gruppoinfor.home2work.entities.Match


data class MatchViewState(
        var screenState: ScreenState? = null,
        var isRefreshing: Boolean = false,
        var matches: List<Match>? = null,
        var isLightMatches: Boolean = false,
        var badgeCount: String = ""
)