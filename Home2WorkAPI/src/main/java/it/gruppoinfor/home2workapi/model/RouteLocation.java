package it.gruppoinfor.home2workapi.model;

import android.arch.persistence.room.Ignore;
import android.location.LocationProvider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


public class RouteLocation implements Serializable {

    @SerializedName("latlng")
    @Expose
    private LatLng latLng;
    @SerializedName("timestamp")
    @Expose
    private Date date;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}