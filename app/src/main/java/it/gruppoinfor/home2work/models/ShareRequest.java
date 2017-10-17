package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ShareRequest implements Parcelable {

    public final static Parcelable.Creator<ShareRequest> CREATOR = new Creator<ShareRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ShareRequest createFromParcel(Parcel in) {
            ShareRequest instance = new ShareRequest();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.matchId = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.date = ((Date) in.readValue((Date.class.getClassLoader())));
            instance.status = in.readValue((Object.class.getClassLoader()));
            return instance;
        }

        public ShareRequest[] newArray(int size) {
            return (new ShareRequest[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("matchId")
    @Expose
    private Long matchId;
    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("status")
    @Expose
    private Object status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(matchId);
        dest.writeValue(date);
        dest.writeValue(status);
    }

    public int describeContents() {
        return 0;
    }

}