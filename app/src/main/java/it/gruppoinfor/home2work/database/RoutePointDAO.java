package it.gruppoinfor.home2work.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;
import java.util.Observable;

import io.reactivex.Single;

@Dao
public interface RoutePointDAO {

    @Query("SELECT * FROM routePoint WHERE userId = :userId ORDER BY timestamp DESC")
    Single<List<RoutePointEntity>> getAllUserPoints(long userId);

    @Query("SELECT * FROM routePoint WHERE id = :id")
    Single<RoutePointEntity> getByID(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(RoutePointEntity routePointEntity);

    @Query("DELETE FROM routePoint WHERE userId = :userId")
    int deleteAll(long userId);

}
