package it.gruppoinfor.home2work

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.facebook.stetho.Stetho
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.leakcanary.LeakCanary
import it.gruppoinfor.home2work.activities.SplashActivity
import it.gruppoinfor.home2workapi.HomeToWorkClient


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initLeakCanary() // LaakCanary
        initCaoc() // Caoc
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

    private fun initCaoc() {

        CaocConfig.Builder.create()
                //.backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                // TODO rimettere .enabled(isDebuggable) // Abilitato solo se in debug mode
                //.showErrorDetails(false) //default: true
                //.showRestartButton(false) //default: true
                //.logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                //.errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
                .restartActivity(SplashActivity::class.java) //default: null (your app's launch activity)
                //.errorActivity(YourCustomErrorActivity.class) //default: null (default error activity)
                //.eventListener(new YourCustomEventListener()) //default: null
                .apply()

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
