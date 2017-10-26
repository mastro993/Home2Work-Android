package it.gruppoinfor.home2work.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchInfo {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("guest")
    @Expose
    private User guest;
    @SerializedName("host")
    @Expose
    private User host;
    @SerializedName("score")
    @Expose
    private Long score;
    @SerializedName("length")
    @Expose
    private Long length;
    @SerializedName("cunsumption")
    @Expose
    private Long cunsumption;
    @SerializedName("emission")
    @Expose
    private Long emission;
    @SerializedName("new")
    @Expose
    private Boolean _new;
    @SerializedName("hidden")
    @Expose
    private Boolean hidden;
    @SerializedName("route")
    @Expose
    private List<RoutePoint> route = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getCunsumption() {
        return cunsumption;
    }

    public void setCunsumption(Long cunsumption) {
        this.cunsumption = cunsumption;
    }

    public Long getEmission() {
        return emission;
    }

    public void setEmission(Long emission) {
        this.emission = emission;
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

    public List<RoutePoint> getRoute() {
        return route;
    }

    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }

}