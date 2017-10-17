package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Route implements Parcelable {

    public final static Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Route createFromParcel(Parcel in) {
            Route instance = new Route();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            in.readList(instance.points, (RoutePoint.class.getClassLoader()));
            instance.date = ((Date) in.readValue((Date.class.getClassLoader())));
            instance.userID = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.duration = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.distance = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.consumption = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.emission = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.matchable = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.hidden = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.frequence = ((Long) in.readValue((Long.class.getClassLoader())));
            return instance;
        }

        public Route[] newArray(int size) {
            return (new Route[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("points")
    @Expose
    private List<RoutePoint> points = new ArrayList<>();
    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("userID")
    @Expose
    private Long userID;
    @SerializedName("duration")
    @Expose
    private Long duration;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("consumption")
    @Expose
    private Double consumption;
    @SerializedName("emission")
    @Expose
    private Double emission;
    @SerializedName("matchable")
    @Expose
    private boolean matchable;
    @SerializedName("hidden")
    @Expose
    private boolean hidden;
    @SerializedName("frequence")
    @Expose
    private Long frequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RoutePoint> getPoints() {
        return points;
    }

    public void setPoints(List<RoutePoint> points) {
        this.points = points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getConsumption() {
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

    public boolean getMatchable() {
        return matchable;
    }

    public void setMatchable(boolean matchable) {
        this.matchable = matchable;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Long getFrequence() {
        return frequence;
    }

    public void setFrequence(Long frequence) {
        this.frequence = frequence;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(points);
        dest.writeValue(date);
        dest.writeValue(userID);
        dest.writeValue(duration);
        dest.writeValue(distance);
        dest.writeValue(consumption);
        dest.writeValue(emission);
        dest.writeValue(matchable);
        dest.writeValue(hidden);
        dest.writeValue(frequence);
    }

    public int describeContents() {
        return 0;
    }

}