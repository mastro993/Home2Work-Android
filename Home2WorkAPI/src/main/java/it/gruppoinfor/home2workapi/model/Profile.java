package it.gruppoinfor.home2workapi.model;


import java.util.ArrayList;
import java.util.List;

public class Profile {

    private Karma karma;
    private ProfileStats profileStats;
    private List<Achievement> achievements = new ArrayList<>();
    private List<Share> shares = new ArrayList<>();

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

    public Karma getKarma() {
        return karma;
    }

    public void setKarma(Karma karma) {
        this.karma = karma;
    }

    public ProfileStats getProfileStats() {
        return profileStats;
    }

    public void setProfileStats(ProfileStats profileStats) {
        this.profileStats = profileStats;
    }

    public Profile(Karma karma, ProfileStats profileStats, List<Achievement> achievements, List<Share> shares) {
        this.karma = karma;
        this.profileStats = profileStats;
        this.achievements = achievements;
        this.shares = shares;
    }
}
