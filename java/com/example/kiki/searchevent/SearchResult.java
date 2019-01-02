package com.example.kiki.searchevent;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult {
    private String genre;
    private String eventName;
    private String venueName;
    private String time;

    public String getTime() {
        return time;
    }

    public String getEventName() {
        return eventName;
    }

    public String getGenre() {
        return genre;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventName", this.eventName);
            jsonObject.put("genre", this.genre);
            jsonObject.put("venueName", this.venueName);
            jsonObject.put("time", this.time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toJsonString() {
        return this.toJson().toString();
    }
}
