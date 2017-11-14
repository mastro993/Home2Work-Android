package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Match {

    @SerializedName("matchID")
    @Expose
    private Long matchID;
    @SerializedName("guest")
    @Expose
    private User guest;
    @SerializedName("host")
    @Expose
    private User host;
    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("sharedDistance")
    @Expose
    private Double sharedDistance;
    @SerializedName("arrivalTime")
    @Expose
    private Date arrivalTime;
    @SerializedName("departureTime")
    @Expose
    private Date departureTime;
    @SerializedName("new")
    @Expose
    private Boolean _new;
    @SerializedName("hidden")
    @Expose
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
