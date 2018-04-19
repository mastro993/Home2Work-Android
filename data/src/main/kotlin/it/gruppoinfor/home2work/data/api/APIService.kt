package it.gruppoinfor.home2work.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIService {

    private const val BASE_URL = "https://hometoworkapi.azurewebsites.net/api/"

    private val httpClient = OkHttpClient.Builder()

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    private val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(APIService.httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(APIService.gson))
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())

    private var retrofit = APIService.builder.build()

    var API_KEY: String? = null

    fun <S> createService(serviceClass: Class<S>): S {
        API_KEY?.let {
            val interceptor = APIAuthenticationInterceptor(it)

            if (!APIService.httpClient.interceptors().contains(interceptor)) {
                APIService.httpClient.addInterceptor(interceptor)

                APIService.builder.client(APIService.httpClient.build())
                APIService.retrofit = APIService.builder.build()
            }

            return APIService.retrofit.create(serviceClass)
        } ?: let {
            throw IllegalStateException("Chiave API non impostata")
        }

    }

}

