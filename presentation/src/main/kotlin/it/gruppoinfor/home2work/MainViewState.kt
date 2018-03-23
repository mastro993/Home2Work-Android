package it.gruppoinfor.home2work

data class MainViewState(
        var shareInProgress: Boolean = false,
        var homeTabBadge: String = "",
        var ranksTabBadge: String = "",
        var matchTabBadge: String = "",
        var profileTabBadge: String = ""
)