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

import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.database.RoutePointRepo;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.RouteLocation;
import it.gruppoinfor.home2workapi.model.User;


public class SyncService extends Service {

    private final String TAG = "SYNC_SERVICE";

    private List<RouteLocation> routeLocations = new ArrayList<>();
    private RoutePointRepo mRoutePointRepo;
    private User mUser;

    @Override
    public void onCreate() {
        super.onCreate();

        UserPrefs.init(this);
        mRoutePointRepo = new RoutePointRepo(this);

        SessionManager.loadSession(this, new SessionManager.SessionCallback() {
            @Override
            public void onValidSession() {
                mUser = HomeToWorkClient.getUser();
                if (canSync()) sync();
            }

            @Override
            public void onInvalidSession(int code, @Nullable Throwable throwable) {
                if (throwable != null) throwable.printStackTrace();
            }
        });

    }

    public void sync() {

        mRoutePointRepo.getAllUserLocations(routePointEntities -> {
            for (RoutePointEntity routePointEntity : routePointEntities) {
                RouteLocation routeLocation = new RouteLocation();
                routeLocation.setLatLng(routePointEntity.getLatLng());
                routeLocation.setDate(new Date(routePointEntity.getTimestamp() * 1000));
                routeLocations.add(routeLocation);
            }

            if (routeLocations.size() > 0) syncRoutePoints(routeLocations);
        }, Throwable::printStackTrace);
    }

    private void syncRoutePoints(final List<RouteLocation> routeLocationList) {
        HomeToWorkClient.getInstance().uploadLocation(mUser.getId(), routeLocationList,
                locations -> mRoutePointRepo.deleteAllUserLocations(mUser.getId()),
                e -> Log.e(TAG, "Sincronizzazione fallita", new Throwable(e)));

    }

    private int getConnectivityType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                return activeNetwork.getType();
            }
        }
        return -1;
    }

    private boolean canSync() {
        boolean WiFiEnabled = getConnectivityType(SyncService.this) == ConnectivityManager.TYPE_WIFI;
        return WiFiEnabled || UserPrefs.SyncWithData;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
