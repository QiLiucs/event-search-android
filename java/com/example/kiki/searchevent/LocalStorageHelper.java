package com.example.kiki.searchevent;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class LocalStorageHelper {
    private static LocalStorageHelper localDataStorageHelper = new LocalStorageHelper();
    private LocalStorageHelper(){}
    public static LocalStorageHelper getInstance(){
        return localDataStorageHelper;
    }

    ////put
    public void addToFav(SharedPreferences sharedPreferences, JSONObject jsonObject) {
        try {
            String eventId=jsonObject.getString("eventId");
            Log.d("debugmsg:eventid",eventId);
            SharedPreferences.Editor ed=sharedPreferences.edit();
            ed.putString(eventId,jsonObject.getJSONObject("resultobj").toString());
            ed.commit();
        }catch (Exception e){

        }

    }
    //remove
    public void removeFromList(SharedPreferences sharedPreferences, String eventid) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove(eventid);
        editor.commit();
    }
    //isFavorite
    public boolean isFavorite(SharedPreferences sharedPreferences,String eventid) {
        return sharedPreferences.contains(eventid);
    }
    //get list
    public JSONArray getAll(SharedPreferences sharedPreferences){
        Map map=sharedPreferences.getAll();
        JSONArray jsonArray=new JSONArray();
        try {
            for(Object k:map.keySet()){
                String key=k.toString();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(key,map.get(k));
                jsonArray.put(jsonObject);
            }
        }catch (Exception e){

        }
        return jsonArray;

    }


}

