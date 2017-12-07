package it.gruppoinfor.home2workapi.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import it.gruppoinfor.home2workapi.Client;


public class User {

    @SerializedName("UserID")
    @Expose
    private Long id;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Token")
    @Expose
    private String token;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Surname")
    @Expose
    private String surname;
    @SerializedName("HomeLatLng")
    @Expose
    private LatLng location;
    @SerializedName("HomeAddress")
    @Expose
    private Address address;
    @SerializedName("Company")
    @Expose
    private Company company;
    @SerializedName("Regdate")
    @Expose
    private Date registrationDate;
    @SerializedName("Configured")
    @Expose
    private Boolean configured;

    // TODO solo per mockup
    public User(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean isConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    public String getAvatarURL() {
        return Client.AVATAR_BASE_URL + id + ".jpg";
    }

    private String getFormattedName() {
        return name + " " + surname;
    }

    @Override
    public String toString() {
        return getFormattedName();
    }
}
