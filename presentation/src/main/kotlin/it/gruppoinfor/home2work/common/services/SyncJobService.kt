package it.gruppoinfor.home2work.common.services

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.common.mappers.UserLocationEntityUserLocationMapper
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.data.entities.UserLocationData_
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.domain.usecases.UploadUserLocations
import it.gruppoinfor.home2work.entities.UserLocation
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SyncJobService : JobService() {

    @Inject
    lateinit var uploadUserLocations: UploadUserLocations
    @Inject
    lateinit var localUserData: LocalUserData

    override fun onCreate() {
        super.onCreate()

        DipendencyInjector.mainComponent.inject(this)
    }

    override fun onStartJob(params: JobParameters): Boolean {

        Timber.v("Inizio job di sincronizzazione")

        val userId = params.extras.getLong(KEY_USER_ID)

        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
        val userLocations = userLocationBox.query().equal(UserLocationData_.userId, userId).build().find()

        return if (userLocations.size > 0) {
            syncRoutePoints(userLocations)
            true
        } else {
            false
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Timber.v("Fine job di sincronizzazione")
        return false
    }

    @SuppressLint("CheckResult")
    private fun syncRoutePoints(locationList: List<UserLocation>) {

        localUserData.user?.let { user ->

            Timber.i("Sincronizzazione di ${locationList.size} posizioni utente")

            val mapper = UserLocationEntityUserLocationMapper()

            uploadUserLocations.upload(locationList.map { mapper.mapFrom(it) })
                    .subscribe({

                        Timber.i("Sincronizzazione completata")
                        Answers.getInstance().logCustom(CustomEvent("Sincronizzazione posizioni"))

                        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
                        userLocationBox.query().equal(UserLocationData_.userId, user.id).build().remove()

                    }, {
                        Timber.e(it, "Sincronizzazione fallita")
                    })

        }

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
