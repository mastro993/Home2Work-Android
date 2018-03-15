package it.gruppoinfor.home2work.match


interface MatchesPresenter {
    fun onViewCreated()
    fun onRefresh()
    fun onPause()
    fun hideMatch(match: Match)
    fun setMatchAsViewed(match: Match)
}