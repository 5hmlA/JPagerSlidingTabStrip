package com.jonas.acase;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import april.yun.JPagerSlidingTabStrip;

public class PromptActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        JPagerSlidingTabStrip tabStrip = (JPagerSlidingTabStrip)findViewById(R.id.tabs);

        tabStrip.getTabStyleDelegate()
                .setShouldExpand(true)
                .setFrameColor(Color.parseColor("#45C01A"))
                .setCornerRadio(40)
                .setIndicatorHeight(80)
                .setIndicatorColor(Color.parseColor("#45C01A"))
                .setTextColor(Color.parseColor("#45C01A"),Color.GRAY);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position){
                Toast.makeText(PromptActivity.this, ""+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state){

            }
        });

        tabStrip.bindTitles(3,"1", "2", "3","4","5","6");
//        tabStrip.bindTitles("1", "2", "3","4","5","6");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
