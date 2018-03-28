package it.gruppoinfor.home2work.common.services

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import io.reactivex.Observable
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.GetUserLocations
import it.gruppoinfor.home2work.domain.usecases.SyncUserLocations
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SyncJobService : JobService() {

    @Inject
    lateinit var getUserLocations: GetUserLocations
    @Inject
    lateinit var syncUserLocation: SyncUserLocations

    override fun onCreate() {
        super.onCreate()

        DipendencyInjector.mainComponent.inject(this)
    }

    override fun onStartJob(params: JobParameters): Boolean {

        Timber.v("Inizio job di sincronizzazione")

        val userId = params.extras.getLong(KEY_USER_ID)

        getUserLocations.byId(userId)
                .flatMap {
                    if (it.isNotEmpty()) {
                        Timber.i("Sincronizzazione di ${it.size} posizioni utente")
                        syncUserLocation.upload(it)
                    } else {
                        Timber.i("Nessuna posizione da sincronizzare")
                        Observable.just(false)
                    }
                }
                .subscribe({
                    Timber.i("Sincronizzazione completata")
                    jobFinished(params, true)
                }, {
                    Timber.e(it, "Sincronizzazione fallita")
                    jobFinished(params, true)
                })

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Timber.v("Fine job di sincronizzazione")
        return false
    }

    companion object {
        const val ID: Int = 2342
        const val KEY_USER_ID: String = "user_id"

        fun schedule(context: Context, userId: Long?) {
            userId?.let {
                val extras = PersistableBundle()
                extras.putLong(SyncJobService.KEY_USER_ID, it)

                val builder = JobInfo.Builder(SyncJobService.ID, ComponentName(context, SyncJobService::class.java))
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                builder.setPeriodic(TimeUnit.DAYS.toMillis(1))

                builder.run {
                    setRequiresDeviceIdle(true)
                    setRequiresCharging(false)
                    setPersisted(true)
                    setExtras(extras)
                }

                val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobs = jobScheduler.allPendingJobs
                if (!jobs.any { it.id == SyncJobService.ID }) {
                    jobScheduler.schedule(builder.build())
                    Timber.v("SyncServiceJob scheduled")
                }
            }

        }

        fun remove(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(ID)
        }
    }
}
