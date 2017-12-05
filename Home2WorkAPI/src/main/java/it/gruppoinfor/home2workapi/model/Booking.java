package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.BookingStatus;

public class Booking {

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
}
