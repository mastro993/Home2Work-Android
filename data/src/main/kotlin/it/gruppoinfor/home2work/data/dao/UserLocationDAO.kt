package it.gruppoinfor.home2work.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import it.gruppoinfor.home2work.data.entities.UserLocationData
import it.gruppoinfor.home2work.domain.entities.UserLocationEntity

@Dao
interface UserLocationDAO {

    @Insert
    fun saveUserLocation(userLocationData: UserLocationData): Long

    @Query("SELECT * FROM user_location WHERE userId = :userId")
    fun getUserLocations(userId: Long): Maybe<List<UserLocationData>>

    @Query("DELETE FROM user_location WHERE userId = :userId")
    fun deleteUserLocations(userId: Long):Int

}