package it.gruppoinfor.home2workapi.callback

interface ResponseCallback {
    fun onSuccess()
    fun onError(errorCode: Int)
}
