package it.gruppoinfor.home2workapi.model;

import java.util.Date;

public class ProfileStats {


    private final Double GAS_PER_KILOMETER = 7.5;
    private final Double EMISSIONS_PER_LITER = 9.0;

    private Date regDate;
    private Double totalKilometers;
    private Double totalGas;
    private Double totalEmissions;
    private Integer totalShares;
    private Double totalSharedKilometers;
    private Double totalGasSaved;
    private Double totalEmissionSaved;

    public ProfileStats(Date regDate, Double totalKilometers, Integer totalShares, Double totalSharedKilometers) {
        this.regDate = regDate;
        this.totalKilometers = totalKilometers;
        this.totalGas = (totalKilometers / 100.0) * 7.5;
        this.totalEmissions = this.totalGas * 9.0;
        this.totalShares = totalShares;
        this.totalSharedKilometers = totalSharedKilometers;
        this.totalGasSaved = (totalSharedKilometers / 100.0) * GAS_PER_KILOMETER;
        this.totalEmissionSaved = this.totalGasSaved * EMISSIONS_PER_LITER;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Double getTotalKilometers() {
        return totalKilometers;
    }

    public void setTotalKilometers(Double totalKilometers) {
        this.totalKilometers = totalKilometers;
    }

    public Double getTotalGas() {
        return totalGas;
    }

    public void setTotalGas(Double totalGas) {
        this.totalGas = totalGas;
    }

    public Double getTotalEmissions() {
        return totalEmissions;
    }

    public void setTotalEmissions(Double totalEmissions) {
        this.totalEmissions = totalEmissions;
    }

    public Integer getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Double getTotalSharedKilometers() {
        return totalSharedKilometers;
    }

    public void setTotalSharedKilometers(Double totalSharedKilometers) {
        this.totalSharedKilometers = totalSharedKilometers;
    }

    public Double getTotalGasSaved() {
        return totalGasSaved;
    }

    public void setTotalGasSaved(Double totalGasSaved) {
        this.totalGasSaved = totalGasSaved;
    }

    public Double getTotalEmissionSaved() {
        return totalEmissionSaved;
    }

    public void setTotalEmissionSaved(Double totalEmissionSaved) {
        this.totalEmissionSaved = totalEmissionSaved;
    }
}
