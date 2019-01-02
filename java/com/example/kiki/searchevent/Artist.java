package com.example.kiki.searchevent;

import org.json.JSONArray;

import java.util.ArrayList;

public class Artist {
    String name="";
    String followers="";
    String popular="";
    String checkat="";
    String term="";
    ArrayList<String> imgs;
    public Artist(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public void setCheckat(String checkat) {
        this.checkat = checkat;
    }

    public void setPopular(String popular) {
        this.popular = popular;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public String getCheckat() {
        return checkat;
    }

    public String getFollowers() {
        return followers;
    }

    public String getTerm() {
        return term;
    }

    public String getPopular() {
        return popular;
    }

}

