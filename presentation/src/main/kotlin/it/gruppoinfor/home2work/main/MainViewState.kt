package it.gruppoinfor.home2work.main

data class MainViewState(
        var joiningShare: Boolean = false,
        var creatingShare: Boolean = false,
        var shareInProgress: Boolean = false
)