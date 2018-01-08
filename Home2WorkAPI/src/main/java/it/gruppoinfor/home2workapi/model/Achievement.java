package it.gruppoinfor.home2workapi.model;

import java.io.Serializable;
import java.util.Date;

import it.gruppoinfor.home2workapi.Home2WorkClient;


public class Achievement implements Serializable {

    private Long achievementID;
    private String name;
    private String description;
    private Integer karma;
    private Integer exp;
    private Date unlockDate;
    private Double goal;
    private Double current;

    public Long getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(Long achievementID) {
        this.achievementID = achievementID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getKarma() {
        return karma;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Date getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(Date unlockDate) {
        this.unlockDate = unlockDate;
    }

    public Double getGoal() {
        return goal;
    }

    public void setGoal(Double goal) {
        this.goal = goal;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Integer getProgress() {
        return Double.valueOf(((100.0 / goal) * current)).intValue();
    }

    public String getIconURL() {
        return Home2WorkClient.ACHIEVEMENTS_BASE_URL + achievementID + ".jpg";
    }
}
