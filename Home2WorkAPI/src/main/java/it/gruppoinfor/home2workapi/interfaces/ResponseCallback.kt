package it.gruppoinfor.home2workapi.interfaces

interface ResponseCallback {
    fun onSuccess()
    fun onError(errorCode: Int)
}
