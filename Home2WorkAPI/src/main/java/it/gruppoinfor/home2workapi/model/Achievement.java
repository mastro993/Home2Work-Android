package it.gruppoinfor.home2workapi.model;

import java.util.Date;

import it.gruppoinfor.home2workapi.Client;


public class Achievement {

    private Long achievementID;
    private String name;
    private String description;
    private Integer karma;
    private Date unlockDate;
    private Double goal;
    private Double current;

    public Achievement() {
    }

    public Achievement(Long achievementID, String name, String description, Integer karma, Date unlockDate, Double goal, Double current) {
        this.achievementID = achievementID;
        this.name = name;
        this.description = description;
        this.karma = karma;
        this.unlockDate = unlockDate;
        this.goal = goal;
        this.current = current;
    }

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
        return Client.ACHIEVEMENTS_BASE_URL + achievementID + ".jpg";
    }
}
