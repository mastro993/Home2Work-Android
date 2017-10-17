package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Karma implements Parcelable {

    public final static Parcelable.Creator<Karma> CREATOR = new Parcelable.Creator<Karma>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Karma createFromParcel(Parcel in) {
            Karma instance = new Karma();
            instance.karma = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.level = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.forNextLevel = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Karma[] newArray(int size) {
            return (new Karma[size]);
        }

    };
    @SerializedName("karma")
    @Expose
    private Integer karma;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("for_next_level")
    @Expose
    private Integer forNextLevel;

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getForNextLevel() {
        return forNextLevel;
    }

    public void setForNextLevel(Integer forNextLevel) {
        this.forNextLevel = forNextLevel;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(karma);
        dest.writeValue(level);
        dest.writeValue(forNextLevel);
    }

    public int describeContents() {
        return 0;
    }

    public Float getProgress() {
        float c = (float) karma;
        float m = (float) forNextLevel;
        return (100.0f / m) * c;
    }

}
