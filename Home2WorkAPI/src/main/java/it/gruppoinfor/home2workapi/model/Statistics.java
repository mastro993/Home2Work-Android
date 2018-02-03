package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Statistics implements Serializable {

    public static final Double KILOMETER_PER_LITRE = 7.5;
    public static final Double EMISSIONS_PER_LITER = 9.0;

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
    @SerializedName("GlobalRanks")
    @Expose
    private Rank globalRanks;
    @SerializedName("CompanyRanks")
    @Expose
    private Rank companyRanks;

}

