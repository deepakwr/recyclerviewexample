package com.example.deepak.recyclerviewexample;

/**
 * Created by deepak on 17/08/18.
 */

public class CountryInfo {

    private String name="";

    private String currency="";

    private String language="";


    public CountryInfo(String name,String currency,String language){
        this.name = name;
        this.currency = currency;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
