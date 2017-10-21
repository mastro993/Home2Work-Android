package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Company implements Parcelable {

    public final static Parcelable.Creator<Company> CREATOR = new Creator<Company>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Company createFromParcel(Parcel in) {
            Company instance = new Company();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((LatLng) in.readValue((LatLng.class.getClassLoader())));
            instance.address = ((Address) in.readValue((Address.class.getClassLoader())));
            instance.website = ((String) in.readValue((String.class.getClassLoader())));
            instance.karma = ((Karma) in.readValue((Karma.class.getClassLoader())));
            instance.stats = ((Statistics) in.readValue((Statistics.class.getClassLoader())));
            return instance;
        }

        public Company[] newArray(int size) {
            return (new Company[size]);
        }

    };

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
    @SerializedName("karma")
    @Expose
    private Karma karma;
    @SerializedName("stats")
    @Expose
    private Statistics stats;

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

    public Karma getKarma() {
        return karma;
    }

    public void setKarma(Karma karma) {
        this.karma = karma;
    }

    public Statistics getStats() {
        return stats;
    }

    public void setStats(Statistics stats) {
        this.stats = stats;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(location);
        dest.writeValue(address);
        dest.writeValue(website);
        dest.writeValue(karma);
        dest.writeValue(stats);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return name + " (" + address.getCity() + ")";
    }
}