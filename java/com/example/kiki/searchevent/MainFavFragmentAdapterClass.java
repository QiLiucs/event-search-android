package com.example.kiki.searchevent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFavFragmentAdapterClass extends FragmentStatePagerAdapter {
    int tabCount;
    public MainFavFragmentAdapterClass(FragmentManager fragmentManager, int tabCount){
        super(fragmentManager);
        this.tabCount=tabCount;
    }
    @Override
    public Fragment getItem(int i) {
        if(i==0){
            MainFragment mainFragment=new MainFragment();
            return mainFragment;
        }
        else if(i==1){
            FavoriteFragment favoriteFragment=new FavoriteFragment();
            return favoriteFragment;
        }
        return null;
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}
