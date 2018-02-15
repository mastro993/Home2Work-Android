package it.gruppoinfor.home2work.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

import it.gruppoinfor.home2work.utils.LatLngConverter


@Database(entities = [(RoutePointEntity::class)], version = 5, exportSchema = false)
@TypeConverters(LatLngConverter::class)
abstract class LocationsDatabase : RoomDatabase() {
    abstract fun routePointDAO(): RoutePointDAO

    companion object {
        private var INSTANCE: LocationsDatabase? = null

        fun getInstance(context: Context): LocationsDatabase? {
            if (INSTANCE == null) {
                synchronized(LocationsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            LocationsDatabase::class.java, "home2work")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
