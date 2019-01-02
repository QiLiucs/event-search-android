package com.example.kiki.searchevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ArtistHolder extends RecyclerView.ViewHolder {
    private TextView tv_term, col_artistname, col_followers, col_popularity,col_checkat,
            col0_artistname, col0_followers, col0_popularity,col0_checkat;
    private LinearLayout layout;
    Context context;
    public ArtistHolder(View view,Context context){
        super(view);
        tv_term=view.findViewById(R.id.tv_term);
        col_artistname=view.findViewById(R.id.col_artistname);
        col_followers=view.findViewById(R.id.col_followers);
        col_popularity=view.findViewById(R.id.col_popular);
        col_checkat=view.findViewById(R.id.col_checkat);
        col0_artistname=view.findViewById(R.id.col0_artistname);
        col0_followers=view.findViewById(R.id.col0_followers);
        col0_popularity=view.findViewById(R.id.col0_popular);
        col0_checkat=view.findViewById(R.id.col0_checkat);
        layout=view.findViewById(R.id.ll_imgs);
        this.context=context;
    }
    public void setDetails(Artist artist){
        tv_term.setText(artist.getTerm());
        if(artist.getName().length()>0)
            col_artistname.setText(artist.getName());
        else{
            col_artistname.setVisibility(View.GONE);
            col0_artistname.setVisibility(View.GONE);
        }
        if(artist.getFollowers().length()>0)
            col_followers.setText(artist.getFollowers());
        else{
            col_followers.setVisibility(View.GONE);
            col0_followers.setVisibility(View.GONE);
        }
        if(artist.getPopular().length()>0)
            col_popularity.setText(artist.getPopular());
        else{
            col_popularity.setVisibility(View.GONE);
            col0_popularity.setVisibility(View.GONE);
        }
        if(artist.getCheckat().length()>0) {
            String html = "<a href='" + artist.getCheckat() + "'>Spotify</a>";
            col_checkat.setMovementMethod(LinkMovementMethod.getInstance());
            Spanned sp=Html.fromHtml(html);
            col_checkat.setText(sp);
        }
        else{
            col_checkat.setVisibility(View.GONE);
            col0_checkat.setVisibility(View.GONE);
        }
        for(String img:artist.getImgs()){
            ImageView imageView=new ImageView(context);
            Glide.with(context).load(img).into(imageView);
            layout.addView(imageView);
        }
    }
}
