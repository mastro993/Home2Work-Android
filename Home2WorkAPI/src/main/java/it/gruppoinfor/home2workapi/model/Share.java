package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Date;

public class Share {
    public static final int CREATED = 0;
    public static final int ONGOING = 1;
    public static final int COMPLETED = 2;

    @SerializedName("ID")
    @Expose
    private Long shareID;
    @SerializedName("Booking")
    @Expose
    private Booking booking;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Status")
    @Expose
    private int status;
    @SerializedName("CreationDate")
    @Expose
    private Timestamp creationDate;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
