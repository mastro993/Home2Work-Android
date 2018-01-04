package it.gruppoinfor.home2work.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.utils.SessionManager;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.Home2WorkClient;
import it.gruppoinfor.home2workapi.model.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.App.dbApp;


public class SyncService extends Service {

    private final String TAG = "SYNC_SERVICE";

    private List<Location> locations = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        if (!UserPrefs.isInited())
            UserPrefs.init(this);

        SessionManager.with(this).checkSession(new SessionManager.SessionManagerCallback() {
            @Override
            public void onValidSession() {
                if (getConnectivityType(SyncService.this) == ConnectivityManager.TYPE_WIFI || UserPrefs.syncWithData) {
                    sync();
                }
            }
        });

    }

    public void sync() {
        AsyncJob.doInBackground(() -> {
            for (RoutePointEntity routePointEntity : dbApp.routePointDAO().getAllUserPoints(Home2WorkClient.User.getId())) {

                Location location = new Location();
                location.setLatLng(routePointEntity.getLatLng());
                location.setDate(new Date(routePointEntity.getTimestamp() * 1000));
                locations.add(location);

            }

            if (locations.size() > 0) syncRoutePoints(locations);

        });

    }

    private void syncRoutePoints(final List<Location> locationList) {

        Home2WorkClient.getAPI().uploadLocations(Home2WorkClient.User.getId(), locationList)
                .enqueue(new Callback<List<Location>>() {
                    @Override
                    public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                        if (response.code() == 200)
                            AsyncJob.doInBackground(() -> dbApp.routePointDAO().deleteAll(Home2WorkClient.User.getId()));
                    }

                    @Override
                    public void onFailure(Call<List<Location>> call, Throwable t) {
                        Log.e(TAG, "Sincronizzazione fallita. Errore Home2WorkClient " + call.toString(), t);
                    }

                });

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
