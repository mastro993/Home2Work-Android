package it.gruppoinfor.home2work.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.gruppoinfor.home2work.models.Achievement;
import it.gruppoinfor.home2work.models.Address;
import it.gruppoinfor.home2work.models.Contacts;
import it.gruppoinfor.home2work.models.Job;
import it.gruppoinfor.home2work.models.Karma;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.Route;
import it.gruppoinfor.home2work.models.Share;
import it.gruppoinfor.home2work.models.Statistics;


public class Account {

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
    private Date registrationDate;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("contacts")
    @Expose
    private Contacts contacts;
    @SerializedName("karma")
    @Expose
    private Karma karma;
    @SerializedName("statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("configured")
    @Expose
    private Boolean configured;
    transient private List<Match> matches = new ArrayList<>();
    transient private List<Route> routes = new ArrayList<>();
    transient private List<Share> shares = new ArrayList<>();
    transient private List<Achievement> achievements = new ArrayList<>();

    @Override
    public String toString() {
        return getFormattedName();
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

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public Karma getKarma() {
        return karma;
    }

    public void setKarma(Karma karma) {
        this.karma = karma;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Boolean isConfigured() {
        return configured;
    }

    public void setConfigured(Boolean configured) {
        this.configured = configured;
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

    public int describeContents() {
        return 0;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public String getAvatarURL() {
        return APIClient.AVATAR_BASE_URL + id + ".jpg";
    }

    private String getFormattedName() {
        return name + " " + surname;
    }

}
