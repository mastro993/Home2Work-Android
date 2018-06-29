package it.gruppoinfor.home2work.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Permette l'autenticazione dell'utente tramite un sessionToken
 * Salva il token al primo login e poi lo riutilizza per ogni richiesta
 */
class APIAuthenticationInterceptor(val apiKey: String?) : Interceptor {

    companion object {
        private const val HEADER_SESSION_TOKEN = "X-User-Session-Token"
        private const val HEADER_API_KEY = "X-Api-Key"
        var sessionToken: String? = null

    }


    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val builder = original.newBuilder()

        if (original.headers()[HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            sessionToken?.let {
                builder.addHeader(HEADER_SESSION_TOKEN, it)
            }

        }

        if (original.headers()[HEADER_API_KEY].isNullOrEmpty()) {

            apiKey?.let {
                builder.addHeader(HEADER_API_KEY, it)
            }

        }

        if (original.headers()[HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            sessionToken?.let {
                builder.addHeader(HEADER_SESSION_TOKEN, it)
            }

        }

        val request = builder.build()

        val response = chain.proceed(request)

        if (!response.headers()[HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            sessionToken = response.headers()[HEADER_SESSION_TOKEN]!!

        }

        return response
    }


}