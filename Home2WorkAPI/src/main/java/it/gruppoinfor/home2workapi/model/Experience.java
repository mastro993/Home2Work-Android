package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Experience {
    @SerializedName("Amount")
    @Expose
    private Integer amount;
    @SerializedName("Level")
    @Expose
    private Integer level;
    @SerializedName("Progress")
    @Expose
    private float progress;
    @SerializedName("NextLvlExp")
    @Expose
    private Integer nextLvlExp;

    public Integer getValue() {
        return amount;
    }

    public void setValue(Integer value) {
        this.amount = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public Integer getNextLvlExp() {
        return nextLvlExp;
    }

    public void setNextLvlExp(Integer nextLvlExp) {
        this.nextLvlExp = nextLvlExp;
    }
}
