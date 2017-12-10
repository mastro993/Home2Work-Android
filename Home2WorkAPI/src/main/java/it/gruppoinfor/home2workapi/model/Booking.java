package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.BookingStatus;

public class Booking {

    public static final int REJECTED = 0;
    public static final int PENDING = 1;
    public static final int ACCEPTED = 2;
    public static final int CANCELED = 3;
    public static final int ONGOING = 4;

    @SerializedName("BookingID")
    @Expose
    private Long bookingID;
    @SerializedName("Match")
    @Expose
    private Match bookedMatch;
    @SerializedName("Date")
    @Expose
    private Date bookedDate;
    @SerializedName("Notes")
    @Expose
    private String notes;
    @SerializedName("Status")
    @Expose
    private int bookingStatus;
    @SerializedName("CreationDate")
    @Expose
    private Date creationDate;
    @SerializedName("Hidden")
    @Expose
    private Boolean hidden;

    public Booking() {
    }

    public Long getBookingID() {
        return bookingID;
    }

    public void setBookingID(Long bookingID) {
        this.bookingID = bookingID;
    }

    public Match getBookedMatch() {
        return bookedMatch;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setBookedMatch(Match bookedMatch) {
        this.bookedMatch = bookedMatch;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
