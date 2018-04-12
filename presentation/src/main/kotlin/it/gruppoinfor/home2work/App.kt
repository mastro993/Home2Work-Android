package it.gruppoinfor.home2work

import android.app.Application
import android.content.Intent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.FirebaseApp
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import it.gruppoinfor.home2work.common.JobScheduler
import it.gruppoinfor.home2work.common.extensions.launchActivity
import it.gruppoinfor.home2work.common.timber.DebugLogTree
import it.gruppoinfor.home2work.common.timber.FileLoggingTree
import it.gruppoinfor.home2work.common.timber.ReleaseLogTree
import it.gruppoinfor.home2work.common.user.LocalUserData
import it.gruppoinfor.home2work.data.api.LogoutEvent
import it.gruppoinfor.home2work.data.api.NoInternetErrorEvent
import it.gruppoinfor.home2work.di.DipendencyInjector
import it.gruppoinfor.home2work.splash.SplashActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject


class App : Application() {

    @Inject
    lateinit var localUserData: LocalUserData
    @Inject
    lateinit var jobScheduler: JobScheduler

    override fun onCreate() {
        super.onCreate()

        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)

        if (BuildConfig.DEBUG) {
            Timber.plant(FileLoggingTree())
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }

        DipendencyInjector.init(applicationContext)
        DipendencyInjector.mainComponent.inject(this)

        FirebaseApp.initializeApp(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        Fabric.with(this, Crashlytics())

        EventBus.getDefault().register(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(event: LogoutEvent) {

        localUserData.clear()
        jobScheduler.removeSyncJob()

        launchActivity<SplashActivity> {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNoInternetErrorEvent(event: NoInternetErrorEvent) {

        //toast("Nessuna connessione ad internet")

    }


}
