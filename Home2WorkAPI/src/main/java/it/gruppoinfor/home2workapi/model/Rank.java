package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rank implements Serializable {
    @SerializedName("Shares")
    @Expose
    private Integer shares;
    @SerializedName("MonthShares")
    @Expose
    private Integer monthShares;
    @SerializedName("LastMonthShares")
    @Expose
    private Integer LastMonthShares;
    @SerializedName("SharedDistance")
    @Expose
    private Integer sharedDistance;
    @SerializedName("MonthSharedDistance")
    @Expose
    private Integer monthSharedDistance;
    @SerializedName("LastMonthSharedDistance")
    @Expose
    private Integer lastMonthSharedDistance;

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public Integer getMonthShares() {
        return monthShares;
    }

    public void setMonthShares(Integer monthShares) {
        this.monthShares = monthShares;
    }

    public Integer getLastMonthShares() {
        return LastMonthShares;
    }

    public void setLastMonthShares(Integer lastMonthShares) {
        LastMonthShares = lastMonthShares;
    }

    public Integer getSharedDistance() {
        return sharedDistance;
    }

    public void setSharedDistance(Integer sharedDistance) {
        this.sharedDistance = sharedDistance;
    }

    public Integer getMonthSharedDistance() {
        return monthSharedDistance;
    }

    public void setMonthSharedDistance(Integer monthSharedDistance) {
        this.monthSharedDistance = monthSharedDistance;
    }

    public Integer getLastMonthSharedDistance() {
        return lastMonthSharedDistance;
    }

    public void setLastMonthSharedDistance(Integer lastMonthSharedDistance) {
        this.lastMonthSharedDistance = lastMonthSharedDistance;
    }
}
