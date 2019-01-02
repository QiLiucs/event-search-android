package com.example.kiki.searchevent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class UpcomingHolder extends RecyclerView.ViewHolder {
    private TextView tv_name, tv_artist, tv_time, tv_type;
    private View view;
    private Context context;
    public UpcomingHolder(View view, Context context){
        super(view);
        tv_name=view.findViewById(R.id.tv_name);
        tv_artist=view.findViewById(R.id.tv_artist);
        tv_time=view.findViewById(R.id.tv_time);
        tv_type=view.findViewById(R.id.tv_type);
        this.view=view;
        this.context=context;
    }
    public void setDetails(UpcommingEvent upcommingEvent){
        tv_name.setText(upcommingEvent.getName());
        tv_artist.setText(upcommingEvent.getArtist());
        tv_time.setText(upcommingEvent.getTime());
        tv_type.setText("Type: "+upcommingEvent.getType());
        final String url=upcommingEvent.getUrl();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                Log.d("debugmsg:click","hhh");
            }
        });

    }

}
