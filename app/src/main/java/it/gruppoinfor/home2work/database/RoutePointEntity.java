package it.gruppoinfor.home2work.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import it.gruppoinfor.home2work.utils.LatLngConverter;
import it.gruppoinfor.home2workapi.model.LatLng;

@Entity(tableName = "routePoint")
public class RoutePointEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    @TypeConverters(LatLngConverter.class)
    private LatLng latLng;
    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
