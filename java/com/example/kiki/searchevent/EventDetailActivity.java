package com.example.kiki.searchevent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class EventDetailActivity extends AppCompatActivity implements FragementCallback{
    String artistname="";
    String category="";
    FragmentAdapterClass fragmentAdapterClass;
    String eventId="";
    String eventName="";
    String venuename="";
    String genre="";
    String time="";
    String url="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetail);
        Intent intent=getIntent();
        eventName=intent.getStringExtra("eventname");
        eventId=intent.getStringExtra("eventid");
        venuename=intent.getStringExtra("venuename");
        genre=intent.getStringExtra("category");
        time=intent.getStringExtra("time");
        Toolbar tb_title=findViewById(R.id.tb_title);
        tb_title.setTitle(eventName);
        tb_title.setBackgroundColor(Color.rgb(3,169,245));
        setSupportActionBar(tb_title);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setBackgroundColor(Color.rgb(3,169,245));
        TabLayout.Tab tabEvent=tabLayout.newTab();
        tabEvent.setIcon(R.drawable.info_outline);
        tabEvent.setText("EVENT");
        tabLayout.addTab(tabEvent);
        TabLayout.Tab tabArtist=tabLayout.newTab();
        tabArtist.setIcon(R.drawable.artist);
        tabArtist.setText("ARTIST(S)");
        tabLayout.addTab(tabArtist);
        TabLayout.Tab tabVenue=tabLayout.newTab();
        tabVenue.setIcon(R.drawable.venue);
        tabVenue.setText("VENUE");
        tabLayout.addTab(tabVenue);
        TabLayout.Tab tabUpcoming=tabLayout.newTab();
        tabUpcoming.setIcon(R.drawable.upcoming);
        tabUpcoming.setText("UPCOMING");
        tabLayout.addTab(tabUpcoming);
        fragmentAdapterClass=new FragmentAdapterClass(getSupportFragmentManager(),tabLayout.getTabCount(),eventId,venuename);
        final ViewPager viewPager=findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentAdapterClass);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                Log.d("debugmsg:up","back");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        final MenuItem favitem=menu.getItem(0);
        final MenuItem twitterItem=menu.getItem(1);
        final SharedPreferences sharedPreferences=getSharedPreferences("fav_list",Context.MODE_PRIVATE);
        final LocalStorageHelper localStorageHelper=LocalStorageHelper.getInstance();
        if(localStorageHelper.isFavorite(sharedPreferences,eventId)){
            favitem.setIcon(R.drawable.heart_fill_red);
        }
        favitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    if(localStorageHelper.isFavorite(sharedPreferences,eventId)){
                        favitem.setIcon(R.drawable.heart_fill_white);
                        favitem.setIcon(R.drawable.heart_outline_black);
                        localStorageHelper.removeFromList(sharedPreferences,eventId);
                        Toast toast=Toast.makeText(getApplicationContext(),eventName+"was removed from favorites",Toast.LENGTH_LONG);
                        View view=toast.getView();
                        view.setBackgroundColor(Color.argb(180,102,102,102));
                        toast.setView(view);
                        TextView textView=toast.getView().findViewById(android.R.id.message);
                        textView.setTextColor(Color.WHITE);
                        toast.show();
                    }else{
                        favitem.setIcon(R.drawable.heart_fill_red);
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("eventId",eventId);
                        SearchResult searchResult=new SearchResult();
                        searchResult.setTime(time);
                        searchResult.setVenueName(venuename);
                        searchResult.setEventName(eventName);
                        searchResult.setGenre(genre);
                        jsonObject.put("resultobj",searchResult.toJson());
                        localStorageHelper.addToFav(sharedPreferences,jsonObject);
                        Toast toast=Toast.makeText(getApplicationContext(),eventName+"was added to favorites",Toast.LENGTH_LONG);
                        View view=toast.getView();
                        view.setBackgroundColor(Color.argb(180,102,102,102));
                        toast.setView(view);
                        TextView textView=toast.getView().findViewById(android.R.id.message);
                        textView.setTextColor(Color.WHITE);
                        toast.show();
                    }
                }catch (Exception e){

                }

                return false;
            }
        });
        twitterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Uri uri = Uri.parse("https://twitter.com/share?url="+"https:/www.ticketmaster.com/los-angeles-lakers-vs-orlando-magic-los-angeles-california-11-25-2018"+"&text=Check out "+eventName+" located at "+venuename+". Website:");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return false;
            }
        });

        return true;
    }

    @Override
    public void setCategoryAndArtist(Bundle arg) {
        artistname=arg.getString("artistname");
        category=arg.getString("category");
        fragmentAdapterClass.setArtistname(artistname);
        fragmentAdapterClass.setCategory(category);
    }


}
