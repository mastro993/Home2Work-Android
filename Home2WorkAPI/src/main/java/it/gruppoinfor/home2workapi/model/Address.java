package it.gruppoinfor.home2workapi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {


    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Cap")
    @Expose
    private String postalCode;
    @SerializedName("AddressLine")
    @Expose
    private String address;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        return this.address + ", " + this.postalCode + ", " + this.city;
    }
    
}