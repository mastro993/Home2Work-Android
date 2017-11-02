package it.gruppoinfor.home2workapi.model;

public class Karma {

    private Integer value;
    private Integer level;
    private Integer forNextLevel;
    private Float levelProgres;

    public Karma(int value) {
        this.value = value;
        this.level = ((Double) (1 + 0.10 * Math.sqrt(value))).intValue();
        this.forNextLevel = ((Double) Math.pow((level / 0.10), 2.0)).intValue();
        this.levelProgres = (100.0f / forNextLevel) * value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getLevel() {
        return level;
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

    public Float getLevelProgres() {
        return levelProgres;
    }

    public void setLevelProgres(Float levelProgres) {
        this.levelProgres = levelProgres;
    }
}
