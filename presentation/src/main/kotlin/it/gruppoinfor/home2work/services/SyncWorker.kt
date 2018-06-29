package it.gruppoinfor.home2work.services

import android.app.job.JobParameters
import android.app.job.JobService
import androidx.work.Worker
import io.reactivex.Observable
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.DeleteUserLocations
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations
import timber.log.Timber
import javax.inject.Inject


class SyncWorker : Worker() {

    @Inject
    lateinit var getUserLocations: GetUserLocations
    @Inject
    lateinit var syncUserLocation: SyncUserLocations
    @Inject
    lateinit var deleteUserLocations: DeleteUserLocations




    override fun doWork(): Worker.Result {
        DipendencyInjector.mainComponent.inject(this)

        Timber.i("Sync job start")

        val userId = inputData.getLong(KEY_USER_ID, 0)

        getUserLocations.byId(userId)
                .flatMap {
                    if (it.isNotEmpty()) {
                        Timber.v("${it.size} positions to sync")
                        syncUserLocation.upload(it)
                    } else {
                        Timber.v("No position to sync")
                        Observable.just(false)
                    }
                }
                .subscribe({
                    if (it) {
                        Timber.i("Sync completed")
                        deleteUserLocations.byId(userId).subscribe {
                            Timber.i("All user positions deleted")
                        }
                    }
                    Worker.Result.SUCCESS
                }, {
                    Timber.e(it, "Sync failed!")
                    Worker.Result.FAILURE
                })

        return Worker.Result.SUCCESS
    }

    companion object {
        const val ID: Int = 2342
        const val KEY_USER_ID: String = "user_id"
    }
}
