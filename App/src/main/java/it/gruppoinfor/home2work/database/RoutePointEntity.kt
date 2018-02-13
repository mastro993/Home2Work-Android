package it.gruppoinfor.home2work.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters

import it.gruppoinfor.home2work.utils.LatLngConverter
import it.gruppoinfor.home2workapi.model.LatLng

@Entity(tableName = "routePoint")
class RoutePointEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var userId: Long = 0
    @TypeConverters(LatLngConverter::class)
    var latLng: LatLng? = null
    var timestamp: Long = 0
}
