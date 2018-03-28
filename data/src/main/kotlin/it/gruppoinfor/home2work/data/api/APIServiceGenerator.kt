package it.gruppoinfor.home2work.data.api

import com.google.gson.GsonBuilder
import it.gruppoinfor.home2work.api.RxErrorHandlingCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIServiceGenerator {

    private const val BASE_URL = "https://hometoworkapi.azurewebsites.net/api/"

    private val httpClient = OkHttpClient.Builder()

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(APIServiceGenerator.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(APIServiceGenerator.gson))
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())

    private var retrofit = APIServiceGenerator.builder.build()

    fun <S> createService(serviceClass: Class<S>): S {
        val interceptor = APIAuthenticationInterceptor()

        if (!APIServiceGenerator.httpClient.interceptors().contains(interceptor)) {
            APIServiceGenerator.httpClient.addInterceptor(interceptor)

            APIServiceGenerator.builder.client(APIServiceGenerator.httpClient.build())
            APIServiceGenerator.retrofit = APIServiceGenerator.builder.build()
        }

        return APIServiceGenerator.retrofit.create(serviceClass)
    }

}

