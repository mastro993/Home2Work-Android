package it.gruppoinfor.home2work.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.gruppoinfor.home2work.models.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static final String SERVER_URL = "http://federicomastriniunipr.altervista.org/fleetup";
    public static final String API_BASE = SERVER_URL + "/V2/";
    public static final String AVATAR_BASE_URL = SERVER_URL + "/images/avatar/";
    public static final String COMPANIES_BASE_URL = SERVER_URL + "/images/companies/";
    public static final String ACHIEVEMENTS_BASE_URL = SERVER_URL + "/images/achievements/";

    private static User signedUser;
    private static EndpointInterface APIService;

    public static User getUser() {
        return signedUser;
    }

    public static void setUser(User user) {
        signedUser = user;
    }

    public static void init() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE)
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
        public APINotInitializedException() {
            super("FleetUpClient non inizializzato");
        }
    }

}
