package it.gruppoinfor.home2workapi.interfaces;

public interface ResponseCallback {
    void onSuccess();

    void onError(int errorCode);

}
