package com.example.kiki.searchevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EventFragment extends Fragment {
    FragementCallback fragementCallback=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("debugmsg:EventFragment","onCreateView");
        View rootView= inflater.inflate(R.layout.fragment_event, container, false);
        Bundle args=getArguments();
        String eventId=args.getString("eventid");
        TextView colArtist=rootView.findViewById(R.id.col_artist);
        TextView colVenue=rootView.findViewById(R.id.col_venue);
        TextView colTime=rootView.findViewById(R.id.col_time);
        TextView colCategory=rootView.findViewById(R.id.col_category);
        TextView colPricerange=rootView.findViewById(R.id.col_pricerange);
        TextView colTicketStatus=rootView.findViewById(R.id.col_ticketStatus);
        TextView colBuy=rootView.findViewById(R.id.col_buy);
        TextView colSeatmap=rootView.findViewById(R.id.col_seatmap);
        TextView col0Artist=rootView.findViewById(R.id.col0_artist);
        TextView col0Venue=rootView.findViewById(R.id.col0_venue);
        TextView col0Time=rootView.findViewById(R.id.col0_time);
        TextView col0Category=rootView.findViewById(R.id.col0_category);
        TextView col0Pricerange=rootView.findViewById(R.id.col0_pricerange);
        TextView col0TicketStatus=rootView.findViewById(R.id.col0_ticketStatus);
        TextView col0Buy=rootView.findViewById(R.id.col0_buy);
        TextView col0Seatmap=rootView.findViewById(R.id.col0_seatmap);
        RelativeLayout layout=rootView.findViewById(R.id.layout);
        String url="https://laevents-219016.appspot.com/search/"+eventId;
        new EventDetailTask(getActivity(),layout,eventId,colArtist,colVenue,colTime,colCategory,colPricerange
        ,colTicketStatus,colBuy,colSeatmap,col0Artist,col0Venue,col0Time,col0Category,col0Pricerange,col0TicketStatus,col0Buy,col0Seatmap).execute(url);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragementCallback=(EventDetailActivity)getActivity();
    }

    class EventDetailTask extends AsyncTask<String,Integer,JSONObject> {
        Context context;
        String eventid;
        RelativeLayout layout;
        ProgressDialog progressDialog;
        TextView colArtist;
        TextView colVenue;
        TextView colTime;
        TextView colCategory;
        TextView colPricerange;
        TextView colTicketStatus;
        TextView colBuy;
        TextView colSeatmap;
        TextView col0Artist;
        TextView col0Venue;
        TextView col0Time;
        TextView col0Category;
        TextView col0Pricerange;
        TextView col0TicketStatus;
        TextView col0Buy;
        TextView col0Seatmap;
        EventDetailTask(Context context,
                        RelativeLayout layout,
                        String eventid,
                        TextView colArtist,
                        TextView colVenue,
                        TextView colTime,
                        TextView colCategory,
                        TextView colPricerange,
                        TextView colTicketStatus,
                        TextView colBuy,
                        TextView colSeatmap,
                        TextView col0Artist,
                        TextView col0Venue,
                        TextView col0Time,
                        TextView col0Category,
                        TextView col0Pricerange,
                        TextView col0TicketStatus,
                        TextView col0Buy,
                        TextView col0Seatmap){
            this.context=context;
            this.layout=layout;
            this.eventid=eventid;
            this.colArtist=colArtist;
            this.colVenue=colVenue;
            this.colTime=colTime;
            this.colCategory=colCategory;
            this.colPricerange=colPricerange;
            this.colTicketStatus=colTicketStatus;
            this.colBuy=colBuy;
            this.colSeatmap=colSeatmap;
            this.col0Artist=col0Artist;
            this.col0Venue=col0Venue;
            this.col0Time=col0Time;
            this.col0Category=col0Category;
            this.col0Pricerange=col0Pricerange;
            this.col0TicketStatus=col0TicketStatus;
            this.col0Buy=col0Buy;
            this.col0Seatmap=col0Seatmap;
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
            try {
                progressDialog.dismiss();
                String artistName=jsonObject.getString("artist_name");
                String venue=jsonObject.getString("venue");
                String category=jsonObject.getString("genre");
                String status=jsonObject.getString("status");
                String priceRange=jsonObject.getString("price_range");
                String time=jsonObject.getString("data_time");
                String buyTicket=jsonObject.getString("buy_ticket");
                String seatMap=jsonObject.getString("seat_map");

                if(!artistName.equals("null") && !artistName.equals("Undefined")){
                    colArtist.setText(artistName);
                }else{
                    col0Artist.setVisibility(View.GONE);
                    colArtist.setVisibility(View.GONE);
                }
                if(!venue.equals("null") && !venue.equals("Undefined")){
                    colVenue.setText(venue);
                }else{
                    col0Venue.setVisibility(View.GONE);
                    colVenue.setVisibility(View.GONE);
                }
                if(!category.equals("null") && !category.equals("Undefined")){//pop|music|undefined|undefined|rapper
                    //preprocess data
                    category=category.replace("Undefined | ","");
                    category=category.replace("| Undefined","");//category is on the end of sentence
                    if(category.charAt(category.length()-1)=='|'){
                        category=category.substring(0,category.length()-1);
                    }
                    Log.d("debugmsg:colCategory",category);
                    colCategory.setText(category);
                }else{
                    col0Category.setVisibility(View.GONE);
                    colCategory.setVisibility(View.GONE);
                }
                if(!status.equals("null") && !status.equals("Undefined")){
                    colTicketStatus.setText(status);
                }else{
                    col0TicketStatus.setVisibility(View.GONE);
                    colTicketStatus.setVisibility(View.GONE);
                }

                if(!priceRange.equals("null") && !priceRange.equals("Undefined")){
                    colPricerange.setText(priceRange);
                }else{
                    col0Pricerange.setVisibility(View.GONE);
                    colPricerange.setVisibility(View.GONE);
                }
                if(!time.equals("null")&& !time.equals("Undefined")){
                    colTime.setText(time);
                }else{
                    col0Time.setVisibility(View.GONE);
                    colTime.setVisibility(View.GONE);
                }
                if(!buyTicket.equals("null")&& !buyTicket.equals("Undefined")){
                    String html = "<a href='"+buyTicket+"'>Ticket master</a>";
                    colBuy.setMovementMethod(LinkMovementMethod.getInstance());
                    colBuy.setText(Html.fromHtml(html));
                }else{
                    colBuy.setVisibility(View.GONE);
                    col0Buy.setVisibility(View.GONE);
                }
                if(!seatMap.equals("null")&&!seatMap.equals("Undefined")){
                    String html1 = "<a href='"+seatMap+"'>View Here</a>";
                    colSeatmap.setMovementMethod(LinkMovementMethod.getInstance());
                    colSeatmap.setText(Html.fromHtml(html1));
                }else{
                    colSeatmap.setVisibility(View.GONE);
                    col0Seatmap.setVisibility(View.GONE);
                }
                List<Fragment> fragments=getActivity().getSupportFragmentManager().getFragments();
                for(int i=0;i<fragments.size();i++){
                    if(fragments.get(i).getClass().toString().equals("class com.example.kiki.searchevent.ArtistFragment")){
                        ArtistFragment artistFragment=(ArtistFragment) fragments.get(i);
                        artistFragment.executeTask(artistName,category);
                        break;
                    }
                }
            }catch (Exception e){

            }

        }
    }
}
