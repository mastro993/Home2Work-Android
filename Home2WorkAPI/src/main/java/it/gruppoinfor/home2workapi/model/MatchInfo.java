package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MatchInfo extends Match {

    private Double cunsumption;
    private Double emission;
    private LatLng startLocation;
    private LatLng endLocation;
    private List<RoutePoint> route = new ArrayList<>();

    public Double getCunsumption() {
        return cunsumption;
    }

    public void setCunsumption(Double cunsumption) {
        this.cunsumption = cunsumption;
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