package it.gruppoinfor.home2work.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.List;

import it.gruppoinfor.home2work.MyLogger;
import it.gruppoinfor.home2work.UserPrefs;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.models.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.App.dbApp;


public class SyncService extends Service {

    private final String TAG = "SYN_SERVICE";

    private List<RoutePoint> routePoints = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.i(TAG, "Creazione servizio");

        if(!UserPrefs.isInited())
            UserPrefs.init(this);

        if (canSync())
            sync();

    }

    public void sync() {
        MyLogger.i(TAG, "Avvio sincronizzazione");

        AsyncJob.doInBackground(()->{
            for(RoutePointEntity routePointEntity : dbApp.routePointDAO().getAllUserPoints(Client.getSignedUser().getId())){

                RoutePoint routePoint = new RoutePoint();
                routePoint.setLatLng(routePointEntity.getLatLng());
                routePoint.setTime(routePointEntity.getTimestamp());
                routePoints.add(routePoint);

            }

            if(routePoints.size() > 0)
                syncRoutePoints(routePoints);
        });

    }

    private void syncRoutePoints(final List<RoutePoint> routePointList) {
        MyLogger.d(TAG, "Sincronizzazione RoutePoints (" + routePointList.size() + ")");

        Client.getAPI().uploadRoutePoint(Client.getSignedUser().getId(), routePointList)
                .enqueue(new Callback<List<RoutePoint>>() {
                    @Override
                    public void onResponse(Call<List<RoutePoint>> call, Response<List<RoutePoint>> response) {
                        AsyncJob.doInBackground(()-> dbApp.routePointDAO().deleteAll(Client.getSignedUser().getId()));
                        MyLogger.d(TAG, "Sync completata");
                    }

                    @Override
                    public void onFailure(Call<List<RoutePoint>> call, Throwable t) {
                        MyLogger.e(TAG, "Sync fallita. " + call.toString(), t);
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
