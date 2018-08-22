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
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.doAsync
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
        Timber.d("SyncWorker: %1s","doWork")

        val userId = inputData.getLong(KEY_USER_ID, 0)

        getUserLocations.byId(userId)
                .flatMap {
                    if (it.isNotEmpty()) {
                        Timber.i("${it.size} posizioni da sincronizzare")
                        syncUserLocation.upload(it)
                    } else {
                        Timber.i("Nessuna posizione da sincronizzare")
                        Observable.just(false)
                    }
                }
                .subscribe({ synced ->
                    if (synced) {
                        Timber.i("Sincronizzazione completata")
                        doAsync{
                            deleteUserLocations.byId(userId).subscribe {
                                Timber.i("Posizioni utente eliminate")
                            }
                        }
                    }
                    Worker.Result.SUCCESS
                }, {
                    Timber.e(it, "Sincronizzazione fallita!")
                    Worker.Result.SUCCESS
                })

        return Worker.Result.SUCCESS
    }

    companion object {
        private val TAG = SyncWorker::class.java.simpleName
        const val KEY_USER_ID: String = "USER_ID"
        private const val REQUEST_TAG: String = "LOCATION_SYNC"

        fun singleRun(userId: Long) {
            Timber.d("SyncWorker: %1s","singleRun")

            val data = Data.Builder()
                    .putLong(KEY_USER_ID, userId)
                    .build()

            val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setInputData(data)
                    .addTag(REQUEST_TAG)
                    .build()

            WorkManager.getInstance().beginUniqueWork(
                    TAG,
                    ExistingWorkPolicy.KEEP,
                    syncWork
            ).enqueue()
        }

        fun schedule(userId: Long) {
            Timber.d("SyncWorker: %1s","schedule")

            val data = Data.Builder()
                    .putLong(KEY_USER_ID, userId)
                    .build()

            val syncWorker = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
                    .setInputData(data)
                    .addTag(REQUEST_TAG)
                    .build()

            WorkManager.getInstance().enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncWorker
            )

        }

        fun remove() {
            Timber.d("SyncWorker: %1s","remove")

            WorkManager.getInstance().cancelAllWorkByTag(REQUEST_TAG)
            WorkManager.getInstance().cancelUniqueWork(TAG)
        }
    }
}
