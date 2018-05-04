package it.gruppoinfor.home2work.common

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import it.gruppoinfor.home2work.services.SyncJobService
import timber.log.Timber
import java.util.concurrent.TimeUnit


class JobScheduler constructor(private val context: Context) {

    fun scheduleSyncJob(userId: Long) {
        val extras = PersistableBundle()
        extras.putLong(SyncJobService.KEY_USER_ID, userId)

        val builder = JobInfo.Builder(SyncJobService.ID, ComponentName(context, SyncJobService::class.java))
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        builder.setPeriodic(TimeUnit.HOURS.toMillis(6))

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

    fun removeSyncJob() {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(SyncJobService.ID)
    }

}