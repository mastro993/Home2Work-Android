package it.gruppoinfor.home2work.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MatchPreferences {

    @SerializedName("max_time")
    @Expose
    private int maxTime;
    @SerializedName("max_distance")
    @Expose
    private int maxDistance;
    @SerializedName("min_score")
    @Expose
    private int minScore;

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

}
