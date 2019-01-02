package com.example.kiki.searchevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentAdapterClass extends FragmentStatePagerAdapter {
    int tabCount;
    String eventId;
    String venuename;
    String category;
    String artistname;

    public FragmentAdapterClass(FragmentManager fragmentManager,int tabCount,String eventId,String venuename){
        super(fragmentManager);
        this.tabCount=tabCount;
        this.eventId=eventId;
        this.venuename=venuename;
    }

    public void setArtistname(String artistname) {
        this.artistname = artistname;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public Fragment getItem(int i) {
        EventFragment eventFragment=null;
        ArtistFragment artistFragment=null;
        if(i==0) {
            //when this fragment is created, the next fragment is created too
            eventFragment=new EventFragment();
            Bundle args=new Bundle();
            args.putString("eventid",eventId);
            eventFragment.setArguments(args);
            return eventFragment;
        }else if(i==1) {
            artistFragment=new ArtistFragment();
            Bundle args1=new Bundle();
            args1.putString("category",category);
            args1.putString("artistname",artistname);
            artistFragment.setArguments(args1);
            return artistFragment;
        }else if(i==2) {
            VenueFragment venueFragment=new VenueFragment();
            Bundle args2=new Bundle();
            args2.putString("venuename",venuename);
            venueFragment.setArguments(args2);
            return venueFragment;
        }else if(i==3) {
            UpcomingFragment upcomingFragment=new UpcomingFragment();
            Bundle args3=new Bundle();
            args3.putString("venue",venuename);
            upcomingFragment.setArguments(args3);
            return upcomingFragment;
        }else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
