package it.gruppoinfor.home2workapi.model;

import android.arch.persistence.room.Ignore;

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

    @Ignore
    public LatLng(com.google.android.gms.maps.model.LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

    public com.google.android.gms.maps.model.LatLng toLatLng() {
        return new com.google.android.gms.maps.model.LatLng(lat, lng);
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
