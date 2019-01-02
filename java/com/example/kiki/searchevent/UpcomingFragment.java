package com.example.kiki.searchevent;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UpcomingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_upcoming, container, false);
        RecyclerView recyclerView=rootView.findViewById(R.id.upcomingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ArrayList<UpcommingEvent> eventsList=new ArrayList<>();
        final UpcommingAdapterClass adapterClass=new UpcommingAdapterClass(getActivity(),eventsList);
        recyclerView.setAdapter(adapterClass);

        Bundle args=getArguments();
        String venue=args.getString("venue");
        LinearLayout layout=rootView.findViewById(R.id.layout_upcomming);
        String url="https://laevents-219016.appspot.com/upcoming/"+venue;
        final Spinner sorttypeSpinner=rootView.findViewById(R.id.sp_sorttype);
        final Spinner sortorderSpinner=rootView.findViewById(R.id.sp_order);
        TextView tv_upnoresults=rootView.findViewById(R.id.tv_upnoreults);
        sorttypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType=sorttypeSpinner.getSelectedItem().toString();
                int order=sortorderSpinner.getSelectedItemPosition();
                if(!selectedType.equals("Default")){
                    sortorderSpinner.setEnabled(true);
                    sortorderSpinner.setClickable(true);
                }else {
                    sortorderSpinner.setEnabled(false);
                    sortorderSpinner.setClickable(false);
                }
                if(position==0){
                    Collections.sort(eventsList,new TimeComparator(order));
                    adapterClass.notifyDataSetChanged();
                    sortorderSpinner.setEnabled(false);
                }else if(position==2){//sort by time
                    Collections.sort(eventsList,new TimeComparator(order));
                    adapterClass.notifyDataSetChanged();
                }else{
                    Collections.sort(eventsList,new StringComparator(selectedType,order));
                    adapterClass.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sortorderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType=sorttypeSpinner.getSelectedItem().toString();
                int order=sortorderSpinner.getSelectedItemPosition();
                if(!selectedType.equals("Default")){
                    sortorderSpinner.setEnabled(true);
                    sortorderSpinner.setClickable(true);
                }else{
                    sortorderSpinner.setEnabled(false);
                    sortorderSpinner.setClickable(false);
                }
                if(selectedType.equals("Time")){
                    Collections.sort(eventsList,new TimeComparator(order));
                    adapterClass.notifyDataSetChanged();
                }else{
                    Collections.sort(eventsList,new StringComparator(selectedType,order));
                    adapterClass.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d("debugmsg:sort",sortorderSpinner.getSelectedItem().toString());
        new UpcommingTask(getActivity(),layout,eventsList,adapterClass,tv_upnoresults).execute(url);
        return rootView;
    }

}
class StringComparator implements Comparator<UpcommingEvent>{
    String arg;
    int order=0;
    public StringComparator(String arg,int order){
        this.arg=arg;
        this.order=order;
    }
    @Override
    public int compare(UpcommingEvent o1, UpcommingEvent o2) {
        switch (arg){
            case "Event Name":
                if(order==0)
                    return o1.getName().compareTo(o2.getName());
                else
                    return -o1.getName().compareTo(o2.getName());
            case "Artist":
                if(order==0)
                    return o1.getArtist().compareTo(o2.getArtist());
                else return -o1.getArtist().compareTo(o2.getArtist());
            case "Type":
                if(order==0)
                    return o1.getType().compareTo(o2.getType());
                else return -o1.getType().compareTo(o2.getType());
                default:
                    return 1;
        }
    }
}
class TimeComparator implements Comparator<UpcommingEvent>{

    int order=0;
    public TimeComparator(int order){
        this.order=order;
    }
    @Override
    public int compare(UpcommingEvent o1, UpcommingEvent o2) {

        String time1=o1.getTime();
        String time2=o2.getTime();
        SimpleDateFormat simpleDateFormatOrigin=new SimpleDateFormat("MMM d, yyyy hh:mm:ss");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long timestamp1=0;
        long timestamp2=0;
        try {
            Date date1=simpleDateFormatOrigin.parse(time1);
            Date date2=simpleDateFormatOrigin.parse(time2);
            String jdbcTime1=simpleDateFormat.format(date1);
            String jdbcTime2=simpleDateFormat.format(date2);
            timestamp1=Timestamp.valueOf(jdbcTime1).getTime()/1000;
            timestamp2=Timestamp.valueOf(jdbcTime2).getTime()/1000;
        }catch (Exception e){

        }
        return order==0?Long.compare(timestamp1,timestamp2):-Long.compare(timestamp1,timestamp2);
    }
}
class UpcommingTask extends AsyncTask<String,Integer,JSONObject> {
    Context context;
    LinearLayout layout;
    ProgressDialog progressDialog;
    ArrayList<UpcommingEvent> eventsList;
    UpcommingAdapterClass adapterClass;
    TextView tv_upnoresults;
    UpcommingTask(Context context,LinearLayout layout,ArrayList<UpcommingEvent> eventsList,UpcommingAdapterClass adapterClass,TextView tv_upnoresults){
        this.context=context;
        this.layout=layout;
        this.eventsList=eventsList;
        this.adapterClass=adapterClass;
        this.tv_upnoresults=tv_upnoresults;
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
    protected JSONObject doInBackground(String... url) {
        //do in background
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        //second param must be null or you will get a client error
        JsonObjectRequest request = new JsonObjectRequest(url[0], null, future, future);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(request);
        JSONObject result=null;
        try {
            JSONObject response = future.get(10,TimeUnit.SECONDS); // this will block
            result=new JSONObject(response.toString());
            Log.d("debugmsg:print",result.toString());

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

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        progressDialog.dismiss();
//            layout.removeAllViews();
        layout.setVerticalScrollBarEnabled(true);
        try {
            if(jsonObject.getString("code").equals("succeed")){
                JSONArray jsonArray=jsonObject.getJSONArray("upcomings");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject upEvent=jsonArray.getJSONObject(i);
                    Object time=upEvent.get("time");
                    String timestr=(time.toString().equals("null")?" ":time.toString());
                    eventsList.add(new UpcommingEvent(upEvent.getString("displayName"),upEvent.getString("artist"),
                            upEvent.getString("date")+" "+timestr, upEvent.getString("type"),upEvent.getString("url")));
                    adapterClass.notifyDataSetChanged();
                }
            }else{
                tv_upnoresults.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){

        }

    }
}