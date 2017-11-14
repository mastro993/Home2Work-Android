package it.gruppoinfor.home2work.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import it.gruppoinfor.home2work.services.RouteService;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Federico on 10/01/2017.
 * Permette di salvare la sessione dell'utente nelle preferenze per un login rapido
 */

public class SessionManager {

    public static final String AUTH_CODE = "auth_code";
    private final SharedPreferences prefs;
    private final Context context;
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";
    private final String KEY_LAST_LOGIN = "lastLogin";
    private final String KEY_USER = "user";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("it.fleetup.app.session", Context.MODE_PRIVATE);
    }

    public void storeSession(final User signedUser) {

        Gson gson = new Gson();

        // Salva le informazioni in modo persistente
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, signedUser.getEmail());
        editor.putString(KEY_TOKEN, signedUser.getToken());
        editor.putLong(KEY_LAST_LOGIN, System.currentTimeMillis());
        editor.putString(KEY_USER, gson.toJson(signedUser));
        editor.apply();

        // TODO Controlla il token Firebase Cloud Messaging
        /* String fcmToken = FirebaseInstanceId.getInstance().getToken();

        if (!account.getFcmToken().equals(fcmToken)) {
            account.setFcmToken(fcmToken);
            Client.getAPI().updateUser(account).enqueue(new retrofit2.SessionManagerCallback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {

                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {

                }
            });
        }*/
    }

    public void checkSession(final SessionManagerCallback callback) {

        if (Client.getSignedUser() != null) {
            callback.onValidSession();
        }

        if (!isUserSignedIn()) {
            callback.onInvalidSession(AuthCode.NO_SESSION);
        } else {

            Long lastLoginTime = prefs.getLong(KEY_LAST_LOGIN, 0);
            Long currentTime = System.currentTimeMillis();
            Long maxTimeDifference = 6L * 60L * 60L * 1000L;

            if ((currentTime - lastLoginTime) < maxTimeDifference) {

                // Sono passate meno di 6 ore dall'ultimo controllo, riutilizzo la sessione salvata

                Gson gson = new Gson();
                User storedUser = gson.fromJson(prefs.getString(KEY_USER, null), User.class);
                Client.setSignedUser(storedUser);
                callback.onValidSession();

            } else {

                // Sono passate piu' di 6 ore, controllo la sessione con il server

                String email = prefs.getString(KEY_EMAIL, null);
                String password = prefs.getString(KEY_TOKEN, null);

                Client.getAPI().login(email, password).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        switch (response.code()) {
                            case 200:
                                User user = response.body();
                                storeSession(user);
                                Client.setSignedUser(user);
                                callback.onValidSession();
                                break;
                            default:
                                callback.onInvalidSession(AuthCode.EXPIRED_TOKEN);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        callback.onInvalidSession(AuthCode.EXPIRED_TOKEN);
                    }
                });

            }

        }
    }

    public void signOutUser() {

        // Elimina tutte le preferenze (com.infor.fleetupht.session)
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Termina i servizi
        context.stopService(new Intent(context, RouteService.class));

    }

    private boolean isUserSignedIn() {
        String email = prefs.getString(KEY_EMAIL, null);
        String token = prefs.getString(KEY_TOKEN, null);
        return email != null && token != null;
    }

    public enum AuthCode {
        EXPIRED_TOKEN, SIGNED_OUT, NO_SESSION
    }

    public interface SessionManagerCallback {
        void onValidSession();

        void onInvalidSession(AuthCode code);
    }

}
