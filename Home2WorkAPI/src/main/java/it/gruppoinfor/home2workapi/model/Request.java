package it.gruppoinfor.home2workapi.model;

import java.util.Date;

import it.gruppoinfor.home2workapi.enums.RequestStatus;

public class Request {

    private Long requestID;
    private Match requestedMatch;
    private Date requestedDate;
    private RequestStatus requestStatus;

    public Request(Long requestID, Match requestedMatch, Date requestedDate, RequestStatus requestStatus) {
        this.requestID = requestID;
        this.requestedMatch = requestedMatch;
        this.requestedDate = requestedDate;
        this.requestStatus = requestStatus;
    }

    public Long getRequestID() {
        return requestID;
    }

    public void setRequestID(Long requestID) {
        this.requestID = requestID;
    }

    public Match getRequestedMatch() {
        return requestedMatch;
    }

    public void setRequestedMatch(Match requestedMatch) {
        this.requestedMatch = requestedMatch;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
