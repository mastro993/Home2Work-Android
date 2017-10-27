package it.gruppoinfor.home2work.models;

import java.util.Date;

public class BookingItem {

    private Long bookingID;
    private MatchItem bookedMatch;
    private Date bookedDate;
    private String notes;

    public BookingItem(Long bookingID, MatchItem bookedMatch, Date bookedDate, String notes) {
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

    public MatchItem getBookedMatch() {
        return bookedMatch;
    }

    public void setBookedMatch(MatchItem bookedMatch) {
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
