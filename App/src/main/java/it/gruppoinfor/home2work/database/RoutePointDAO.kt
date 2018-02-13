package it.gruppoinfor.home2work.database


import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import java.util.Observable

import io.reactivex.Single

@Dao
interface RoutePointDAO {

    @Query("SELECT * FROM routePoint WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllUserPoints(userId: Long): Single<List<RoutePointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routePointEntity: RoutePointEntity): Long?

    @Query("DELETE FROM routePoint WHERE userId = :userId")
    fun deleteAll(userId: Long): Int

}
