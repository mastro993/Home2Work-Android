package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MatchInfo extends Match {

    @SerializedName("consumption")
    @Expose
    private Double consumption;
    @SerializedName("emission")
    @Expose
    private Double emission;
    @SerializedName("startLocation")
    @Expose
    private LatLng startLocation;
    @SerializedName("endLocation")
    @Expose
    private LatLng endLocation;
    @SerializedName("route")
    @Expose
    private List<RoutePoint> route;

    public Double getCunsumption() {
        return consumption;
    }

    public void setConsumption(Double consumption) {
        this.consumption = consumption;
    }

    public Double getEmission() {
        return emission;
    }

    public void setEmission(Double emission) {
        this.emission = emission;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public List<RoutePoint> getRoute() {
        return route;
    }

    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }
}