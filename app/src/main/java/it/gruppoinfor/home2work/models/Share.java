package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Share implements Parcelable {

    public final static Parcelable.Creator<Share> CREATOR = new Creator<Share>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Share createFromParcel(Parcel in) {
            Share instance = new Share();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.match = ((Match) in.readValue((Match.class.getClassLoader())));
            instance.karma = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.date = ((Date) in.readValue((Date.class.getClassLoader())));
            return instance;
        }

        public Share[] newArray(int size) {
            return (new Share[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("match")
    @Expose
    private Match match;
    @SerializedName("karma")
    @Expose
    private Double karma;
    @SerializedName("date")
    @Expose
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Double getKarma() {
        return karma;
    }

    public void setKarma(Double karma) {
        this.karma = karma;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(match);
        dest.writeValue(karma);
        dest.writeValue(date);
    }

    public int describeContents() {
        return 0;
    }

}