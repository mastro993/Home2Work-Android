package it.gruppoinfor.home2work.data.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import it.gruppoinfor.home2work.api.AuthenticationInterceptor
import it.gruppoinfor.home2work.api.RxErrorHandlingCallAdapterFactory
import it.gruppoinfor.home2work.data.API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


internal object ServiceGenerator {

    private const val BASE_URL = "$API_BASE_URL/api/"

    private val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(ServiceGenerator.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(ServiceGenerator.gson))
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())

    private var retrofit = ServiceGenerator.builder.build()

    fun <S> createService(serviceClass: Class<S>): S {
        val interceptor = AuthenticationInterceptor()

        if (!ServiceGenerator.httpClient.interceptors().contains(interceptor)) {
            ServiceGenerator.httpClient.addInterceptor(interceptor)

            ServiceGenerator.builder.client(ServiceGenerator.httpClient.build())
            ServiceGenerator.retrofit = ServiceGenerator.builder.build()
        }

        return ServiceGenerator.retrofit.create(serviceClass)
    }
}