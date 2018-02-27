package it.gruppoinfor.home2workapi

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


internal class AuthenticationInterceptor(private val sessionToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        var builder = original.newBuilder()

        if (original.headers()[HomeToWorkClient.HEADER_SESSION_TOKEN].isNullOrEmpty()) {

            builder = original.newBuilder().addHeader(HomeToWorkClient.HEADER_SESSION_TOKEN, sessionToken)

        }

        val request = builder.build()

        return chain.proceed(request)
    }
}