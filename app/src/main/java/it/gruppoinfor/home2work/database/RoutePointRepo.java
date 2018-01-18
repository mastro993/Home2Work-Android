package it.gruppoinfor.home2work.database;


import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2work.App;

public class RoutePointRepo {

    public static void getAllUserLocations(OnSuccessListener<List<RoutePointEntity>> onSuccessListener, OnFailureListener onFailureListener) {
        App.DBApp.routePointDAO().getAllUserPoints(App.home2WorkClient.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public static void deleteAllUserLocations(OnSuccessListener<Integer> onSuccessListener) {
        AsyncJob.doInBackground(() -> {
            int deletedRows = App.DBApp.routePointDAO().deleteAll(App.home2WorkClient.getUser().getId());
            AsyncJob.doOnMainThread(() -> onSuccessListener.onSuccess(deletedRows));

        });
    }

    public static void deleteAllUserLocations() {
        AsyncJob.doInBackground(() -> App.DBApp.routePointDAO().deleteAll(App.home2WorkClient.getUser().getId()));
    }

    public static void insert(RoutePointEntity routePointEntity, OnSuccessListener<Long> onSuccessListener) {
        AsyncJob.doInBackground(() -> {
            Long id = App.DBApp.routePointDAO().insert(routePointEntity);
            AsyncJob.doOnMainThread(() -> onSuccessListener.onSuccess(id));

        });
    }

    public static void insert(RoutePointEntity routePointEntity) {
        AsyncJob.doInBackground(() -> App.DBApp.routePointDAO().insert(routePointEntity));
    }


}
