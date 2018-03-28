package it.gruppoinfor.home2work

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.FirebaseApp
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import it.gruppoinfor.home2work.common.timber.DebugLogTree
import it.gruppoinfor.home2work.common.timber.FileLoggingTree
import it.gruppoinfor.home2work.common.timber.ReleaseLogTree
import it.gruppoinfor.home2work.di.DipendencyInjector
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        val kit = Crashlytics.Builder().core(core).build()
        Fabric.with(this, kit)

        if (BuildConfig.DEBUG) {
            Timber.plant(FileLoggingTree())
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }

        DipendencyInjector.init(applicationContext)

        FirebaseApp.initializeApp(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        Fabric.with(this, Crashlytics())

    }


}
