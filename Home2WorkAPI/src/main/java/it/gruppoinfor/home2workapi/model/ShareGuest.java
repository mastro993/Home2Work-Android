package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class ShareGuest {

    public static final int JOINED = 0;
    public static final int COMPLETED = 1;
    public static final int CANCELED = 2;

    @SerializedName("ShareId")
    @Expose
    private long shareId;
    @SerializedName("Guest")
    @Expose
    private User guest;
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
    private int status;
    @SerializedName("Distance")
    @Expose
    private int distance;

    public long getShareId() {
        return shareId;
    }

    public void setShareId(long shareId) {
        this.shareId = shareId;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
