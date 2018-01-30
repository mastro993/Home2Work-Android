package it.gruppoinfor.home2work;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import it.gruppoinfor.home2work.activities.SplashActivity;
import it.gruppoinfor.home2workapi.HomeToWorkClient;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary(); // LaakCanary
        initCaoc(); // Caoc
        initStetho(); // Stetho

        HomeToWorkClient.init();

    }

    private void initLeakCanary() {
        // Inizializzazione LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private void initStetho() {
        // Inizializzazione Stetho
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        // Abilito gli strumenti di sviluppo di chrome
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // Abilito CLI
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    private void initCaoc() {
        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        CaocConfig.Builder.create()
                //.backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                // TODO rimettere .enabled(isDebuggable) // Abilitato solo de in debug mode
                //.showErrorDetails(false) //default: true
                //.showRestartButton(false) //default: true
                //.logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                //.errorDrawable(R.drawable.ic_custom_drawable) //default: bug image
                .restartActivity(SplashActivity.class) //default: null (your app's launch activity)
                //.errorActivity(YourCustomErrorActivity.class) //default: null (default error activity)
                //.eventListener(new YourCustomEventListener()) //default: null
                .apply();
    }


}
