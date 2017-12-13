package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;

import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.services.SyncService;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze per un login rapido
 */

public class SessionManager {

    public static final String AUTH_CODE = "auth_code";
    public static final int EXPIRED_TOKEN = 0;
    public static final int ERROR = 1;

    private final SharedPreferences prefs;
    private final Context context;
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";

    private SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("it.fleetup.app.session", Context.MODE_PRIVATE);
    }

    public static SessionManager with(Context context) {
        return new SessionManager(context);
    }

    public void storeSession(final User signedUser) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, signedUser.getEmail());
        editor.putString(KEY_TOKEN, signedUser.getToken());
        editor.apply();

        String token = FirebaseInstanceId.getInstance().getToken();
        Client.getAPI().setFCMToken(signedUser.getId(), token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void checkSession(final SessionManagerCallback callback) {

        if (Client.User != null) {
            callback.onValidSession();
            return;
        }

        if (!isUserSignedIn()) {
            callback.onNoSession();
            return;
        }

        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_TOKEN, null);

        Client.getAPI().login(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                switch (response.code()) {
                    case 200:
                        User user = response.body();
                        storeSession(user);
                        Client.User = user;
                        callback.onValidSession();
                        break;
                    default:
                        callback.onExpiredToken();
                        signOutUser();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError();
            }

        });
    }

    public void signOutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        context.stopService(new Intent(context, LocationService.class));
        context.stopService(new Intent(context, SyncService.class));
    }

    private boolean isUserSignedIn() {
        String email = prefs.getString(KEY_EMAIL, null);
        String token = prefs.getString(KEY_TOKEN, null);
        return email != null && token != null;
    }

    public static class SessionManagerCallback {

        public void onNoSession() {
            MyLogger.v("SessionManager", "onNoSession fired but not implemented");
        }

        public void onValidSession() {
            MyLogger.v("SessionManager", "onValidSession fired but not implemented");
        }

        public void onExpiredToken() {
            MyLogger.v("SessionManager", "onInvalidSessions fired but not implemented");
        }

        public void onError() {
            MyLogger.v("SessionManager", "onError fired but not implemented");
        }
    }

}
