package it.gruppoinfor.home2work.signin


data class SignInViewState(
        var isLoading: Boolean = false,
        var savedEmail: String? = null
)