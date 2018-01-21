package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Share implements Serializable {

    @SerializedName("Id")
    @Expose
    private Long id;
    @SerializedName("Host")
    @Expose
    private User host;
    @SerializedName("Status")
    @Expose
    private Status status;
    @SerializedName("Date")
    @Expose
    private Timestamp date;
    @SerializedName("Type")
    @Expose
    private Type type;
    @SerializedName("Guests")
    @Expose
    private ArrayList<Guest> guests = new ArrayList<>();

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Guest> getGuests() {
        return guests;
    }

    public void setGuests(ArrayList<Guest> guests) {
        this.guests = guests;
    }

    public enum Status {

        @SerializedName("0")
        CREATED(0),
        @SerializedName("1")
        COMPLETED(1),
        @SerializedName("2")
        CANCELED(2);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Type {
        @SerializedName("0")
        DRIVER(0),

        @SerializedName("1")
        GUEST(1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
