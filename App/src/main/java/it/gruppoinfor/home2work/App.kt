package it.gruppoinfor.home2work

import android.app.Application
import android.content.ContextWrapper
import com.facebook.stetho.Stetho
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.leakcanary.LeakCanary


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLeakCanary() // LeakCanary
        initStetho() // Stetho
        initPrefsManager() // EasyPrefs

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


}
