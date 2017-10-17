package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Statistics implements Parcelable {

    public final static Parcelable.Creator<Statistics> CREATOR = new Creator<Statistics>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Statistics createFromParcel(Parcel in) {
            Statistics instance = new Statistics();
            instance.routes = ((int) in.readValue((int.class.getClassLoader())));
            instance.distance = ((double) in.readValue((double.class.getClassLoader())));
            instance.duration = ((int) in.readValue((int.class.getClassLoader())));
            instance.consumption = ((double) in.readValue((double.class.getClassLoader())));
            instance.emission = ((double) in.readValue((double.class.getClassLoader())));
            instance.shares = ((int) in.readValue((int.class.getClassLoader())));
            instance.sharedDistance = ((double) in.readValue((double.class.getClassLoader())));
            instance.savedConsumption = ((double) in.readValue((double.class.getClassLoader())));
            instance.savedEmission = ((double) in.readValue((double.class.getClassLoader())));
            return instance;
        }

        public Statistics[] newArray(int size) {
            return (new Statistics[size]);
        }

    };
    @SerializedName("routes")
    @Expose
    private int routes;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("consumption")
    @Expose
    private double consumption;
    @SerializedName("emission")
    @Expose
    private double emission;
    @SerializedName("shares")
    @Expose
    private int shares;
    @SerializedName("shared_distance")
    @Expose
    private double sharedDistance;
    @SerializedName("saved_consumption")
    @Expose
    private double savedConsumption;
    @SerializedName("saved_emission")
    @Expose
    private double savedEmission;

    public int getRoutes() {
        return routes;
    }

    public void setRoutes(int routes) {
        this.routes = routes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getEmission() {
        return emission;
    }

    public void setEmission(double emission) {
        this.emission = emission;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public double getSharedDistance() {
        return sharedDistance;
    }

    public void setSharedDistance(double sharedDistance) {
        this.sharedDistance = sharedDistance;
    }

    public double getSavedConsumption() {
        return savedConsumption;
    }

    public void setSavedConsumption(double savedConsumption) {
        this.savedConsumption = savedConsumption;
    }

    public double getSavedEmission() {
        return savedEmission;
    }

    public void setSavedEmission(double savedEmission) {
        this.savedEmission = savedEmission;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(routes);
        dest.writeValue(distance);
        dest.writeValue(duration);
        dest.writeValue(consumption);
        dest.writeValue(emission);
        dest.writeValue(shares);
        dest.writeValue(sharedDistance);
        dest.writeValue(savedConsumption);
        dest.writeValue(savedEmission);
    }

    public int describeContents() {
        return 0;
    }

}