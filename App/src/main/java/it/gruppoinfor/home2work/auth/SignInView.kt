package it.gruppoinfor.home2work.auth

interface SignInView {
    fun onLoginSuccess()
    fun onError()
    fun showErrorMessage(errorMessage: String)
}