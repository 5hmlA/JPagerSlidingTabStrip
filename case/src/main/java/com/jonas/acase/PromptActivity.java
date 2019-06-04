package com.jonas.acase;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import april.yun.JPagerSlidingTabStrip;

import static april.yun.other.JTabStyleBuilder.STYLE_DEFAULT;
import static april.yun.other.JTabStyleBuilder.STYLE_ROUND;

public class PromptActivity extends AppCompatActivity {

    private int[] mPressed;
    private int[] mSelectors;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPressed = new int[] { R.drawable.ic_tab_msg_h, R.drawable.ic_tab_contact_h,
                R.drawable.ic_tab_moments_h, R.drawable.ic_tab_profile_h };
        mSelectors = new int[] { R.drawable.tab1, R.drawable.tab2, R.drawable.tab3, R.drawable.tab4 };

        JPagerSlidingTabStrip tabStrip = (JPagerSlidingTabStrip)findViewById(R.id.tabs);
        JPagerSlidingTabStrip tabStrip2 = (JPagerSlidingTabStrip)findViewById(R.id.tabs2);

        tabStrip.getTabStyleDelegate().setJTabStyle(STYLE_ROUND)
                .setShouldExpand(true)
                .setFrameColor(Color.parseColor("#45C01A"))
                .setCornerRadio(40)
                .setIndicatorHeight(80)
                .setIndicatorColor(Color.parseColor("#45C01A"))
                .setTextColor(Color.parseColor("#45C01A"),Color.GRAY);
        tabStrip2.getTabStyleDelegate().setJTabStyle(STYLE_DEFAULT).setTabIconGravity(Gravity.TOP)
                .setShouldExpand(true)
                .setFrameColor(Color.TRANSPARENT)
                .setCornerRadio(40)
                .setIndicatorHeight(80)
                .setIndicatorColor(Color.TRANSPARENT)
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
        tabStrip2.coifigTabIcons(mSelectors[0],mSelectors[1],mSelectors[2],mSelectors[3]);
        tabStrip2.bindTitles("微信", "通讯里", "发现","我");
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
