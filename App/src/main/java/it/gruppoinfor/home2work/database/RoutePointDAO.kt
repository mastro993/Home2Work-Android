package it.gruppoinfor.home2work.database


import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import java.util.Observable

import io.reactivex.Single

@Dao
interface RoutePointDAO {

    @Query("SELECT * FROM routePoint WHERE userId = :arg0 ORDER BY timestamp DESC")
    fun getAllUserPoints(userId: Long): Flowable<List<RoutePointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routePointEntity: RoutePointEntity): Long?

    @Query("DELETE FROM routePoint WHERE userId = :arg0")
    fun deleteAll(userId: Long): Int

}
