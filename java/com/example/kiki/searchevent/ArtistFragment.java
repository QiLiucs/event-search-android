package com.example.kiki.searchevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ArtistFragment extends Fragment {
    LinearLayout layout;
    View rootView;
    ArrayList<Artist> artistsList=new ArrayList<>();
    ArtistAdapterClass adapterClass;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_artist, container, false);
        layout=rootView.findViewById(R.id.artist_layout);
        RecyclerView recyclerView=rootView.findViewById(R.id.artistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterClass=new ArtistAdapterClass(getActivity(),artistsList);
        recyclerView.setAdapter(adapterClass);
        return rootView;
    }
    public void executeTask(String artistname,String category){
        String url="https://laevents-219016.appspot.com/artist/" + artistname+"/"+artistname+"/"+category+"/"+"nothing";
        Log.d("debugmsg:url",url);
        new ArtistTask(getActivity(),layout,rootView,artistsList,adapterClass).execute(url);
    }

}

class ArtistTask extends AsyncTask<String,Integer,JSONObject[]> {
    Context context;
    ProgressDialog progressDialog;
    LinearLayout layout;
    View rootView;
    ArrayList<Artist> artistsList;
    ArtistAdapterClass adapterClass;
    ArtistTask(Context context,LinearLayout layout, View rootView,ArrayList<Artist> artistsList,
            ArtistAdapterClass adapterClass){
        this.context=context;
        this.layout=layout;
        this.rootView=rootView;
        this.artistsList=artistsList;
        this.adapterClass=adapterClass;
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
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        //second param must be null or you will get a client error
        JsonArrayRequest request = new JsonArrayRequest(url[0], future, future);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(request);
        JSONObject[] results=null;
        try {
            JSONArray response = future.get(10,TimeUnit.SECONDS); // this will block
            int len=response.length();
            results=new JSONObject[len];
            for(int i=0;i<len;i++){
                results[i]=new JSONObject(response.get(i).toString());
            }
            Log.d("debugmsg:print",results[0].toString());

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
        return results;
    }

    @Override
    protected void onPostExecute(JSONObject[] jsonObjects) {
        progressDialog.dismiss();
        HashMap<String,String> fieldToTitleMap=new HashMap<>();
        fieldToTitleMap.put("name","Name");
        fieldToTitleMap.put("followers","Followers");
        fieldToTitleMap.put("popular","Popularity");
        fieldToTitleMap.put("checkat","Check At");
        layout.setVerticalScrollBarEnabled(true);

        try {
            for(int i=0;i<jsonObjects.length;i++){
                JSONObject jsonObject=jsonObjects[i];
                String term=jsonObject.getString("term");
                JSONArray array=jsonObject.getJSONArray("filterartists");
                JSONArray imgs=jsonObject.getJSONArray("imgs");
                Artist artist=new Artist();
                artist.setTerm(term);

                if(array!=null && array.length()>0){
                    JSONObject artistinfo=(JSONObject)array.get(0);
                    artist.setName(artistinfo.getString("name"));
                    artist.setFollowers(artistinfo.getString("followers"));
                    artist.setPopular(artistinfo.getString("popular"));
                    artist.setCheckat(artistinfo.getString("checkat"));

                }
                ArrayList<String> imgsList=new ArrayList<>();
                for(int j=0;j<imgs.length();j++){
                    imgsList.add(imgs.getString(j));
                }
                artist.setImgs(imgsList);
                artistsList.add(artist);
            }
            adapterClass.notifyDataSetChanged();
        }catch (Exception e){
            Log.d("debugmsg:artistpost",e.toString());
        }
    }
}