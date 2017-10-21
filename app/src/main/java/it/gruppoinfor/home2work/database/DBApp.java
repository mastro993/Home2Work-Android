package it.gruppoinfor.home2work.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import it.gruppoinfor.home2work.LatLngConverter;



@Database(entities = {RoutePointEntity.class}, version = 3)
@TypeConverters({LatLngConverter.class})
public abstract class DBApp extends RoomDatabase {
    public abstract RoutePointDAO routePointDAO();
}
