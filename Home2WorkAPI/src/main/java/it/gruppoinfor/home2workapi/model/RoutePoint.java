package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoutePoint {

    @SerializedName("location")
    @Expose
    private LatLng latLng;
    @SerializedName("timestamp")
    @Expose
    private Long time;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }



}