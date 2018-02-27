package it.gruppoinfor.home2workapi

import android.text.TextUtils
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


internal object ServiceGenerator {

    private const val API_BASE_URL = "http://home2workapi.azurewebsites.net/api/"

    private val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private var retrofit = builder.build()

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, "")
    }

    fun <S> createService(serviceClass: Class<S>, sessionToken: String): S {
        if (!TextUtils.isEmpty(sessionToken)) {
            val interceptor = AuthenticationInterceptor(sessionToken)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }

        return retrofit.create(serviceClass)
    }
}