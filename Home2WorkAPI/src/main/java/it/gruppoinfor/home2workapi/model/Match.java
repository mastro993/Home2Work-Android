package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Match implements Serializable {

    @SerializedName("MatchID")
    @Expose
    private Long matchID;
    @SerializedName("Guest")
    @Expose
    private User guest;
    @SerializedName("Host")
    @Expose
    private User host;
    @SerializedName("Weekdays")
    @Expose
    private ArrayList<Integer> weekdays;
    @SerializedName("Score")
    @Expose
    private Integer score;
    @SerializedName("Distance")
    @Expose
    private Integer distance;
    @SerializedName("StartLocation")
    @Expose
    private  LatLng startLocation;
    @SerializedName("StartTime")
    @Expose
    private Timestamp startTime;
    @SerializedName("EndLocation")
    @Expose
    private LatLng endLocation;
    @SerializedName("EndTime")
    @Expose
    private Timestamp endTime;
    @SerializedName("New")
    @Expose
    private Boolean _new;
    @SerializedName("Hidden")
    @Expose
    private Boolean hidden;

    public Match() {
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

    public ArrayList<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Boolean isNew() {
        return _new;
    }

    public void setNew(Boolean _new) {
        this._new = _new;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
