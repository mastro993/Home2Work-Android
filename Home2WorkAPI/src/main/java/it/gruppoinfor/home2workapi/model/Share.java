package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Share {

    @SerializedName("Id")
    @Expose
    private Long id;
    @SerializedName("Host")
    @Expose
    private User host;
    @SerializedName("Status")
    @Expose
    private int status;
    @SerializedName("Date")
    @Expose
    private Timestamp date;
    @SerializedName("Guests")
    @Expose
    private ArrayList<ShareGuest> guests = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public ArrayList<ShareGuest> getGuests() {
        return guests;
    }

    public void setGuests(ArrayList<ShareGuest> guests) {
        this.guests = guests;
    }
}
