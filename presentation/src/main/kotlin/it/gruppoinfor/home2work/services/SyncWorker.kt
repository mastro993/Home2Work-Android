package it.gruppoinfor.home2work.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.PersistableBundle
import androidx.work.*
import io.reactivex.Observable
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.DeleteUserLocations
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
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
                    Worker.Result.SUCCESS
                })

        return Worker.Result.SUCCESS
    }

    companion object {
        private val TAG = SyncWorker::class.java.simpleName
        const val KEY_USER_ID: String = "user_id"
        private const val REQUEST_TAG: String = "LOCATION_SYNC"

        fun singleRun(userId: Long) {

            val data: Data = mapOf("USER_ID" to userId).toWorkData()

            val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setInputData(data)
                    .addTag(REQUEST_TAG)
                    .build()

            WorkManager.getInstance()?.beginUniqueWork(
                    TAG,
                    ExistingWorkPolicy.KEEP,
                    syncWork
            )?.enqueue()
        }

        fun schedule(userId: Long) {

            val data: Data = mapOf("USER_ID" to userId).toWorkData()

            val syncWorker = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
                    .setInputData(data)
                    .addTag(REQUEST_TAG)
                    .build()

            WorkManager.getInstance()?.enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncWorker
            )

        }

        fun remove() {
            WorkManager.getInstance()?.cancelAllWorkByTag(REQUEST_TAG)
            WorkManager.getInstance()?.cancelUniqueWork(TAG)
        }
    }
}
