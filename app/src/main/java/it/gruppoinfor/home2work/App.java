package it.gruppoinfor.home2work;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import it.gruppoinfor.home2work.api.Client;


public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    private static App instance;

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
        //setupUncaughtExceptionsHandler(); // Setup gestore eccezioni non gestite
        //setupLeakCanary(); // Inizializzazione LeakCanary
        setupStetho(); // Inizializzazione Stetho
        //setupRealm(); // Inizializzazione Realm.io
        //MyLogger.init(this); // Inizializzazione logger su file
        //PreferenceManager.init(this); // Inizializzazione gestore preferenze
        Client.init();
    }

/*    private void setupUncaughtExceptionsHandler() {
        final Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Logger.log(Logger.ERROR, thread.getName().toUpperCase(), ex.getLocalizedMessage(), ex);
                // re-throw critical exception further to the os (important)
                defaultUEH.uncaughtException(thread, ex);
            }
        });

    }*/

/*    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }*/

/*    private void setupRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(2) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
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
