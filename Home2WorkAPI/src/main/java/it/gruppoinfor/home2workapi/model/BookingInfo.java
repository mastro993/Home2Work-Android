package it.gruppoinfor.home2workapi.model;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.BookingStatus;

public class BookingInfo extends Booking {

    private String notes;
    private MatchInfo matchInfo;

    public BookingInfo(Long bookingID, MatchInfo bookedMatch, Date bookedDate, Date creationDate, BookingStatus bookingStatus, String notes) {
        super(bookingID, bookedMatch, bookedDate, creationDate, bookingStatus);
        this.matchInfo = bookedMatch;
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MatchInfo getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(MatchInfo matchInfo) {
        this.matchInfo = matchInfo;
    }
}
