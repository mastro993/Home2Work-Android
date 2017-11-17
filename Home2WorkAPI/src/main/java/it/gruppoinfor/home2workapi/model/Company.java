package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Company {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private LatLng location;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("experience")
    @Expose
    private Long exp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Long getExperience() {
        return exp;
    }

    public void setExperience(Long experience) {
        this.exp = experience;
    }

    @Override
    public String toString() {
        return name + " (" + address.getCity() + ")";
    }
}