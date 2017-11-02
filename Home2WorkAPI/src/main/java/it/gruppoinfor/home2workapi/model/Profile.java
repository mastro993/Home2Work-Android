package it.gruppoinfor.home2workapi.model;


public class Profile {
    private Karma karma;
    private ProfileStats profileStats;

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

    public Profile(Karma karma, ProfileStats profileStats) {

        this.karma = karma;
        this.profileStats = profileStats;
    }
}
