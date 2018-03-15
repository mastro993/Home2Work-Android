package it.gruppoinfor.home2work.tracking

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
import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.App
import it.gruppoinfor.home2work.api.HomeToWorkClient
import it.gruppoinfor.home2work.location.Location
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class SyncJobService : JobService() {

    private val routeLocations = ArrayList<Location>()

    override fun onStartJob(params: JobParameters): Boolean {

        Timber.v("Inizio job di sincronizzazione")

        val userId = params.extras.getLong(KEY_USER_ID)

        val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
        val userLocations = userLocationBox.query().equal(UserLocation_.userId, userId).build().find()

        userLocations.forEach {
            val routeLocation = Location()
            routeLocation.latLng = it.latLng
            routeLocation.date = Date(it.timestamp * 1000)
            routeLocations.add(routeLocation)
        }

        return if (routeLocations.size > 0) {
            syncRoutePoints(routeLocations)
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
    private fun syncRoutePoints(locationList: List<Location>) {

        Timber.i("Sincronizzazione di ${locationList.size} posizioni utente")

        HomeToWorkClient.getUserService().uploadLocations(locationList)
                .subscribeOn(Schedulers.io())
                .onErrorReturn { null }
                .subscribe({
                    Timber.i("Sincronizzazione completata")
                    Answers.getInstance().logCustom(CustomEvent("Sincronizzazione posizioni"))

                    val userLocationBox = App.boxStore.boxFor(UserLocation::class.java)
                    userLocationBox.query().equal(UserLocation_.userId, HomeToWorkClient.user!!.id).build().remove()
                }, {
                    Timber.e(it, "Sincronizzazione fallita")
                })

    }

    companion object {
        const val ID: Int = 2342
        const val KEY_USER_ID: String = "user_id"

        fun schedule(context: Context, userId: Long) {
            val extras = PersistableBundle()
            extras.putLong(KEY_USER_ID, userId)

            val builder = JobInfo.Builder(ID, ComponentName(context, SyncJobService::class.java))
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
            if (!jobs.any { it.id == ID }) {
                jobScheduler.schedule(builder.build())
                Timber.v("SyncServiceJob scheduled")
            }

        }

        fun remove(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(ID)
        }
    }
}
