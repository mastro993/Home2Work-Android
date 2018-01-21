package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.services.SyncService;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.interfaces.LoginCallback;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze per un login rapido
 */

public class SessionManager implements LoginCallback {

    public static final int EXPIRED_TOKEN = 0;
    public static final int ERROR = 1;
    public static final int NO_INTERNET = 2;
    public static final String AUTH_CODE = "auth_code";
    private static final String PREFS_SESSION = "it.fleetup.app.session";
    private final SharedPreferences prefs;
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";
    private SessionManagerCallback mCallback;
    private Context mContext;


    public SessionManager(Context context) {
        mContext = context;
        prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
    }

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

    public void storeSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, HomeToWorkClient.getUser().getEmail());
        editor.putString(KEY_TOKEN, HomeToWorkClient.getUser().getToken());
        editor.apply();

        String token = FirebaseInstanceId.getInstance().getToken();

        HomeToWorkClient.getInstance().setFcmToken(token);
    }

    public void loadSession(SessionManagerCallback sessionManagerCallback) {
        mCallback = sessionManagerCallback;

        if (mCallback == null) throw new IllegalStateException("Callback non implementati");

        if (HomeToWorkClient.getUser() != null) {
            mCallback.onValidSession();
            return;
        }

        String email = prefs.getString(KEY_EMAIL, null);
        String token = prefs.getString(KEY_TOKEN, null);

        if (email == null && token == null) {
            mCallback.onNoSession();
            return;
        }

        HomeToWorkClient.getInstance().login(email, token, true, this);

    }

    public void signOutUser() {
        mContext.stopService(new Intent(mContext, LocationService.class));
        mContext.stopService(new Intent(mContext, SyncService.class));

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
