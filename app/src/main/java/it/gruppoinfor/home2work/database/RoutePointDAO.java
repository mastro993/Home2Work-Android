package it.gruppoinfor.home2work.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RoutePointDAO {

    @Query("SELECT * FROM routePoint WHERE userId = :userId ORDER BY timestamp DESC")
    List<RoutePointEntity> getAllUserPoints(long userId);

    @Query("SELECT * FROM routePoint WHERE id = :id")
    RoutePointEntity getByID(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RoutePointEntity routePointEntity);

    @Query("DELETE FROM routePoint WHERE userId = :userId")
    void deleteAll(long userId);

}
