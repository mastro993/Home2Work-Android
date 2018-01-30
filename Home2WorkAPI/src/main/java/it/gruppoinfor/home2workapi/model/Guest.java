package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class Guest implements Serializable {

    @SerializedName("ShareId")
    @Expose
    private long shareId;
    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("StartLocation")
    @Expose
    private LatLng startLocation;
    @SerializedName("StartDate")
    @Expose
    private Timestamp startTime;
    @SerializedName("EndLocation")
    @Expose
    private LatLng endLocation;
    @SerializedName("EndDate")
    @Expose
    private Timestamp endTime;
    @SerializedName("Status")
    @Expose
    private Status status;
    @SerializedName("Distance")
    @Expose
    private int distance;

    public long getShareId() {
        return shareId;
    }

    public void setShareId(long shareId) {
        this.shareId = shareId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User guest) {
        this.user = guest;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public enum Status {

        @SerializedName("0")
        JOINED(0),
        @SerializedName("1")
        COMPLETED(1),
        @SerializedName("2")
        CANCELED(2);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
