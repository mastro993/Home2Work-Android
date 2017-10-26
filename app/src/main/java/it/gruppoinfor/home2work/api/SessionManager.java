package it.gruppoinfor.home2work.api;

import android.content.Context;
import android.content.SharedPreferences;

import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.models.User;
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
    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_TOKEN = "token";
    private final String KEY_EMAIL = "email";
    private final String KEY_ID = "id";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("it.fleetup.app.session", Context.MODE_PRIVATE);
    }

    public void storeSession(final User signedUser) {

        // Salva le informazioni in modo persistente
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, signedUser.getEmail());
        editor.putString(KEY_TOKEN, signedUser.getToken());
        editor.putLong(KEY_ID, signedUser.getId());
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

    public User loadSession() {
        if (!isUserSignedIn())
            return null;
        User user = new User();
        user.setId(prefs.getLong(KEY_ID, 0));
        user.setEmail(prefs.getString(KEY_EMAIL, null));
        user.setToken(prefs.getString(KEY_TOKEN, null));
        return user;
    }

    public void checkSession(final SessionManagerCallback callback) {

        if (!isUserSignedIn()) {
            callback.onInvalidSession(AuthCode.NO_SESSION);
        } else {

            String email = prefs.getString(KEY_EMAIL, "");
            String password = prefs.getString(KEY_TOKEN, "");

            Client.getAPI().login(email, password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(retrofit2.Call<User> call, Response<User> response) {
                    switch (response.code()) {
                        case 200:
                            User user = response.body();
                            storeSession(user);
                            callback.onValidSession(user);
                            break;
                        default:
                            callback.onInvalidSession(AuthCode.EXPIRED_TOKEN);
                            break;
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<User> call, Throwable t) {
                    callback.onInvalidSession(AuthCode.EXPIRED_TOKEN);
                }
            });
        }
    }

    public void signOutUser() {

        // Elimina tutte le preferenze (com.infor.fleetupht.session)
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Termina i servizi
        // TODO context.stopService(new Intent(context, RouteService.class));

    }

    public boolean isUserSignedIn() {
        return prefs.getBoolean(IS_LOGIN, false);
    }

    public enum AuthCode {
        EXPIRED_TOKEN, SIGNED_OUT, NO_SESSION
    }

    public interface SessionManagerCallback {
        void onValidSession(User account);

        void onInvalidSession(AuthCode code);
    }

}
