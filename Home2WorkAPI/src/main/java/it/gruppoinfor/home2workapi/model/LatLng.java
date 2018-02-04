package it.gruppoinfor.home2workapi.model;

import android.arch.persistence.room.Ignore;
import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class LatLng implements Serializable {

    @SerializedName("latitude")
    @Expose
    private Double lat;
    @SerializedName("longitude")
    @Expose
    private Double lng;

    public LatLng() {
        // Empty constructor
    }

    @Ignore
    public LatLng(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float distanceTo(LatLng latLng) {
        Location dep = new Location("");
        dep.setLatitude(lat);
        dep.setLongitude(lng);
        Location dest = new Location("");
        dest.setLatitude(latLng.getLat());
        dest.setLongitude(latLng.getLng());
        return dep.distanceTo(dest);
    }

    public float distanceTo(Location dest) {
        Location dep = new Location("");
        dep.setLatitude(lat);
        dep.setLongitude(lng);
        return dep.distanceTo(dest);
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
