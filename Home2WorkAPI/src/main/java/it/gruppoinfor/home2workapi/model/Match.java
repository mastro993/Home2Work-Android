package it.gruppoinfor.home2workapi.model;

import java.util.Date;

public class Match {

    private Long matchID;
    private User guest;
    private User host;
    private Double sharedDistance;
    private Date arrivalTime;
    private Date departureTime;
    private Integer score;
    private Boolean _new;
    private Boolean hidden;

    public Match(Long matchID, User guest, User host, Double sharedDistance, Date arrivalTime, Date departureTime, Integer score, Boolean _new, Boolean hidden) {
        this.matchID = matchID;
        this.guest = guest;
        this.host = host;
        this.sharedDistance = sharedDistance;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.score = score;
        this._new = _new;
        this.hidden = hidden;
    }

    public Match() {
    }

    public Double getSharedDistance() {
        return sharedDistance;
    }

    public void setSharedDistance(Double sharedDistance) {
        this.sharedDistance = sharedDistance;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Long getMatchID() {
        return matchID;
    }

    public void setMatchID(Long matchID) {
        this.matchID = matchID;
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

    public Boolean isNew() {
        return _new;
    }

    public void setNew(Boolean _new) {
        this._new = _new;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
