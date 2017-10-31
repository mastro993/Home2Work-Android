package it.gruppoinfor.home2work;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;
import it.gruppoinfor.home2work.database.DBApp;
import it.gruppoinfor.home2work.utils.MyLogger;
import it.gruppoinfor.home2work.utils.UserPrefs;
import it.gruppoinfor.home2workapi.Client;


public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    private static App instance;

    public static DBApp dbApp;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //setupLeakCanary();
        setupStetho();
        MyLogger.init(this);
        UserPrefs.init(this);
        Client.init();

        dbApp = Room.databaseBuilder(getApplicationContext(), DBApp.class, "home2work")
                .fallbackToDestructiveMigration()
                .build();
    }

/*    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }*/

    private void setupStetho() {
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(this)
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }

}
