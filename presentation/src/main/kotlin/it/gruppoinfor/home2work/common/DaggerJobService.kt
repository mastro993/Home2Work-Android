package it.gruppoinfor.home2work.common

import android.app.job.JobService
import it.gruppoinfor.home2work.di.DipendencyInjector

abstract class DaggerJobService: JobService() {

    override fun onCreate() {
        super.onCreate()
        DipendencyInjector.mainComponent.inject(this)
    }
}