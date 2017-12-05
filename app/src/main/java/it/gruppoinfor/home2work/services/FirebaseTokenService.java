package it.gruppoinfor.home2work.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2workapi.Client;

public class FirebaseTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MyLogger.d("MESSAGING_SERVICE", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        Client.getAPI().setFCMToken(Client.getSignedUser().getId(), refreshedToken);
    }


}
