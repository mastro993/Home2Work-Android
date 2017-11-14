package it.gruppoinfor.home2workapi.model;

public class Karma {

    private Integer value;
    private Integer level;
    private Integer forNextLevel;
    private Float levelProgress;

    public Karma(int value) {
        this.value = value;
        this.level = ((Double) (1 + 0.10 * Math.sqrt(value))).intValue();

        int thisLevelKarma = (int) Math.pow(10.0 * (level - 1.0), 2.0);
        int nextLevelKarma = (int) Math.pow(10 * level, 2.0);
        this.forNextLevel = nextLevelKarma - thisLevelKarma;

        int thisLevelKarmaDelta = value - thisLevelKarma;

        this.levelProgress = (100.0f / forNextLevel) * thisLevelKarmaDelta;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getLevel() {
        return Math.min(level, 100);
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getForNextLevel() {
        return forNextLevel;
    }

    public void setForNextLevel(Integer forNextLevel) {
        this.forNextLevel = forNextLevel;
    }

    public Float getLevelProgress() {
        return level > 100? 100f : levelProgress;
    }

    public void setLevelProgress(Float levelProgres) {
        this.levelProgress = levelProgres;
    }
}
