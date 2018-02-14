package it.gruppoinfor.home2work.database


import android.content.Context
import com.arasthel.asyncjob.AsyncJob
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2workapi.HomeToWorkClient

class RoutePointRepo constructor(ctx: Context) {

    private val locationsDatabase: LocationsDatabase = LocationsDatabase.getInstance(ctx)!!

    fun insert(routePointEntity: RoutePointEntity) {
        AsyncJob.doInBackground { locationsDatabase.routePointDAO().insert(routePointEntity) }
    }

    fun getAllUserLocations(onSuccessListener: OnSuccessListener<List<RoutePointEntity>>, onFailureListener: OnFailureListener) {

        locationsDatabase.routePointDAO().getAllUserPoints(HomeToWorkClient.getUser().id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccessListener.onSuccess(it) }, { onFailureListener.onFailure(Exception(it)) })
    }

    fun deleteAllUserLocations(onSuccessListener: OnSuccessListener<Int>) {
        AsyncJob.doInBackground {
            val deletedRows = locationsDatabase.routePointDAO().deleteAll(HomeToWorkClient.getUser().id!!)
            AsyncJob.doOnMainThread { onSuccessListener.onSuccess(deletedRows) }

        }
    }

    fun deleteAllUserLocations(userId: Long) {
        AsyncJob.doInBackground { locationsDatabase.routePointDAO().deleteAll(userId) }
    }

    fun insert(routePointEntity: RoutePointEntity, onSuccessListener: OnSuccessListener<Long>) {
        AsyncJob.doInBackground {
            val id = locationsDatabase.routePointDAO().insert(routePointEntity)
            AsyncJob.doOnMainThread { onSuccessListener.onSuccess(id) }
        }
    }


}
