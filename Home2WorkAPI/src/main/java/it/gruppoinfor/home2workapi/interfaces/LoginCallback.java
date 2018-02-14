package it.gruppoinfor.home2workapi.interfaces;

import it.gruppoinfor.home2workapi.model.User;

public interface LoginCallback {
    void onLoginSuccess(User user);

    void onInvalidCredential();

    void onLoginError();

    void onError(Throwable throwable);
}
