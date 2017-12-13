package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Date;

public class Share {
    public static final int CREATED = 0;
    public static final int TOJOB = 1;
    public static final int ARRIVED = 2;
    public static final int TOHOME = 3;
    public static final int COMPLETED = 4;

    @SerializedName("ID")
    @Expose
    private Long shareID;
    @SerializedName("Booking")
    @Expose
    private Booking booking;
    @SerializedName("CreationDate")
    @Expose
    private Timestamp creationDate;
    @SerializedName("Status")
    @Expose
    private int status;

    public Long getShareID() {
        return shareID;
    }

    public void setShareID(Long shareID) {
        this.shareID = shareID;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
}
