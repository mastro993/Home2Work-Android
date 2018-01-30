package it.gruppoinfor.home2workapi;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2workapi.interfaces.LoginCallback;
import it.gruppoinfor.home2workapi.model.Company;
import it.gruppoinfor.home2workapi.model.Guest;
import it.gruppoinfor.home2workapi.model.Location;
import it.gruppoinfor.home2workapi.model.Match;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeToWorkClient {

    public static final String AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/";
    public static final String COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/";
    public static final String ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/";
    private static final String API_BASE_URL = "http://home2workapi.azurewebsites.net/api/";
    private static HomeToWorkClient sInstance;
    private static User sUser;
    private EndpointInterface mAPI;

    private HomeToWorkClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mAPI = retrofit.create(EndpointInterface.class);
    }

    public static void init() {
        sInstance = new HomeToWorkClient();
    }

    public static HomeToWorkClient getInstance() {
        return sInstance;
    }

    /**
     * Ottiene l'utente attualmente autenticato nel client
     *
     * @return User utente autenticato
     */
    public static User getUser() {
        return sUser;
    }

    /**
     * Effettua il login di un utente con le credenziali passate
     *
     * @param email           String Email utente
     * @param password        String Pasword dell'utente
     * @param isPasswordToken boolean Flag per indicare se si sta accedendo con un token (true) oppure con la password (false)
     * @param loginCallback   LoginCallback Callback per il login
     */
    public void login(String email, String password, boolean isPasswordToken, LoginCallback loginCallback) {
        mAPI.login(email, password, isPasswordToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    switch (userResponse.code()) {
                        case 404:
                            loginCallback.onInvalidCredential();
                            break;
                        case 200:
                            sUser = userResponse.body();
                            loginCallback.onLoginSuccess();
                            break;
                        default:
                            loginCallback.onLoginError();
                            break;
                    }
                }, loginCallback::onError);
    }

    /**
     * Imposta nel server il token unico per la FMC Platform
     *
     * @param fcmToken Token generato per Firebase Messaging Cloud platform
     */
    public void setFcmToken(String fcmToken) {
        mAPI.setFCMToken(sUser.getId(), fcmToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // .. nulla
                }, Throwable::printStackTrace);
    }

    /**
     * Carica l'avatar sul server
     *
     * @param avatarBody        Immagine avatar
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo
     * @param onFailureListener Callback in caso di errori del server
     */
    public void uploadAvatar(MultipartBody.Part avatarBody, OnSuccessListener<ResponseBody> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.uploadAvatar(sUser.getId(), avatarBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 201)
                        onSuccessListener.onSuccess(response.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + response.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Ottiene la lista dei match dell'utente
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce una lista di match
     * @param onFailureListener Callback in caso di errori del server
     */
    public void getUserMatches(OnSuccessListener<List<Match>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getMatches(sUser.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + listResponse.code()));
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        onFailureListener.onFailure(new Exception(throwable));
                    }
                });
    }

    /**
     * Modifica le informazioni di un match
     *
     * @param match             Match aggiornato da salvarne in cambiamenti sul server
     * @param onSuccessListener Callback in caso di modifica avvenuta con successo. Restituisce il match aggiornato
     * @param onFailureListener Callback in caso di errori del server
     */
    public void editMatch(Match match, OnSuccessListener<Match> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.editMatch(match)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(matchResponse -> {
                    if (matchResponse.code() == 200)
                        onSuccessListener.onSuccess(matchResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + matchResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Modifica le informazioni di un match (Metodo senza callbacks)
     *
     * @param match Match aggiornato da salvarne in cambiamenti sul server
     */
    public void editMatch(Match match) {
        mAPI.editMatch(match)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * Crea una nuova condivisione sul server e la avvia
     *
     * @param onSuccessListener Callback in caso di creazione avvenuta con successo. Restituisce la condivisione appena creata
     * @param onFailureListener Callback in caso di errori del server
     */
    public void createShare(OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.createShare(sUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Annulla una condivisione
     *
     * @param share             Condivisione da annullare
     * @param onSuccessListener Callback in caso di eliminazione avvenuta con successo.
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void cancelShare(Share share, OnSuccessListener<ResponseBody> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.cancelShare(share.getId(), sUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Abbandona una condivisione alla quale si sta partecipando come ospiti.
     *
     * @param share             Condivisione da abbandonare
     * @param onSuccessListener Callback in caso di abbandono avvenuto con successo. Restituisce la condivisione aggiornata.
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void leaveShare(Share share, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.leaveShare(share.getId(), sUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Espelle l'utente dalla condivisione nella quale si è host
     *
     * @param share             Condivisione
     * @param guest             Utente da espellere
     * @param onSuccessListener Callback in caso di espulsione avvenuta con successo. Restituisce la condivisione aggiornata
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void expelGuest(Share share, Guest guest, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.leaveShare(share.getId(), guest.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Ottiene informazioni su una condivisione con l'ID fornito
     *
     * @param sID               ID della condivisione
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la condivisione ottenuta
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void getShare(Long sID, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getShare(sID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Aggiorna le informazioni dell'utente collegato.
     *
     * @param onSuccessListener Callback in caso di aggiornato avvenuto con successo.
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void updateUser(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.updateUser(sUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 200) {
                        sUser = response.body();
                        onSuccessListener.onSuccess(null);
                    } else
                        onFailureListener.onFailure(new Exception("Response code " + response.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Ottiene la lista delle aziende presenti nel sistema.
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la lista delle aziende
     */
    public void getCompanies(OnSuccessListener<List<Company>> onSuccessListener) {
        mAPI.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companiesResponse -> {
                    if (companiesResponse.code() == 200)
                        onSuccessListener.onSuccess(companiesResponse.body());
                }, Throwable::printStackTrace);
    }

    /**
     * Aggiorna l'utente collegato.
     *
     * @param onSuccessListener Callback in caso di aggiornamento avvenuto con successo
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void refreshUser(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getUser(sUser.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    if (userResponse.code() == 200) {
                        sUser = userResponse.body();
                        onSuccessListener.onSuccess(null);
                    } else
                        onFailureListener.onFailure(new Exception("Response code: " + userResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    /**
     * Ottiene la lista delle condivisioni dell'utente, in corso e completate.
     *
     * @param onSuccessListener Callback in caso di caricamento avvenuto con successo. Restituisce la lista delle condivisioni.
     * @param onFailureListener Callback in caso di errori del server.
     */
    public void getUserShares(OnSuccessListener<List<Share>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getShares(sUser.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + listResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void joinShare(Long shareId, android.location.Location joinLocation, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        String locationString = joinLocation.getLatitude() + "," + joinLocation.getLongitude();
        mAPI.joinShare(shareId, sUser.getId(), locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + responseBodyResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void completeShare(Share share, android.location.Location joinLocation, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        String locationString = joinLocation.getLatitude() + "," + joinLocation.getLongitude();
        mAPI.completeShare(share.getId(), sUser.getId(), locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + responseBodyResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void finishShare(Share share, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.finishShare(share.getId(), sUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + responseBodyResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void uploadLocation(long userId, List<Location> locationList, OnSuccessListener<List<Location>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.uploadLocations(userId, locationList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResponse -> {
                    if (listResponse.code() == 200)
                        onSuccessListener.onSuccess(listResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code" + listResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public Observable<Response<User>> getUser(Long userId) {
        return mAPI.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
