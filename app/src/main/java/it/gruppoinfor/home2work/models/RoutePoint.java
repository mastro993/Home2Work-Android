package it.gruppoinfor.home2work.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoutePoint implements Parcelable {

    @SerializedName("location")
    @Expose
    private LatLng latLng;
    @SerializedName("time")
    @Expose
    private Long time;
    public final static Parcelable.Creator<RoutePoint> CREATOR = new Creator<RoutePoint>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RoutePoint createFromParcel(Parcel in) {
            RoutePoint instance = new RoutePoint();
            instance.latLng = ((LatLng) in.readValue((LatLng.class.getClassLoader())));
            instance.time = ((Long) in.readValue((Long.class.getClassLoader())));
            return instance;
        }

        public RoutePoint[] newArray(int size) {
            return (new RoutePoint[size]);
        }

    };

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(latLng);
        dest.writeValue(time);
    }

    public int describeContents() {
        return 0;
    }


}