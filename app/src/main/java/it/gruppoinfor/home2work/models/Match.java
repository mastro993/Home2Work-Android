package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Match implements Parcelable {

    public final static Parcelable.Creator<Match> CREATOR = new Creator<Match>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Match createFromParcel(Parcel in) {
            Match instance = new Match();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.guest = ((User) in.readValue((User.class.getClassLoader())));
            instance.host = ((User) in.readValue((User.class.getClassLoader())));
            instance.score = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.length = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.cunsumption = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.emission = ((Double) in.readValue((Double.class.getClassLoader())));
            instance._new = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.hidden = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            in.readList(instance.route, (RoutePoint.class.getClassLoader()));
            return instance;
        }

        public Match[] newArray(int size) {
            return (new Match[size]);
        }

    };
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
    private Integer score;
    @SerializedName("length")
    @Expose
    private Double length;
    @SerializedName("cunsumption")
    @Expose
    private Double cunsumption;
    @SerializedName("emission")
    @Expose
    private Double emission;
    @SerializedName("new")
    @Expose
    private Boolean _new;
    @SerializedName("hidden")
    @Expose
    private Boolean hidden;
    @SerializedName("route")
    @Expose
    private List<RoutePoint> route = new ArrayList<>();

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
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

    public List<RoutePoint> getRoute() {
        return route;
    }

    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(guest);
        dest.writeValue(host);
        dest.writeValue(score);
        dest.writeValue(length);
        dest.writeValue(cunsumption);
        dest.writeValue(emission);
        dest.writeValue(_new);
        dest.writeValue(hidden);
        dest.writeList(route);
    }

    public int describeContents() {
        return 0;
    }

}