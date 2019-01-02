package com.example.kiki.searchevent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class UpcommingAdapterClass extends RecyclerView.Adapter<UpcomingHolder> {
    private Context context;
    private ArrayList<UpcommingEvent> upcommingEvents;
    UpcommingAdapterClass(Context context,ArrayList<UpcommingEvent> upcommingEvents){
        this.context=context;
        this.upcommingEvents=upcommingEvents;
    }
    @NonNull
    @Override
    public UpcomingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_upcoming,viewGroup,false);
        return new UpcomingHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingHolder upcomingHolder, int i) {
        UpcommingEvent upcommingEvent=upcommingEvents.get(i);
        upcomingHolder.setDetails(upcommingEvent);
    }

    @Override
    public int getItemCount() {
        return upcommingEvents.size();
    }
}
