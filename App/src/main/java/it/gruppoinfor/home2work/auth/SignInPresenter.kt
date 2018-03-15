package it.gruppoinfor.home2work.auth

interface SignInPresenter {
    fun onResume()
    fun onPause()
    fun login(email: String, password: String)
}