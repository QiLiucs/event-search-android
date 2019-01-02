package com.example.kiki.searchevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class VenueFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    String lat="";
    String lng="";
    String venuename;
    SupportMapFragment mapFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("debugmsg:VenueFragment","onCreateView");
        View rootView= inflater.inflate(R.layout.fragment_venue, container, false);
        Bundle args=getArguments();
        venuename=args.getString("venuename");
        String url="https://laevents-219016.appspot.com/venue/" +venuename;
        TextView tv0_venuename=rootView.findViewById(R.id.col0_venuename);
        TextView tv0_addr=rootView.findViewById(R.id.col0_addr);
        TextView tv0_city=rootView.findViewById(R.id.col0_city);
        TextView tv0_phone=rootView.findViewById(R.id.col0_phone);
        TextView tv0_openhour=rootView.findViewById(R.id.col0_openhours);
        TextView tv0_grule=rootView.findViewById(R.id.col0_grule);
        TextView tv0_crule=rootView.findViewById(R.id.col0_crule);
        TextView tv_venuename=rootView.findViewById(R.id.col_venuename);
        TextView tv_addr=rootView.findViewById(R.id.col_addr);
        TextView tv_city=rootView.findViewById(R.id.col_city);
        TextView tv_phone=rootView.findViewById(R.id.col_phone);
        TextView tv_openhour=rootView.findViewById(R.id.col_openhours);
        TextView tv_grule=rootView.findViewById(R.id.col_grule);
        TextView tv_crule=rootView.findViewById(R.id.col_crule);
        final EditText tv_lat=rootView.findViewById(R.id.tv_lat);
        final EditText tv_lng=rootView.findViewById(R.id.tv_lng);
        tv_lng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lat=tv_lat.getText().toString();
                lng=tv_lng.getText().toString();
                initMap();

            }
        });
        RelativeLayout layout=rootView.findViewById(R.id.venue_layout);
        new EventDetailTask(getActivity(),layout,tv_venuename,tv_addr,tv_city,tv_phone,tv_openhour,tv_grule,tv_crule,tv_lat,tv_lng,
                tv0_venuename,tv0_addr,tv0_city,tv0_phone,tv0_openhour,tv0_grule,tv0_crule).execute(url);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
    }

    public void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("debugmsg:lat",lat);
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in "+venuename));
        mMap.setMinZoomPreference(16);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
class EventDetailTask extends AsyncTask<String,Integer,JSONObject> {
    Context context;
    RelativeLayout layout;
    TextView tv_venuename;
    TextView tv_addr;
    TextView tv_city;
    TextView tv_phone;
    TextView tv_openhour;
    TextView tv_grule;
    TextView tv_crule;
    ProgressDialog progressDialog;
    EditText tv_lat;
    EditText tv_lng;
    TextView tv0_venuename,tv0_addr,tv0_city,tv0_phone,tv0_openhour,tv0_grule,tv0_crule;
    EventDetailTask(Context context,
                    RelativeLayout layout,
                    TextView tv_venuename,
                    TextView tv_addr,
                    TextView tv_city,
                    TextView tv_phone,
                    TextView tv_openhour,
                    TextView tv_grule,
                    TextView tv_crule,
                    EditText tv_lat,
                    EditText tv_lng,
                    TextView tv0_venuename,
                    TextView tv0_addr,
                    TextView tv0_city,
                    TextView tv0_phone,
                    TextView tv0_openhour,
                    TextView tv0_grule,
                    TextView tv0_crule){
        this.context=context;
        this.layout=layout;
        this.tv_addr=tv_addr;
        this.tv_city=tv_city;
        this.tv_crule=tv_crule;
        this.tv_grule=tv_grule;
        this.tv_openhour=tv_openhour;
        this.tv_phone=tv_phone;
        this.tv_venuename=tv_venuename;
        this.tv_lat=tv_lat;
        this.tv_lng=tv_lng;
        this.tv0_venuename=tv0_venuename;
        this.tv0_addr=tv0_addr;
        this.tv0_city=tv0_city;
        this.tv0_phone=tv0_phone;
        this.tv0_openhour=tv0_openhour;
        this.tv0_grule=tv0_grule;
        this.tv0_crule=tv0_crule;
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
        try {
            String venueName="";
            if(jsonObject.has("venuename")){
                venueName=jsonObject.getString("venuename");
                tv_venuename.setText(venueName);
            }else{
                tv0_venuename.setVisibility(View.GONE);
                tv_venuename.setVisibility(View.GONE);
            }
            String lat="";
            if(jsonObject.has("lat")){
                lat=jsonObject.getString("lat");
                tv_lat.setText(lat);

            }else{

            }
            String lng="";
            if(jsonObject.has("lng")){
                lng=jsonObject.getString("lng");
                tv_lng.setText(lng);
            }
            String address="";
            if(jsonObject.has("address")){
                address=jsonObject.getString("address");
                tv_addr.setText(address);

            }else {
                tv0_addr.setVisibility(View.GONE);
                tv_addr.setVisibility(View.GONE);
            }
            String city="";
            if(jsonObject.has("city")){
                city=jsonObject.getString("city");
                tv_city.setText(city);
            }else {
                tv0_city.setVisibility(View.GONE);
                tv_city.setVisibility(View.GONE);
            }
            String phone="";
            if(jsonObject.has("phone")){
                phone=jsonObject.getString("phone");
                tv_phone.setText(phone);

            }else {
                tv0_phone.setVisibility(View.GONE);
                tv_phone.setVisibility(View.GONE);
            }
            String openh="";
            if(jsonObject.has("openh")){
                openh=jsonObject.getString("openh");
                tv_openhour.setText(openh);
            }else {
                tv0_openhour.setVisibility(View.GONE);
                tv_openhour.setVisibility(View.GONE);
            }
            String grule="";
            if(jsonObject.has("grule")){
                grule=jsonObject.getString("grule");
                tv_grule.setText(grule);

            }else {
                tv0_grule.setVisibility(View.GONE);
                tv_grule.setVisibility(View.GONE);
            }
            String crule="";
            if(jsonObject.has("crule")){
                crule=jsonObject.getString("crule");
                tv_crule.setText(crule);
            }else {
                tv0_crule.setVisibility(View.GONE);
                tv_crule.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.d("debugmsg:posterror",e.toString());//No value for phone
        }


    }
}