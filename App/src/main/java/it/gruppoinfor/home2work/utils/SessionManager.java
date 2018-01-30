package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.gruppoinfor.home2work.services.LocationService;
import it.gruppoinfor.home2work.services.SyncService;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.interfaces.LoginCallback;
import it.gruppoinfor.home2workapi.model.User;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze e di ripristinarla all'occorrenza (se non scaduta)
 */

public class SessionManager {

    public static final String AUTH_CODE = "auth_code";
    private static final String PREFS_SESSION = "it.home2work.app.session";
    private static final String PREFS_TOKEN = "token";
    private static final String PREFS_EMAIL = "email";

    public static void storeSession(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREFS_EMAIL, user.getEmail());
        editor.putString(PREFS_TOKEN, user.getToken());
        editor.apply();

        String token = FirebaseInstanceId.getInstance().getToken();

        HomeToWorkClient.getInstance().setFcmToken(token);
    }

    public static void loadSession(Context context, @NotNull SessionManager.SessionCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);

        if (HomeToWorkClient.getUser() != null) {
            callback.onValidSession();
            return;
        }

        String email = prefs.getString(PREFS_EMAIL, null);
        String token = prefs.getString(PREFS_TOKEN, null);

        if (email == null && token == null) {
            callback.onInvalidSession(0, null);
            return;
        }

        HomeToWorkClient.getInstance().login(email, token, true, new LoginCallback() {
            @Override
            public void onLoginSuccess() {
                callback.onValidSession();
            }

            @Override
            public void onInvalidCredential() {
                clearSession(context);
                callback.onInvalidSession(1, null);
            }

            @Override
            public void onLoginError() {
                callback.onInvalidSession(2, null);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onInvalidSession(2, throwable);
            }
        });
    }

    public static void clearSession(Context context) {
        context.stopService(new Intent(context, LocationService.class));
        context.stopService(new Intent(context, SyncService.class));

        SharedPreferences prefs = context.getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public interface SessionCallback {

        /**
         * Sessione trovata e convalidata
         */
        void onValidSession();

        /**
         * Sessione non trovata o non valida
         *
         * @param code      0: Nessuna sessione, 1: Token non valido, 2: Errore del server
         * @param throwable Throwable opzionale in caso di errore
         */
        void onInvalidSession(int code, @Nullable Throwable throwable);

    }

}
