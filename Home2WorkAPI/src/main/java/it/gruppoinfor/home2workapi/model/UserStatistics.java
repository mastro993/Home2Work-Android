package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatistics {


    public static final Double KILOMETER_PER_LITRE = 7.5;
    public static final Double EMISSIONS_PER_LITER = 9.0;

    @SerializedName("Shares")
    @Expose
    private Integer shares;
    @SerializedName("SharedKilometers")
    @Expose
    private Double sharedKilometers;
    @SerializedName("GasSaved")
    @Expose
    private Double gasSaved;
    @SerializedName("EmissionsSaved")
    @Expose
    private Double emissionSaved;

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public Double getSharedKilometers() {
        return sharedKilometers;
    }

    public void setSharedKilometers(Double sharedKilometers) {
        this.sharedKilometers = sharedKilometers;
    }

    public Double getGasSaved() {
        return gasSaved;
    }

    public void setGasSaved(Double gasSaved) {
        this.gasSaved = gasSaved;
    }

    public Double getEmissionSaved() {
        return emissionSaved;
    }

    public void setEmissionSaved(Double emissionSaved) {
        this.emissionSaved = emissionSaved;
    }
}
