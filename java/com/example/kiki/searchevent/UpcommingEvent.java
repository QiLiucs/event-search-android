package com.example.kiki.searchevent;

public class UpcommingEvent {
    private String name;
    private String artist;
    private String time;
    private String type;
    private String url;
    public UpcommingEvent(String name,String artist,String time,String type,String url){
        this.name=name;
        this.artist=artist;
        this.time=time;
        this.type=type;
        this.url=url;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
