package it.gruppoinfor.home2workapi.auth

interface AuthCallback {
    fun onSuccess()
    fun onInvalidCredential()
    fun onError(throwable: Throwable?)
}
