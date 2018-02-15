package it.gruppoinfor.home2workapi.interfaces

import it.gruppoinfor.home2workapi.model.User

interface LoginCallback {
    fun onLoginSuccess(user: User)
    fun onInvalidCredential()
    fun onLoginError()
    fun onError(throwable: Throwable)
}
