package it.gruppoinfor.home2workapi.model;


import java.util.ArrayList;
import java.util.List;

public class Profile {

    private Integer exp;
    private Integer karma;
    private ProfileStats profileStats;
    private List<Achievement> achievements = new ArrayList<>();
    private List<Share> shares = new ArrayList<>();

    private Integer expLevel;
    private Integer expToNextLevel;
    private Float expLevelProgress;

    public Profile(Integer exp, Integer karma, ProfileStats profileStats, List<Achievement> achievements, List<Share> shares) {
        this.exp = exp;

        this.expLevel = ((Double) (1 + 0.10 * Math.sqrt(exp))).intValue();

        int thisLevelExp = (int) Math.pow(10.0 * (this.expLevel - 1.0), 2.0);
        int nextLevelExp = (int) Math.pow(10 * this.expLevel, 2.0);
        this.expToNextLevel =  nextLevelExp - thisLevelExp;

        int expDelta = exp - this.expToNextLevel;
        this.expLevelProgress = (100.0f / this.expToNextLevel) * expDelta;

        this.karma = karma;
        this.profileStats = profileStats;
        this.achievements = achievements;
        this.shares = shares;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

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

    public ProfileStats getProfileStats() {
        return profileStats;
    }

    public void setProfileStats(ProfileStats profileStats) {
        this.profileStats = profileStats;
    }

    ////////////////


    public Integer getExpLevel() {
        return expLevel;
    }

    public Integer getExpToNextLevel() {
        return expToNextLevel;
    }

    public Float getExpLevelProgress() {
        return expLevelProgress;
    }
}
