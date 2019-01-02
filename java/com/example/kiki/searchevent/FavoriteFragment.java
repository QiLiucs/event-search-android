package com.example.kiki.searchevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class FavoriteFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_favorite, container, false);
        final SharedPreferences sharedPreferences=getActivity().getSharedPreferences("fav_list",Context.MODE_PRIVATE);
        LocalStorageHelper localStorageHelper=LocalStorageHelper.getInstance();
        JSONArray jsonArray=localStorageHelper.getAll(sharedPreferences);
        //adpator
        if(jsonArray.length()>0){
            Log.d("debugmsg:array","len>0");
            TextView tv_norecord=rootView.findViewById(R.id.tv_norecord);
            tv_norecord.setVisibility(View.GONE);
            ListView lv_fav=rootView.findViewById(R.id.lv_favorite);
            lv_fav.setAdapter(new FavoriteAdaptor(getActivity(),jsonArray,tv_norecord));

        }
        return rootView;

    }
}
class FavoriteAdaptor extends BaseAdapter {
    JSONArray jsonArray;
    Context context;
    TextView tv_norecord;
    FavoriteAdaptor(Context context,JSONArray jsonArray,TextView tv_norecord){
        this.jsonArray=jsonArray;
        this.context=context;
        this.tv_norecord=tv_norecord;
    }
    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView;
        String eventId="";
        String eventName="";
        String category="";
        String venueName="";
        String time="";
        if(convertView==null){
            itemView=View.inflate(context, R.layout.item,null);
        }else{
            itemView=convertView;
        }
        try {
            JSONObject jsonObject=jsonArray.getJSONObject(position);
            Iterator<String> iterator=jsonObject.keys();
            eventId=iterator.next();
            String obj=jsonObject.getString(eventId);
            JSONObject searchResItem=new JSONObject(obj);
            eventName = searchResItem.getString("eventName");
            category=searchResItem.getString("genre");
            venueName=searchResItem.getString("venueName");
            time=searchResItem.getString("time");

            TextView tv_event = itemView.findViewById(R.id.tv_event);
            tv_event.setText(eventName);
            ImageView iv = itemView.findViewById(R.id.iv_category);
            switch (category) {
                case "Music":
                    Glide.with(context).load(R.drawable.music_icon).error(R.drawable.ic_launcher_background).override(100, 100).into(iv);
                    break;
                case "Sports":
                    Glide.with(context).load(R.drawable.sport_icon).error(R.drawable.ic_launcher_background).override(100, 100).into(iv);
                    break;
                case "Arts & Theatre":
                    Glide.with(context).load(R.drawable.art_icon).error(R.drawable.ic_launcher_background).override(100, 100).into(iv);
                    break;
                case "Film":
                    Glide.with(context).load(R.drawable.film_icon).error(R.drawable.ic_launcher_background).override(100, 100).into(iv);
                    break;
                case "Miscellaneous":
                    Glide.with(context).load(R.drawable.miscellaneous_icon).error(R.drawable.ic_launcher_background).override(100, 100).into(iv);
                    break;
            }
            TextView tv_venue=itemView.findViewById(R.id.tv_venue);
            tv_venue.setText(venueName);
            tv_venue.setTextColor(Color.rgb(153,153,153));
            TextView tv_time=itemView.findViewById(R.id.tv_time);
            tv_time.setTextColor(Color.rgb(153,153,153));
            tv_time.setText(time);
            ImageView iv_fav=itemView.findViewById(R.id.iv_favorite);
            iv_fav.setImageResource(R.drawable.heart_fill_red);
            iv_fav.setTag(R.drawable.heart_fill_red);
            iv_fav.setClickable(true);
            iv_fav.bringToFront();
            final String finalEventName1 = eventName;
            final String finalEventId1 = eventId;
            iv_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonArray.remove(position);
                    notifyDataSetChanged();
                    if(jsonArray.length()==0){
                        tv_norecord.setVisibility(View.VISIBLE);
                    }
                    final SharedPreferences sharedPreferences=context.getSharedPreferences("fav_list",Context.MODE_PRIVATE);
                    LocalStorageHelper localStorageHelper=LocalStorageHelper.getInstance();
                    localStorageHelper.removeFromList(sharedPreferences, finalEventId1);
                    Toast toast=Toast.makeText(context, finalEventName1 +"was removed from favorites",Toast.LENGTH_LONG);
                    View view=toast.getView();
                    view.setBackgroundColor(Color.argb(180,102,102,102));
                    toast.setView(view);
                    TextView textView=toast.getView().findViewById(android.R.id.message);
                    textView.setTextColor(Color.WHITE);
                    toast.show();
                }
            });
        }catch (Exception e){

        }
        final String finalEventId = eventId;
        final String finalEventName = eventName;
        final String finalVenueName = venueName;
        final String finalCategory = category;
        final String finalTime = time;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent detailIntent=new Intent(context,EventDetailActivity.class);
                detailIntent.putExtra("eventid", finalEventId);
                detailIntent.putExtra("eventname", finalEventName);
                detailIntent.putExtra("venuename", finalVenueName);
                detailIntent.putExtra("genre", finalCategory);
                detailIntent.putExtra("time", finalTime);
                context.startActivity(detailIntent);
            }
        });
        return itemView;
    }
}