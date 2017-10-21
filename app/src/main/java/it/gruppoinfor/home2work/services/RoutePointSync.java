package it.gruppoinfor.home2work.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.arasthel.asyncjob.AsyncJob;

import java.util.ArrayList;
import java.util.List;

import it.gruppoinfor.home2work.MyLogger;
import it.gruppoinfor.home2work.PreferenceManager;
import it.gruppoinfor.home2work.UserPrefs;
import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.database.RoutePointEntity;
import it.gruppoinfor.home2work.models.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static it.gruppoinfor.home2work.App.dbApp;


public class RoutePointSync {

    private static final String TAG = "ROUTE_POINT_SYNC";

    private static List<RoutePoint> routePoints = new ArrayList<>();

    public static void sync(Context context) {
        PreferenceManager.init(context);

        if (!canSync(context)) return;

        try {

            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {

                    for(RoutePointEntity routePointEntity : dbApp.routePointDAO().getAllUserPoints(Client.getSignedUser().getId())){

                        RoutePoint routePoint = new RoutePoint();
                        routePoint.setLatLng(routePointEntity.getLatLng());
                        routePoint.setTime(routePointEntity.getTimestamp());
                        routePoints.add(routePoint);

                    }

                    if(routePoints.size() > 0)
                        syncRoutePoints(routePoints);

                }
            });


        } catch (Exception e) {
            MyLogger.e(TAG, null, e.getCause());
        }

    }

    private static void syncRoutePoints(final List<RoutePoint> routePointList) {
        MyLogger.d(TAG, "Sincronizzazione RoutePoints");

        Client.getAPI().uploadRoutePoint(Client.getSignedUser().getId(), routePointList)
                .enqueue(new Callback<List<RoutePoint>>() {
                    @Override
                    public void onResponse(Call<List<RoutePoint>> call, Response<List<RoutePoint>> response) {
                        AsyncJob.doInBackground(()-> dbApp.routePointDAO().deleteAll(Client.getSignedUser().getId()));
                        MyLogger.d(TAG, "Upload completato");
                    }

                    @Override
                    public void onFailure(Call<List<RoutePoint>> call, Throwable t) {
                        MyLogger.e(TAG, "Upload fallito. " + call.toString(), t);
                    }

                });

    }

    /**
     * Controlla se l'utente ha connettività WiFi
     */
    private static int getConnectivityType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            return activeNetwork.getType();
        } else {
            return -1;
        }
    }

    private static boolean canSync(Context context) {
        if (getConnectivityType(context) != ConnectivityManager.TYPE_WIFI) {
            if (!UserPrefs.syncWithData || getConnectivityType(context) != ConnectivityManager.TYPE_MOBILE)
                return false;
        }
        return true;
    }


}
