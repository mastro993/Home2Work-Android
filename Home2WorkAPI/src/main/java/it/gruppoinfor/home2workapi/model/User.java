package it.gruppoinfor.home2workapi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import it.gruppoinfor.home2workapi.HomeToWorkClient;


public class User implements Serializable {

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

    @SerializedName("Exp")
    @Expose
    private Long exp;

    @SerializedName("Regdate")
    @Expose
    private Date registrationDate;

    @SerializedName("Configured")
    @Expose
    private Boolean configured;

    @SerializedName("Facebook")
    @Expose
    private String facebook;
    @SerializedName("Twitter")
    @Expose
    private String twitter;
    @SerializedName("Telegram")
    @Expose
    private String telegram;


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

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

////////////////

    public String getAvatarURL() {
        return HomeToWorkClient.AVATAR_BASE_URL + id + ".jpg";
    }

    private String getFormattedName() {
        return name + " " + surname;
    }

    @Override
    public String toString() {
        return getFormattedName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User){
            User user = (User) obj;
            return id.equals(user.getId());
        }
        return  false;
    }
}
