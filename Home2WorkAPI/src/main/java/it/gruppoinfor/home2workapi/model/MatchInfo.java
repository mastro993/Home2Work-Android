package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchInfo {

    private Long matchId;
    private User guest;
    private User host;
    private Integer score;
    private Double sharedDistance;
    private Double cunsumption;
    private Double emission;
    private LatLng startLocation;
    private Date departureTime;
    private LatLng endLocation;
    private Date arrivalTime;
    private List<RoutePoint> route = new ArrayList<>();

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getSharedDistance() {
        return sharedDistance;
    }

    public void setSharedDistance(Double sharedDistance) {
        this.sharedDistance = sharedDistance;
    }

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

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<RoutePoint> getRoute() {
        return route;
    }

    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }
}