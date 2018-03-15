package it.gruppoinfor.home2work

import android.app.Application
import android.content.ContextWrapper
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import io.objectbox.BoxStore
import it.gruppoinfor.home2work.tracking.MyObjectBox
import it.gruppoinfor.home2work.utils.FileLoggingTree
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber logger
        initTimber()

        // Fabric
        Fabric.with(this, Crashlytics())

        // Instanza ObjectBox
        boxStore = MyObjectBox.builder().androidContext(this).build()

        initLeakCanary() // LeakCanary
        initStetho() // Stetho
        initPrefsManager() // EasyPrefs

    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(FileLoggingTree())
        }
    }

    private fun initLeakCanary() {

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

    }

    private fun initStetho() {

        val initializerBuilder = Stetho.newInitializerBuilder(this)
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
        val initializer = initializerBuilder.build()
        Stetho.initialize(initializer)

    }

    private fun initPrefsManager() {

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()

    }

    companion object {
        lateinit var boxStore: BoxStore
    }


}
