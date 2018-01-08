package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2workapi.interfaces.LoginCallback;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze per un login rapido
 */

public class SessionManager {

    public static final int EXPIRED_TOKEN = 0;
    public static final int ERROR = 1;
    public static final int NO_INTERNET = 2;
    public static final String AUTH_CODE = "auth_code";
    private static final String PREFS_SESSION = "it.fleetup.app.session";
    private final SharedPreferences prefs;
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";
    private SessionManagerCallback mCallback;
    private LoginCallback mLoginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess() {
            storeSession();
            mCallback.onValidSession();
        }

        @Override
        public void onInvalidCredential() {
            mCallback.onExpiredToken();
            signOutUser();
        }

        @Override
        public void onLoginError() {
            mCallback.onExpiredToken();
        }

        @Override
        public void onError(Throwable throwable) {
            mCallback.onError(throwable);
        }
    };

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
    }

    public SessionManager(Context context, SessionManagerCallback sessionManagerCallback) {
        this(context);
        mCallback = sessionManagerCallback;
    }

    public void storeSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, App.home2WorkClient.getUser().getEmail());
        editor.putString(KEY_TOKEN, App.home2WorkClient.getUser().getToken());
        editor.apply();

        String token = FirebaseInstanceId.getInstance().getToken();

        App.home2WorkClient.setFcmToken(token);
    }

    public void loadSession() {

        if (mCallback == null) throw new IllegalStateException("Callback non implementati");

        if (App.home2WorkClient.getUser() != null) {
            mCallback.onValidSession();
            return;
        }

        String email = prefs.getString(KEY_EMAIL, null);
        String token = prefs.getString(KEY_TOKEN, null);

        if (email == null && token == null) {
            mCallback.onNoSession();
            return;
        }

        App.home2WorkClient.login(email, token, true, mLoginCallback);

    }

    public void signOutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public interface SessionManagerCallback {

        void onNoSession();

        void onValidSession();

        void onExpiredToken();

        void onError(Throwable throwable);
    }

}
