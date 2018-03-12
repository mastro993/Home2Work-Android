package it.gruppoinfor.home2work.matches

import it.gruppoinfor.home2workapi.match.Match


interface MatchesPresenter {
    fun onViewCreated()
    fun onRefresh()
    fun onPause()
    fun hideMatch(match: Match)
    fun setMatchAsViewed(match: Match)
}