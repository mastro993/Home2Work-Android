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

public class Home2WorkClient {

    public static final String AVATAR_BASE_URL = "http://home2workapi.azurewebsites.net/images/avatar/";
    public static final String COMPANIES_BASE_URL = "http://home2workapi.azurewebsites.net/images/companies/";
    public static final String ACHIEVEMENTS_BASE_URL = "http://home2workapi.azurewebsites.net/images/achievements/";

    private User mUser;
    private EndpointInterface mAPI;

    public Home2WorkClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://home2workapi.azurewebsites.net/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mAPI = retrofit.create(EndpointInterface.class);
    }

    /**
     * Ottiene l'utente correntemente autenticato nel client
     *
     * @return User utente autenticato
     */
    public User getUser() {
        return mUser;
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
                            this.mUser = userResponse.body();
                            loginCallback.onLoginSuccess();
                            break;
                        default:
                            loginCallback.onLoginError();
                            break;
                    }
                }, loginCallback::onError);
    }

    public void setFcmToken(String fcmToken) {
        mAPI.setFCMToken(mUser.getId(), fcmToken)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void uploadAvatar(MultipartBody.Part avatarBody, OnSuccessListener<ResponseBody> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.uploadAvatar(mUser.getId(), avatarBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 200)
                        onSuccessListener.onSuccess(response.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + response.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void getUserMatches(OnSuccessListener<List<Match>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getMatches(mUser.getId())
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

    public void createShare(OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.createShare(mUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void cancelShare(long shareId, OnSuccessListener<ResponseBody> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.cancelShare(shareId, mUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void leaveShare(long shareId, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.leaveShare(shareId, mUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void expelGuest(long shareId, long guestId, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.leaveShare(shareId, guestId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void getShare(Long shareID, OnSuccessListener<Share> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getShare(shareID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shareResponse -> {
                    if (shareResponse.code() == 200)
                        onSuccessListener.onSuccess(shareResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + shareResponse.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void updateUser(OnSuccessListener<User> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.updateUser(mUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 200)
                        onSuccessListener.onSuccess(response.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code " + response.code()));

                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void getCompanies(OnSuccessListener<List<Company>> onSuccessListener) {
        mAPI.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companiesResponse -> {
                    if (companiesResponse.code() == 200)
                        onSuccessListener.onSuccess(companiesResponse.body());
                }, Throwable::printStackTrace);
    }

    public void refreshUser(OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getUser(mUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    if (userResponse.code() == 200)
                        onSuccessListener.onSuccess(null);
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + userResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void getUserShares(OnSuccessListener<List<Share>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.getShares(mUser.getId())
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
        mAPI.joinShare(shareId, mUser.getId(), locationString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> {
                    if (responseBodyResponse.code() == 200)
                        onSuccessListener.onSuccess(responseBodyResponse.body());
                    else
                        onFailureListener.onFailure(new Exception("Response code: " + responseBodyResponse.code()));
                }, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void uploadLocation(List<Location> locationList, OnSuccessListener<List<Location>> onSuccessListener, OnFailureListener onFailureListener) {
        mAPI.uploadLocations(mUser.getId(), locationList)
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
