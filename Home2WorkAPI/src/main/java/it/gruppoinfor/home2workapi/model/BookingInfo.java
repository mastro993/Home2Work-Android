package it.gruppoinfor.home2workapi.model;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.BookingStatus;

public class BookingInfo {

    private Long bookingID;
    private MatchInfo bookedMatch;
    private Date bookedDate;
    private Date creationDate;
    private String notes;
    private BookingStatus bookingStatus;

    public BookingInfo(Long bookingID, MatchInfo bookedMatch, Date bookedDate, Date creationDate, String notes, BookingStatus bookingStatus) {
        this.bookingID = bookingID;
        this.bookedMatch = bookedMatch;
        this.bookedDate = bookedDate;
        this.creationDate = creationDate;
        this.notes = notes;
        this.bookingStatus = bookingStatus;
    }

    public Long getBookingID() {
        return bookingID;
    }

    public void setBookingID(Long bookingID) {
        this.bookingID = bookingID;
    }

    public MatchInfo getBookedMatch() {
        return bookedMatch;
    }

    public void setBookedMatch(MatchInfo bookedMatch) {
        this.bookedMatch = bookedMatch;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
