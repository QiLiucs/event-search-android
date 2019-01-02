package com.example.kiki.searchevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class SearchResultActivity extends AppCompatActivity {
    final String FAV_LIST_FILE="fav_list";
    String location="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.rl_layout);
        TextView tv_noresult=findViewById(R.id.tv_searchnoresults);
        ListView lv=findViewById(R.id.lv_event);
        setTitle("Search Results");
        Intent intent=getIntent();
        String keyword=intent.getStringExtra("keyword");
        String apikey=intent.getStringExtra("apikey");
        String dist=intent.getStringExtra("dist");
        location=intent.getStringExtra("location");
        double lat=intent.getDoubleExtra("lat",0);
        double lng=intent.getDoubleExtra("lng",0);
        int categoryNumber=intent.getIntExtra("category",-1);
        String category="";
        switch (categoryNumber){
            case 0:
                category="Default";
                break;
            case 1:
                category="Music";
                break;
            case 2:
                category="Sports";
                break;
            case 3:
                category="Arts & Theatre";
                break;
            case 4:
                category="Film";
                break;
            case 5:
                category="Miscellaneous";
                break;
        }
        String units="";
        int unitsNumber=intent.getIntExtra("units",-1);
        switch (unitsNumber){
            case 0:
                units="miles";
                break;
            case 1:
                units="km";
                break;
        }

        Log.d("debugmsg:keyword",keyword+"");
        Log.d("debugmsg:lat",lat+"");
        Log.d("debugmsg:lng",lng+"");
        Log.d("debugmsg:category",category+"");
        Log.d("debugmsg:dist",dist+"");
        Log.d("debugmsg:units",units+"");
        Log.d("debugmsg:afterlocation",location);//""
        //make a request
        String url = "https://laevents-219016.appspot.com/search?keyword="+keyword+"&lat="+lat
                +"&lng="+lng+"&apikey="+apikey+"&category="+category
                +"&dist="+dist+"&units="+units;
        if(location.length()>0){
            String getLatLngUrl="https://maps.googleapis.com/maps/api/geocode/json?address=" + location +
                    "&key=AIzaSyCt7RAy9HK6ENne2PAeRu7jDTWv0lw7HjA";
            new SearchTask(this,layout,tv_noresult,lv,FAV_LIST_FILE).execute(getLatLngUrl,url);
        }else
            new SearchTask(this,layout,tv_noresult,lv,FAV_LIST_FILE).execute(url);
    }

}
class SearchTask extends AsyncTask<String,Integer,JSONObject[]>{
    Context context;
    ProgressDialog progressDialog;
    RelativeLayout layout;
    ListView lv;
    String filename;
    TextView tv_noresult;
    SearchTask(Context context,RelativeLayout layout,TextView tv_noresult,ListView lv,String filename){
        this.context=context;
        this.layout=layout;
        this.tv_noresult=tv_noresult;
        this.lv=lv;
        this.filename=filename;
        progressDialog=new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        layout.setClickable(false);
        layout.setFocusable(false);
        layout.setFocusableInTouchMode(false);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("search events...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected JSONObject[] doInBackground(String... url) {
        String newUrl=url[0];
        try {
            if(url[0].indexOf("https://maps")==0){
                JSONObject latLngInfo=getWebData(url[0]);
                JSONObject firstRes=latLngInfo.getJSONArray("results").getJSONObject(0);
                JSONObject geometry=firstRes.getJSONObject("geometry");
                JSONObject locationJson=geometry.getJSONObject("location");
                String latStr=locationJson.getString("lat");
                String lngStr=locationJson.getString("lng");
                String searchUrl=url[1];
                int latIdx=searchUrl.indexOf("&lat")+5;
                int lngIdx=searchUrl.indexOf("&lng");
                int lngEndIdx=searchUrl.indexOf("&apikey");
                String oldLat=searchUrl.substring(latIdx,lngIdx);
                String oldLng=searchUrl.substring(lngIdx+5,lngEndIdx);
                searchUrl=searchUrl.replaceFirst(oldLat,latStr);
                searchUrl=searchUrl.replaceFirst(oldLng,lngStr);
                newUrl=searchUrl;
                Log.d("debugmsg:newurl",newUrl);
            }
        }catch (Exception e){

        }

        //do in background
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        //second param must be null or you will get a client error
        JsonObjectRequest request = new JsonObjectRequest(newUrl, null, future, future);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(request);
        JSONObject[] results=null;
        try {
            JSONObject response = future.get(10,TimeUnit.SECONDS); // this will block
            JSONArray eventResultArr=response.getJSONArray("eventsjson");//get an error for no results
            int len=eventResultArr.length();
            results=new JSONObject[len];
            for(int i=0;i<len;i++){
                results[i]=new JSONObject(eventResultArr.get(i).toString());

            }
            Log.d("debugmsg:print",results[0].toString());

        } catch (InterruptedException e) {
            Log.d("debugmsg:err1",e.getMessage());
            // exception handling
        } catch (ExecutionException e) {
            Log.d("debugmsg:err2",e.getMessage());
            // exception handling
        }catch (JSONException e){
            Log.d("debugmsg:err3",e.getMessage());

        }catch(Exception e){
            //len=0
            //no result
            Log.d("debugmsg:err4",e.getMessage());
        }
        return results;
    }

    @Override
    protected void onPostExecute(JSONObject[] results) {
        progressDialog.dismiss();
        if(results!=null && results.length>0){
            tv_noresult.setVisibility(View.GONE);
        }
        lv.setAdapter(new EventAdaptor(context,results,filename));
    }
    public JSONObject getWebData(String url){
        //do in background
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        //second param must be null or you will get a client error
        JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(request);
        JSONObject result=null;
        try {
            JSONObject response = future.get(20,TimeUnit.SECONDS); // this will block
            result=new JSONObject(response.toString());
            Log.d("debugmsg:getweb",result.toString());
        } catch (InterruptedException e) {
            Log.d("debugmsg:err",e.getMessage());
            // exception handling
        } catch (ExecutionException e) {
            Log.d("debugmsg:err",e.getMessage());
            // exception handling
        }catch (JSONException e){
            Log.d("debugmsg:err",e.getMessage());

        }catch(Exception e){
            Log.d("debugmsg:err",e.getMessage());
        }
        return result;
    }
}

class EventAdaptor extends BaseAdapter{
    Context context;
    JSONObject[] eventResultArr;
    String filename;
    EventAdaptor(Context context,JSONObject[] eventResultArr,String filename){
        this.context=context;
        this.eventResultArr=eventResultArr;
        this.filename=filename;
    }
    @Override
    public int getCount() {
        return eventResultArr.length;
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
    public View getView(int position, final View convertView, ViewGroup parent) {
        View itemView;
        if(convertView==null){
            itemView=View.inflate(context, R.layout.item,null);
        }else{
            itemView=convertView;
        }
        try {
            final String eventName=eventResultArr[position].getString("eventname");
            TextView tv_event=itemView.findViewById(R.id.tv_event);
            tv_event.setText(eventName);
            final String category=eventResultArr[position].getString("genre");
            ImageView iv=itemView.findViewById(R.id.iv_category);
            switch (category){
                case "Music":
                    Glide.with(context).load(R.drawable.music_icon).error(R.drawable.ic_launcher_background).override(100,100).into(iv);
                    break;
                case "Sports":
                    Glide.with(context).load(R.drawable.sport_icon).error(R.drawable.ic_launcher_background).override(100,100).into(iv);
                    break;
                case "Arts & Theatre":
                    Glide.with(context).load(R.drawable.art_icon).error(R.drawable.ic_launcher_background).override(100,100).into(iv);
                    break;
                case "Film":
                    Glide.with(context).load(R.drawable.film_icon).error(R.drawable.ic_launcher_background).override(100,100).into(iv);
                    break;
                case "Miscellaneous":
                    Glide.with(context).load(R.drawable.miscellaneous_icon).error(R.drawable.ic_launcher_background).override(100,100).into(iv);
                    break;
            }
            final String venuename=eventResultArr[position].getString("venuename");
            TextView tv_venue=itemView.findViewById(R.id.tv_venue);
            tv_venue.setText(venuename);
            tv_venue.setTextColor(Color.rgb(153,153,153));
            final String localDate=eventResultArr[position].getString("localDate");
            final String localTime=eventResultArr[position].getString("localTime");
            TextView tv_time=itemView.findViewById(R.id.tv_time);
            tv_time.setTextColor(Color.rgb(153,153,153));
            tv_time.setText(localDate+" "+localTime);
            final ImageView iv_fav=itemView.findViewById(R.id.iv_favorite);
            final SharedPreferences sharedPreferences=context.getSharedPreferences(filename,Context.MODE_PRIVATE);
            final String eventid=eventResultArr[position].getString("eventid");
            LocalStorageHelper localStorageHelper=LocalStorageHelper.getInstance();
            if(localStorageHelper.isFavorite(sharedPreferences,eventid)){
                iv_fav.setImageResource(R.drawable.heart_fill_red);
                iv_fav.setTag(R.drawable.heart_fill_red);
            }else{
                iv_fav.setImageResource(R.drawable.heart_fill_white);
                iv_fav.setImageResource(R.drawable.heart_outline_black);
                iv_fav.setTag(R.drawable.heart_outline_black);
            }
            iv_fav.setClickable(true);
            iv_fav.bringToFront();

            iv_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debugmsg:fav","fav");
                    iv_fav.setImageResource(R.drawable.heart_fill_red);
                    LocalStorageHelper localStorageHelper=LocalStorageHelper.getInstance();
                    if(iv_fav.getTag().equals(R.drawable.heart_outline_black)){//is not faved
                        iv_fav.setImageResource(R.drawable.heart_fill_red);
                        iv_fav.setTag(R.drawable.heart_fill_red);

                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("eventId",eventid);
                            SearchResult searchResult=new SearchResult();
                            searchResult.setGenre(category);
                            searchResult.setEventName(eventName);
                            searchResult.setVenueName(venuename);
                            searchResult.setTime(localDate+" "+localTime);
                            jsonObject.put("resultobj",searchResult.toJson());
                            Log.d("debugmsg:add",searchResult.toJson().toString());
                            localStorageHelper.addToFav(sharedPreferences,jsonObject);
                            Toast toast=Toast.makeText(context,eventName+"was added to favorites",Toast.LENGTH_LONG);
                            View view=toast.getView();
                            view.setBackgroundColor(Color.argb(180,102,102,102));
                            toast.setView(view);
                            TextView textView=toast.getView().findViewById(android.R.id.message);
                            textView.setTextColor(Color.WHITE);
                            toast.show();
                        }catch (Exception e){
                            Log.d("debugmsg:err",e.getMessage());

                        }

                    }else{//is in fav list
                        iv_fav.setImageResource(R.drawable.heart_fill_white);
                        iv_fav.setImageResource(R.drawable.heart_outline_black);
                        iv_fav.setTag(R.drawable.heart_outline_black);
                        localStorageHelper.removeFromList(sharedPreferences,eventid);
                        Toast toast=Toast.makeText(context,eventName+"was removed from favorites",Toast.LENGTH_LONG);
                        View view=toast.getView();
                        view.setBackgroundColor(Color.argb(180,102,102,102));
                        toast.setView(view);
                        TextView textView=toast.getView().findViewById(android.R.id.message);
                        textView.setTextColor(Color.WHITE);
                        toast.show();
                    }

                }
            });
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent detailIntent=new Intent(context,EventDetailActivity.class);
                    detailIntent.putExtra("eventid",eventid);
                    detailIntent.putExtra("eventname",eventName);
                    detailIntent.putExtra("venuename",venuename);
                    detailIntent.putExtra("genre",category);
                    detailIntent.putExtra("time",localDate+" "+localTime);
                    context.startActivity(detailIntent);
                }
            });

        }catch (Exception e){

        }

        return itemView;
    }
}
