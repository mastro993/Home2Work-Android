package it.gruppoinfor.home2work;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import it.gruppoinfor.home2work.database.DBApp;
import it.gruppoinfor.home2work.utils.UserPrefs;


public class App extends Application {
    public static DBApp dbApp;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inizializzazione LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        // Inizializzazione Stetho
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        // Abilito gli strumenti di sviluppo di chrome
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // Abilito CLI
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);

        // Inizializzazione Room
        dbApp = Room.databaseBuilder(getApplicationContext(), DBApp.class, "home2work")
                .fallbackToDestructiveMigration()
                .build();
    }

}
