package com.example.kiki.searchevent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity{
    MainFavFragmentAdapterClass mainFavFragmentAdapterClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("debugmsg:onCreate","onCreate");
        ActionBar ab=getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.rgb(3,169,245)));
        TabLayout tabLayout=findViewById(R.id.main_tablayout);
        tabLayout.setBackgroundColor(Color.rgb(3,169,245));
        TabLayout.Tab tabSearch=tabLayout.newTab();
        tabSearch.setText("SEARCH");
        tabLayout.addTab(tabSearch);
        TabLayout.Tab tabFav=tabLayout.newTab();
        tabFav.setText("FAVORITE");
        tabLayout.addTab(tabFav);
        mainFavFragmentAdapterClass=new MainFavFragmentAdapterClass(getSupportFragmentManager(),tabLayout.getTabCount());
        final ViewPager viewPager=findViewById(R.id.main_viewpager);
        viewPager.setAdapter(mainFavFragmentAdapterClass);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}
