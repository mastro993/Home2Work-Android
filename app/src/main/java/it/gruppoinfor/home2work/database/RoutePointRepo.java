package it.gruppoinfor.home2work.database;


import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import it.gruppoinfor.home2work.App;
import it.gruppoinfor.home2workapi.Home2WorkClient;

public class RoutePointRepo {

    public static Single<List<RoutePointEntity>> getAllUserLocations() {
        return App.DBApp.routePointDAO().getAllUserPoints(App.home2WorkClient.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<?> deleteAllUserLocations() {
        return Single.defer(() -> {
            App.DBApp.routePointDAO().deleteAll(App.home2WorkClient.getUser().getId());
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<?> insert(RoutePointEntity routePointEntity) {
        return Single.defer(() -> {
            App.DBApp.routePointDAO().insert(routePointEntity);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
