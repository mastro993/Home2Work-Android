package it.gruppoinfor.home2work.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchItem {

    @SerializedName("matchID")
    @Expose
    private Long matchID;
    @SerializedName("guestID")
    @Expose
    private Long guestID;
    @SerializedName("guest")
    @Expose
    private User hostUser;
    @SerializedName("type")
    @Expose
    private Long type;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("score")
    @Expose
    private Long score;
    @SerializedName("new")
    @Expose
    private int _new;

    public Long getMatchID() {
        return matchID;
    }

    public void setMatchID(Long matchID) {
        this.matchID = matchID;
    }

    public Long getGuestID() {
        return guestID;
    }

    public void setGuestID(Long guestID) {
        this.guestID = guestID;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Boolean isNew() {
        return _new == 1;
    }

    public void setNew(Boolean isNew) {
        if(isNew) this._new = 1;
        else this._new = 0;
    }

    public User getHostUser() {
        return hostUser;
    }

    public void setHostUser(User guest) {
        this.hostUser = guest;
    }
}
