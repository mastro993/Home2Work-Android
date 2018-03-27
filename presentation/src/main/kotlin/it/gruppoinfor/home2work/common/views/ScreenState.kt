package it.gruppoinfor.home2work.common.views


sealed class ScreenState {
    object Loading : ScreenState()
    data class Empty(val emptyMessage: String?) : ScreenState()
    data class Error(val errorMessage: String?) : ScreenState()
    object Done : ScreenState()
}