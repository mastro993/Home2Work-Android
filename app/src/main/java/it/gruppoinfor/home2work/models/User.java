package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.gruppoinfor.home2work.api.Client;


public class User implements Parcelable {

    public static final String[] profilePrivacy = {
            "Pubblica",
            "Solo match"
    };
    public final static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @SuppressWarnings({
                "unchecked"
        })
        public User createFromParcel(Parcel in) {
            User instance = new User();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.email = ((String) in.readValue((String.class.getClassLoader())));
            instance.token = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.surname = ((String) in.readValue((String.class.getClassLoader())));
            instance.homeLoc = ((LatLng) in.readValue((LatLng.class.getClassLoader())));
            instance.job = ((Job) in.readValue((Job.class.getClassLoader())));
            instance.registrationDate = ((Date) in.readValue((Date.class.getClassLoader())));
            instance.fcmToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.privacy = ((int) in.readValue((int.class.getClassLoader())));
            instance.contacts = ((Contacts) in.readValue((Contacts.class.getClassLoader())));
            instance.karma = ((Karma) in.readValue((Karma.class.getClassLoader())));
            instance.statistics = ((Statistics) in.readValue((Statistics.class.getClassLoader())));
            instance.configured = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            in.readList(instance.followersId, (java.lang.Long.class.getClassLoader()));
            in.readList(instance.followingId, (java.lang.Long.class.getClassLoader()));
            return instance;
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    };
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
    @SerializedName("home_loc")
    @Expose
    private LatLng homeLoc;
    @SerializedName("job")
    @Expose
    private Job job;
    @SerializedName("registration_date")
    @Expose
    private Date registrationDate;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("privacy")
    @Expose
    private int privacy;
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
    @SerializedName("followersId")
    @Expose
    private List<Long> followersId = new ArrayList<>();
    @SerializedName("followingId")
    @Expose
    private List<Long> followingId = new ArrayList<>();
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

    public LatLng getHomeLoc() {
        return homeLoc;
    }

    public void setHomeLoc(LatLng homeLoc) {
        this.homeLoc = homeLoc;
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

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
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

    public List<Long> getFollowersId() {
        return followersId;
    }

    public void setFollowersId(List<Long> followersId) {
        this.followersId = followersId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(email);
        dest.writeValue(token);
        dest.writeValue(name);
        dest.writeValue(surname);
        dest.writeValue(homeLoc);
        dest.writeValue(job);
        dest.writeValue(registrationDate);
        dest.writeValue(fcmToken);
        dest.writeValue(privacy);
        dest.writeValue(contacts);
        dest.writeValue(karma);
        dest.writeValue(statistics);
        dest.writeValue(configured);
        dest.writeList(followersId);
        dest.writeList(followingId);
    }


    //______________________________________________________________________________________________
    //______________________________________________________________________________________________
    //______________________________________________________________________________________________


    public List<Long> getFollowingId() {
        return followingId;
    }

    public void setFollowingId(List<Long> followingId) {
        this.followingId = followingId;
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
        return Client.AVATAR_BASE_URL + id + ".jpg";
    }

    private String getFormattedName() {
        return name + " " + surname;
    }

    public static class Contacts implements Parcelable {

        public final static Parcelable.Creator<Contacts> CREATOR = new Creator<Contacts>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Contacts createFromParcel(Parcel in) {
                Contacts instance = new Contacts();
                instance.phone = ((String) in.readValue((String.class.getClassLoader())));
                instance.facebook = ((String) in.readValue((String.class.getClassLoader())));
                instance.twitter = ((String) in.readValue((String.class.getClassLoader())));
                instance.telegram = ((String) in.readValue((String.class.getClassLoader())));
                return instance;
            }

            public Contacts[] newArray(int size) {
                return (new Contacts[size]);
            }

        };
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("facebook")
        @Expose
        private String facebook;
        @SerializedName("twitter")
        @Expose
        private String twitter;
        @SerializedName("telegram")
        @Expose
        private String telegram;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(phone);
            dest.writeValue(facebook);
            dest.writeValue(twitter);
            dest.writeValue(telegram);
        }

        public int describeContents() {
            return 0;
        }

    }

    public static class Job implements Parcelable {

        public final static Parcelable.Creator<Job> CREATOR = new Creator<Job>() {


            @SuppressWarnings({
                    "unchecked"
            })
            public Job createFromParcel(Parcel in) {
                Job instance = new Job();
                instance.company = ((Company) in.readValue((Company.class.getClassLoader())));
                instance.start = ((long) in.readValue((long.class.getClassLoader())));
                instance.end = ((long) in.readValue((long.class.getClassLoader())));
                return instance;
            }

            public Job[] newArray(int size) {
                return (new Job[size]);
            }

        };
        @SerializedName("company")
        @Expose
        private Company company;
        @SerializedName("start")
        @Expose
        private long start;
        @SerializedName("end")
        @Expose
        private long end;

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(company);
            dest.writeValue(start);
            dest.writeValue(end);
        }

        public int describeContents() {
            return 0;
        }

    }
}
