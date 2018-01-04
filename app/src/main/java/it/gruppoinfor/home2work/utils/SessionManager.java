package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2workapi.Home2WorkClient;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze per un login rapido
 */

public class SessionManager {

    public static final int EXPIRED_TOKEN = 0;
    public static final int ERROR = 1;
    public static final String AUTH_CODE = "auth_code";
    private static final String PREFS_SESSION = "it.fleetup.app.session";
    private final SharedPreferences prefs;
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";
    private Home2WorkClient home2WorkClient;

    public SessionManager(Context context) {
        home2WorkClient = new Home2WorkClient();
        prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
    }

    public void storeSession() {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, Home2WorkClient.User.getEmail());
        editor.putString(KEY_TOKEN, Home2WorkClient.User.getToken());
        editor.apply();

        String token = FirebaseInstanceId.getInstance().getToken();

        home2WorkClient.API
                .setFCMToken(Home2WorkClient.User.getId(), token)
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void checkSession(final SessionManagerCallback callback) {

        if (Home2WorkClient.User != null) {
            callback.onValidSession();
            return;
        }

        if (!isUserSignedIn()) {
            callback.onNoSession();
            return;
        }

        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_TOKEN, null);

        home2WorkClient.API.login(email, password, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    switch (userResponse.code()) {
                        case 200:
                            Home2WorkClient.User = userResponse.body();
                            storeSession();
                            callback.onValidSession();
                            break;
                        default:
                            callback.onExpiredToken();
                            signOutUser();
                            break;
                    }
                }, throwable -> callback.onError());

    }

    public void signOutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private boolean isUserSignedIn() {
        String email = prefs.getString(KEY_EMAIL, null);
        String token = prefs.getString(KEY_TOKEN, null);
        return email != null && token != null;
    }

    public interface SessionManagerCallback {

        void onNoSession();

        void onValidSession();

        void onExpiredToken();

        void onError();
    }

}
