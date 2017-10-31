package it.gruppoinfor.home2workapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Job {


    @SerializedName("company")
    @Expose
    private Company company;
    @SerializedName("start")
    @Expose
    private long start;
    @SerializedName("end")
    @Expose
    private long end;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }


}
