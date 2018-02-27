package it.gruppoinfor.home2workapi.callback

import it.gruppoinfor.home2workapi.model.ClientUser

interface LoginCallback {
    fun onLoginSuccess()
    fun onInvalidCredential()
    fun onError(throwable: Throwable?)
}
