package it.gruppoinfor.home2workapi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("Exp")
    @Expose
    private Integer exp;
    @SerializedName("Karma")
    @Expose
    private Integer karma;
    @SerializedName("Stats")
    @Expose
    private UserStatistics stats;

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public UserStatistics getStats() {
        return stats;
    }

    public void setStats(UserStatistics stats) {
        this.stats = stats;
    }


}
