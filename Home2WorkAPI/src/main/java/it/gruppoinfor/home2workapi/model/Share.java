package it.gruppoinfor.home2workapi.model;

import java.util.Date;

public class Share {
    private Long shareID;
    private User guest;
    private User host;
    private Double sharedDistance;
    private Integer karma;
    private Integer exp;
    private Date shareDate;

    public Share(Long shareID, User guest, User host, Double sharedDistance, Integer karma, Integer exp, Date shareDate) {
        this.shareID = shareID;
        this.guest = guest;
        this.host = host;
        this.sharedDistance = sharedDistance;
        this.karma = karma;
        this.exp = exp;
        this.shareDate = shareDate;
    }

    public Long getShareID() {

        return shareID;
    }

    public void setShareID(Long shareID) {
        this.shareID = shareID;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Double getSharedDistance() {
        return sharedDistance;
    }

    public void setSharedDistance(Double sharedDistance) {
        this.sharedDistance = sharedDistance;
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

    public Date getShareDate() {
        return shareDate;
    }

    public void setShareDate(Date shareDate) {
        this.shareDate = shareDate;
    }
}
