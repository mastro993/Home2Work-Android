package it.gruppoinfor.home2work;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.Logger;

import it.gruppoinfor.home2work.api.Client;
import it.gruppoinfor.home2work.database.DBApp;


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
        setupUncaughtExceptionsHandler();
        //setupLeakCanary();
        setupStetho();
        MyLogger.init(this);
        ReceiverManager.init(this);
        //PreferenceManager.init(this); // Inizializzazione gestore preferenze
        Client.init();

        dbApp = Room.databaseBuilder(getApplicationContext(), DBApp.class, "home2work")
                .fallbackToDestructiveMigration()
                .build();
    }

    private void setupUncaughtExceptionsHandler() {
        final Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Logger.log(Logger.ERROR, thread.getName().toUpperCase(), ex.getLocalizedMessage(), ex);
                // re-throw critical exception further to the os (important)
                defaultUEH.uncaughtException(thread, ex);
            }
        });

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

/*        // Realm.io
        initializerBuilder.enableWebKitInspector(
                RealmInspectorModulesProvider.builder(this).build()
        );*/

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }

}
