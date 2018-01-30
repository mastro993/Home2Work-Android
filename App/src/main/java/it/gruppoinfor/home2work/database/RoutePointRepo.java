package it.gruppoinfor.home2work.database;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.arasthel.asyncjob.AsyncJob;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2workapi.HomeToWorkClient;
import it.gruppoinfor.home2workapi.model.User;

public class RoutePointRepo {

    private DBApp dbApp;

    public RoutePointRepo(Context context) {
        dbApp = Room.databaseBuilder(context, DBApp.class, "home2work")
                .fallbackToDestructiveMigration()
                .build();
    }

    public void insert(RoutePointEntity routePointEntity) {
        AsyncJob.doInBackground(() -> dbApp.routePointDAO().insert(routePointEntity));
    }

    public void getAllUserLocations(OnSuccessListener<List<RoutePointEntity>> onSuccessListener, OnFailureListener onFailureListener) {
        dbApp.routePointDAO().getAllUserPoints(HomeToWorkClient.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessListener::onSuccess, throwable -> onFailureListener.onFailure(new Exception(throwable)));
    }

    public void deleteAllUserLocations(OnSuccessListener<Integer> onSuccessListener) {
        AsyncJob.doInBackground(() -> {
            int deletedRows = dbApp.routePointDAO().deleteAll(HomeToWorkClient.getUser().getId());
            AsyncJob.doOnMainThread(() -> onSuccessListener.onSuccess(deletedRows));

        });
    }

    public void deleteAllUserLocations(long userId) {
        AsyncJob.doInBackground(() -> dbApp.routePointDAO().deleteAll(userId));
    }

    public void insert(RoutePointEntity routePointEntity, OnSuccessListener<Long> onSuccessListener) {
        AsyncJob.doInBackground(() -> {
            Long id = dbApp.routePointDAO().insert(routePointEntity);
            AsyncJob.doOnMainThread(() -> onSuccessListener.onSuccess(id));

        });
    }


}
