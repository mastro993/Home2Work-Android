package it.gruppoinfor.home2workapi;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.Nullable;

import it.gruppoinfor.home2workapi.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    public static final String AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/";
    public static final String COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/";
    public static final String ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/";

    @Nullable
    public static User User;
    private static EndpointInterface APIService;

    public static void init() {
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
                .build();

        APIService = retrofit.create(EndpointInterface.class);
    }

    public static EndpointInterface getAPI() throws APINotInitializedException {
        if (APIService == null)
            throw new APINotInitializedException();
        return APIService;
    }

    public static class APINotInitializedException extends RuntimeException {
        APINotInitializedException() {
            super("Client non inizializzato");
        }
    }

}
