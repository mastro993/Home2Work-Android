package it.gruppoinfor.home2work;

import android.content.Context;
import android.content.SharedPreferences;

import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.api.Account;
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
    private final String KEY_NAME = "name";
    private final String KEY_SURNAME = "surname";
    private final String KEY_ID = "id";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("it.fleetup.app.session", Context.MODE_PRIVATE);
    }

    public void storeSession(final Account account) {

        // Salva le informazioni in modo persistente
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, account.getEmail());
        editor.putString(KEY_TOKEN, account.getToken());
        editor.putString(KEY_NAME, account.getName());
        editor.putString(KEY_SURNAME, account.getSurname());
        editor.putLong(KEY_ID, account.getId());
        editor.apply();

        // TODO Controlla il token Firebase Cloud Messaging
        /* String fcmToken = FirebaseInstanceId.getInstance().getToken();

        if (!account.getFcmToken().equals(fcmToken)) {
            account.setFcmToken(fcmToken);
            APIClient.API().updateUser(account).enqueue(new retrofit2.Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {

                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {

                }
            });
        }*/
    }

    public Account loadSession() {
        if (!isUserSignedIn())
            return null;
        Account account = new Account();
        account.setEmail(prefs.getString(KEY_EMAIL, null));
        account.setId(prefs.getLong(KEY_ID, 0));
        account.setToken(prefs.getString(KEY_TOKEN, null));
        account.setName(prefs.getString(KEY_NAME, null));
        account.setSurname(prefs.getString(KEY_SURNAME, null));
        return account;
    }

    public void checkSession(final SessionManager.Callback callback) {

        if (!isUserSignedIn()) {
            callback.onInvalidSession(AuthCode.NO_SESSION);
        } else {

            String email = prefs.getString(KEY_EMAIL, "");
            String password = prefs.getString(KEY_TOKEN, "");

            APIClient.API().login(email, password).enqueue(new retrofit2.Callback<Account>() {
                @Override
                public void onResponse(retrofit2.Call<Account> call, Response<Account> response) {
                    switch (response.code()) {
                        case 200:
                            Account account = response.body();
                            storeSession(account);
                            callback.onValidSession(account);
                            break;
                        default:
                            callback.onInvalidSession(AuthCode.EXPIRED_TOKEN);
                            break;
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Account> call, Throwable t) {
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

    public interface Callback {
        void onValidSession(Account account);

        void onInvalidSession(AuthCode code);
    }

}
