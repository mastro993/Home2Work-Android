package it.gruppoinfor.home2work

import android.app.Application
import android.content.ContextWrapper
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.pixplicity.easyprefs.library.Prefs
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

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()



        Fabric.with(this, Crashlytics())

        val initializerBuilder = Stetho.newInitializerBuilder(this)
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
        val initializer = initializerBuilder.build()
        Stetho.initialize(initializer)

    }


}
