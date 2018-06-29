package it.gruppoinfor.home2work.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import it.gruppoinfor.home2work.data.dao.UserLocationDAO
import it.gruppoinfor.home2work.data.entities.UserLocationData

@Database(entities = [(UserLocationData::class)], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class UserLocationDb : RoomDatabase() {

    abstract fun userLocationDAO(): UserLocationDAO

    companion object {
        private var INSTANCE: UserLocationDb? = null

        fun getInstance(context: Context): UserLocationDb {
            if (INSTANCE == null) {
                synchronized(UserLocationDb::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            UserLocationDb::class.java, "userLocations.db")
                            .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}