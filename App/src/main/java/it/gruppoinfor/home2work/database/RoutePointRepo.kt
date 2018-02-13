package it.gruppoinfor.home2work.database


import android.arch.persistence.room.Room
import android.content.Context

import com.arasthel.asyncjob.AsyncJob
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2workapi.HomeToWorkClient
import it.gruppoinfor.home2workapi.model.User

class RoutePointRepo(context: Context) {

    private val dbApp: DBApp

    init {
        dbApp = Room.databaseBuilder(context, DBApp::class.java, "home2work")
                .fallbackToDestructiveMigration()
                .build()
    }

    fun insert(routePointEntity: RoutePointEntity) {
        AsyncJob.doInBackground { dbApp.routePointDAO().insert(routePointEntity) }
    }

    fun getAllUserLocations(onSuccessListener: OnSuccessListener<List<RoutePointEntity>>, onFailureListener: OnFailureListener) {
        dbApp.routePointDAO().getAllUserPoints(HomeToWorkClient.getUser().id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<List<RoutePointEntity>> { onSuccessListener.onSuccess(it) }) { throwable -> onFailureListener.onFailure(Exception(throwable)) }
    }

    fun deleteAllUserLocations(onSuccessListener: OnSuccessListener<Int>) {
        AsyncJob.doInBackground {
            val deletedRows = dbApp.routePointDAO().deleteAll(HomeToWorkClient.getUser().id!!)
            AsyncJob.doOnMainThread { onSuccessListener.onSuccess(deletedRows) }

        }
    }

    fun deleteAllUserLocations(userId: Long) {
        AsyncJob.doInBackground { dbApp.routePointDAO().deleteAll(userId) }
    }

    fun insert(routePointEntity: RoutePointEntity, onSuccessListener: OnSuccessListener<Long>) {
        AsyncJob.doInBackground {
            val id = dbApp.routePointDAO().insert(routePointEntity)
            AsyncJob.doOnMainThread { onSuccessListener.onSuccess(id) }

        }
    }


}
