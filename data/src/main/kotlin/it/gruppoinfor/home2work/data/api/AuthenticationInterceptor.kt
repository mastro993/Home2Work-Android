package it.gruppoinfor.home2work.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Permette l'autenticazione dell'utente tramite un sessionToken
 * Salva il token al primo login e poi lo riutilizza per ogni richiesta
 */
internal class AuthenticationInterceptor : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val builder = original.newBuilder()

        if (original.headers()[HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            builder.addHeader(HEADER_SESSION_TOKEN, sessionToken)

        }

        val request = builder.build()

        val response = chain.proceed(request)

        if (!response.headers()[HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            sessionToken = response.headers()[HEADER_SESSION_TOKEN]!!

        }

        return response
    }

    companion object {
        private const val HEADER_SESSION_TOKEN = "X-User-Session-Token"
        private var sessionToken: String = ""
    }
}