package it.gruppoinfor.home2work.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import it.gruppoinfor.home2work.api.Account;

public class SearchResults {

    @SerializedName("accounts")
    @Expose
    private List<Account> accounts = new ArrayList<>();
    @SerializedName("companies")
    @Expose
    private List<Company> companies = new ArrayList<>();

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

}
