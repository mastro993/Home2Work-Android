package it.gruppoinfor.home2workapi.model;

import java.util.Date;

public class Booking {

    private Long bookingID;
    private Match bookedMatch;
    private Date bookedDate;
    private String notes;

    public Booking(Long bookingID, Match bookedMatch, Date bookedDate, String notes) {
        this.bookingID = bookingID;
        this.bookedMatch = bookedMatch;
        this.bookedDate = bookedDate;
        this.notes = notes;
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

    public void setBookedMatch(Match bookedMatch) {
        this.bookedMatch = bookedMatch;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
