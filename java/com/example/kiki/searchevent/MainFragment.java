package com.example.kiki.searchevent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,LocationListener{
    RadioGroup radioGroup;
    double lat=0;
    double lng=0;
    private static final int MY_PERMISSION_FINE_LOCATION=118;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoCompleteAdapter autoCompleteAdapter;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_main, container, false);
        final AppCompatAutoCompleteTextView appCompatAutoCompleteTextView=rootView.findViewById(R.id.et_keyword);
        final TextView selectedText=rootView.findViewById(R.id.selected_item);
        autoCompleteAdapter=new AutoCompleteAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line);
        appCompatAutoCompleteTextView.setThreshold(2);
        appCompatAutoCompleteTextView.setAdapter(autoCompleteAdapter);
        appCompatAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //dont set this, or the page would show your choice
//                selectedText.setText(autoCompleteAdapter.getObject(position));
            }
        });
        appCompatAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==TRIGGER_AUTO_COMPLETE){
                    if(!TextUtils.isEmpty(appCompatAutoCompleteTextView.getText())){
                        makeApiCall(appCompatAutoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });
        radioGroup = rootView.findViewById(R.id.rg_loc);
        radioGroup.setOnCheckedChangeListener(this);
        Button searchBtn=rootView.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debugmsg:search","search");
                searchEvent();
            }
        });
        Button clearBtn=rootView.findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mcheckPermission();
        return rootView;
    }

    private void makeApiCall(String text) {
        AutoCompleteCall.make(getActivity(), text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> stringList=new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject obj=new JSONObject(responseObject.getJSONObject("_embedded").toString());
                    JSONArray array = obj.getJSONArray("attractions");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoCompleteAdapter.setData(stringList);
                autoCompleteAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.radio_curloc){
            EditText etLocation=rootView.findViewById(R.id.et_loc);
            etLocation.setText("");
            etLocation.setHint("Type in the Location");
            etLocation.setEnabled(false);
            TextView tvLocError=rootView.findViewById(R.id.tv_locerror);
            tvLocError.setText("");
        }
        if(checkedId==R.id.radio_otherloc){
            EditText etLocation=rootView.findViewById(R.id.et_loc);
            etLocation.setEnabled(true);
        }
    }

    public void searchEvent(){
        Log.d("debugmsg:searchEvent","searchEvent");
        EditText etKeyword=rootView.findViewById(R.id.et_keyword);
        Spinner spCategory=rootView.findViewById(R.id.sp_category);
        EditText etDistance=rootView.findViewById(R.id.et_distance);
        Spinner spUnits=rootView.findViewById(R.id.sp_units);
        RadioButton rbCurloc=rootView.findViewById(R.id.radio_curloc);
        RadioButton rbOther=rootView.findViewById(R.id.radio_otherloc);
        EditText rtLocation=rootView.findViewById(R.id.et_loc);
        String keyword=etKeyword.getText().toString().trim();
        String location=rtLocation.getText().toString().trim();
        TextView tvKerror=rootView.findViewById(R.id.tv_kerror);
        TextView tvLocError=rootView.findViewById(R.id.tv_locerror);
        //show error toast
        if(keyword.length()==0 || (rbOther.isChecked() && location.trim().length()==0)){
            if(keyword.length()==0 )
                tvKerror.setText("Please enter mandatory field");
            Log.d("debugmsg:searchEvent",rbOther.isChecked()+","+location.trim().length());
            if(rbOther.isChecked() && location.trim().length()==0)
                tvLocError.setText("Please enter mandatory field");
            Toast toast=Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_LONG);
            View view=toast.getView();
            view.setBackgroundColor(Color.argb(180,102,102,102));
            toast.setView(view);
            TextView textView=toast.getView().findViewById(android.R.id.message);
            textView.setTextColor(Color.WHITE);
            toast.show();
            return;
        }
        //start a new activity
        Intent searchIntent=new Intent(getActivity(),SearchResultActivity.class);
        searchIntent.putExtra("keyword",etKeyword.getText().toString());
        Log.d("debugmsg:keyword",etKeyword.getText().toString());
        searchIntent.putExtra("apikey","wfHUN63wOkYsxR9lASv6pLfIYlb6uusM");
        searchIntent.putExtra("category",spCategory.getSelectedItemPosition());
        if(etDistance.getText().toString().length()==0){
            searchIntent.putExtra("dist",etDistance.getHint().toString());
        }else{
            searchIntent.putExtra("dist",etDistance.getText().toString());
        }
        searchIntent.putExtra("units",spUnits.getSelectedItemPosition());
        searchIntent.putExtra("lat",lat);
        searchIntent.putExtra("lng",lng);
        searchIntent.putExtra("location",location);
        Log.d("debugmsg:originloc",location);//empty
        startActivity(searchIntent);
    }
    public void clear(){
        EditText etKeyword=rootView.findViewById(R.id.et_keyword);
        etKeyword.setText("");
        Spinner spCategory=rootView.findViewById(R.id.sp_category);
        spCategory.setSelection(0);
        EditText etDistance=rootView.findViewById(R.id.et_distance);
        etDistance.setText("");
        Spinner spUnits=rootView.findViewById(R.id.sp_units);
        spUnits.setSelection(0);
        radioGroup.check(R.id.radio_curloc);
        EditText rtLocation=rootView.findViewById(R.id.et_loc);
        rtLocation.setText("");
        rtLocation.setHint("Type in the Location");
        rtLocation.setEnabled(false);
        TextView tvKerror=rootView.findViewById(R.id.tv_kerror);
        tvKerror.setText("");
        TextView tvLocError=rootView.findViewById(R.id.tv_locerror);
        tvLocError.setText("");

    }

    private void mcheckPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //permission is denied
            Log.d("debugmsg","request permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_FINE_LOCATION);
        }else{
            LocationManager locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("debugmsg:location",location.getLatitude()+"");
        lat=location.getLatitude();
        lng=location.getLongitude();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("onProviderEnabled","onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("onStatusChanged","onStatusChanged");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("onProviderDisabled","onProviderDisabled");
    }


}
