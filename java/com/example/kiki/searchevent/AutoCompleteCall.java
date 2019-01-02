package com.example.kiki.searchevent;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class AutoCompleteCall {
    private static AutoCompleteCall mInstance;
    private RequestQueue requestQueue;
    private static Context context;
    public AutoCompleteCall(Context ctx){
        context=ctx;
        requestQueue=getRequestQueue();
    }
    public static synchronized AutoCompleteCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AutoCompleteCall(context);
        }
        return mInstance;
    }
    private RequestQueue getRequestQueue() {
        if(requestQueue==null){
            requestQueue=Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = "https://app.ticketmaster.com/discovery/v2/suggest?apikey=wfHUN63wOkYsxR9lASv6pLfIYlb6uusM"+"&keyword="+query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        AutoCompleteCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
