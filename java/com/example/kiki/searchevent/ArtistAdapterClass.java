package com.example.kiki.searchevent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ArtistAdapterClass extends RecyclerView.Adapter<ArtistHolder> {
    private Context context;
    private ArrayList<Artist> artists;
    ArtistAdapterClass(Context context,ArrayList<Artist> artists){
        this.context=context;
        this.artists=artists;
    }
    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_artist,viewGroup,false);
        return new ArtistHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder artistHolder, int i) {
        Artist artist=artists.get(i);
        artistHolder.setDetails(artist);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }
}
