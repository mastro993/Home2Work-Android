package it.gruppoinfor.home2work.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.database.RoutePointRepo;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.model.Location;


public class SyncService extends Service {

    private final String TAG = "SYNC_SERVICE";

    private List<Location> locations = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        UserPrefs.init(this);

        SessionManager sessionManager = new SessionManager(this, new SessionManager.SessionManagerCallback() {
            @Override
            public void onNoSession() {

            }

            @Override
            public void onValidSession() {
                if (getConnectivityType(SyncService.this) == ConnectivityManager.TYPE_WIFI || UserPrefs.syncWithData) {
                    sync();
                }
            }

            @Override
            public void onExpiredToken() {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        sessionManager.loadSession();

    }

    public void sync() {
        RoutePointRepo.getAllUserLocations()
                .subscribe(routePointEntities -> {

                    for (RoutePointEntity routePointEntity : routePointEntities) {

                        Location location = new Location();
                        location.setLatLng(routePointEntity.getLatLng());
                        location.setDate(new Date(routePointEntity.getTimestamp() * 1000));
                        locations.add(location);

                    }

                    if (locations.size() > 0) syncRoutePoints(locations);

                });

    }

    private void syncRoutePoints(final List<Location> locationList) {

        App.home2WorkClient.uploadLocation(locationList,
                locations -> RoutePointRepo.deleteAllUserLocations().subscribe(),
                e -> Log.e(TAG, "Sincronizzazione fallita", new Throwable(e)));

    }

    private int getConnectivityType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            return activeNetwork.getType();
        } else {
            return -1;
        }
    }

    private boolean canSync() {
        if (getConnectivityType(this) != ConnectivityManager.TYPE_WIFI) {
            if (!UserPrefs.syncWithData || getConnectivityType(this) != ConnectivityManager.TYPE_MOBILE)
                return false;
        }
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
