package it.gruppoinfor.home2workapi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserProfile implements Serializable {

    @SerializedName("Regdate")
    @Expose
    private Date registrationDate;

    @SerializedName("Exp")
    @Expose
    private Experience exp;
    @SerializedName("Stats")
    @Expose
    private Statistics stats;
    @SerializedName("Achievements")
    @Expose
    private List<Achievement> achievements;

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Experience getExp() {
        return exp;
    }

    public void setExp(Experience exp) {
        this.exp = exp;
    }

    public Statistics getStats() {
        return stats;
    }

    public void setStats(Statistics stats) {
        this.stats = stats;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }
}
