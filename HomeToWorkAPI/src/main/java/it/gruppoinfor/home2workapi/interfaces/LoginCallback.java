package it.gruppoinfor.home2workapi.interfaces;

public interface LoginCallback {
    void onLoginSuccess();

    void onInvalidCredential();

    void onLoginError();

    void onError(Throwable throwable);
}
