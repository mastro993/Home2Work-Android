package it.gruppoinfor.home2work.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import it.gruppoinfor.home2work.utils.LatLngConverter


@Database(entities = arrayOf(RoutePointEntity::class), version = 3, exportSchema = false)
@TypeConverters(LatLngConverter::class)
abstract class DBApp : RoomDatabase() {
    abstract fun routePointDAO(): RoutePointDAO
}
