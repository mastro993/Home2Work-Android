package it.gruppoinfor.home2workapi.model;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.BookingStatus;

public class Booking {

    private Long bookingID;
    private Match bookedMatch;
    private Date bookedDate;
    private Date creationDate;
    private BookingStatus bookingStatus;

    public Booking(Long bookingID, Match bookedMatch, Date bookedDate, Date creationDate, BookingStatus bookingStatus) {
        this.bookingID = bookingID;
        this.bookedMatch = bookedMatch;
        this.bookedDate = bookedDate;
        this.creationDate = creationDate;
        this.bookingStatus = bookingStatus;
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

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
