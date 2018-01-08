package it.gruppoinfor.home2work.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2workapi.Home2WorkClient;

public class FirebaseTokenService extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseTokenService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);

        if(App.home2WorkClient.getUser() != null)
            App.home2WorkClient.setFcmToken(refreshedToken);
    }


}
