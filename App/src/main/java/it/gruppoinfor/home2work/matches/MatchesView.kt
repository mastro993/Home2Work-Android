package it.gruppoinfor.home2work.matches

import it.gruppoinfor.home2workapi.match.Match


interface MatchesView {
    fun setMatches(list: ArrayList<Match>)
    fun onBadgeRefresh(badge: String)
    fun onLoading()
    fun onLoadingError(errorMessage: String)
    fun onRefresh()
    fun onRefreshDone()
    fun showErrorMessage(errorMessage: String)
    fun onEmptyList()
    fun onMatchHidden(position: Int)
    fun onMatchClick(position: Int, match: Match)
    fun onMatchLongClick(position: Int, match: Match)
}