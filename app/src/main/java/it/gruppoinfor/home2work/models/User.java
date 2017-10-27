package it.gruppoinfor.home2work.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.gruppoinfor.home2work.api.Client;


public class User {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("location")
    @Expose
    private LatLng location;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("registration_date")
    @Expose
    private String registrationDate;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("configured")
    @Expose
    private Boolean configured;
    @SerializedName("match_preferences")
    @Expose
    private UserMatchPreferences matchPreferences;

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

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Boolean isConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
    }

    public UserMatchPreferences getMatchPreferences() {
        return matchPreferences;
    }

    public void setMatchPreferences(UserMatchPreferences matchPreferences) {
        this.matchPreferences = matchPreferences;
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
