package it.gruppoinfor.home2workapi;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nullable;

import it.gruppoinfor.home2workapi.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home2WorkClient {

    public static final String AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/";
    public static final String COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/";
    public static final String ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/";

    public static User User;
    public EndpointInterface API;

    public Home2WorkClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://home2workapi.azurewebsites.net/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        API = retrofit.create(EndpointInterface.class);
    }

}
