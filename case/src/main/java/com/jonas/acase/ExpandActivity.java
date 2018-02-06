package com.jonas.acase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.jonas.acase.eventbusmsg.PromptMsg;
import com.jonas.acase.fragment.DemoCardFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.security.SecureRandom;

import april.yun.ISlidingTabStrip;
import april.yun.other.JTabStyleDelegate;

import static april.yun.other.JTabStyleBuilder.STYLE_DEFAULT;
import static april.yun.other.JTabStyleBuilder.STYLE_DOTS;
import static april.yun.other.JTabStyleBuilder.STYLE_ROUND;

public class ExpandActivity extends FragmentActivity {
    private ISlidingTabStrip tabs_up;
    private ISlidingTabStrip tabs_up1;
    private ISlidingTabStrip tabs_up2;
    private ISlidingTabStrip tabs_up3;
    private ISlidingTabStrip dots;
    private ISlidingTabStrip tabs_buttom;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private int[] mPressed;
    private int[] mNormal;
    private int[] mSelectors;
    private String[] mTitles;


    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand);
        setActionBar(( (Toolbar)findViewById(R.id.toolbar) ));
        EventBus.getDefault().register(this);
        tabs_up = (ISlidingTabStrip)findViewById(R.id.tabs);
        tabs_up1 = (ISlidingTabStrip)findViewById(R.id.tabs_1);
        tabs_up2 = (ISlidingTabStrip)findViewById(R.id.tabs_2);
        tabs_up3 = (ISlidingTabStrip)findViewById(R.id.tabs_3);

        tabs_buttom = (ISlidingTabStrip)findViewById(R.id.tab_buttom);
        dots = (ISlidingTabStrip)findViewById(R.id.dots);


        setupTabStrips();

        setupViewpager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()) {

            case R.id.sts_badge:
                startActivity(new Intent(this, NotExpandActicity.class));
                return true;
            case R.id.sts_promptview:
                startActivity(new Intent(this, PromptActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupTabStrips(){

        setupStrip(tabs_up.getTabStyleDelegate(), STYLE_ROUND);
        setupStrip(tabs_up1.getTabStyleDelegate(), STYLE_DEFAULT);
        setupStrip(tabs_up2.getTabStyleDelegate(), STYLE_DEFAULT);
        setupStrip(tabs_up3.getTabStyleDelegate(), STYLE_DEFAULT);
        setupStrip(tabs_buttom.getTabStyleDelegate(), STYLE_DEFAULT);
        setupStrip(dots.getTabStyleDelegate(), STYLE_DOTS);
        tabs_buttom.getTabStyleDelegate().setFrameColor(Color.TRANSPARENT).setIndicatorColor(Color.TRANSPARENT)
                .setTabIconGravity(Gravity.TOP)//图标显示在top
                .setIndicatorHeight(-8)//设置的高小于0 会显示在tab顶部 否则底部
                .setDividerColor(Color.TRANSPARENT);
        tabs_up.getTabStyleDelegate().setNotDrawIcon(true).setTextColor(Color.WHITE, Color.parseColor("#009688"))
                .setNeedTabTextColorScrollUpdate(true).setNeedTabTextColorScrollUpdate(true).setFrameColor(Color.parseColor("#45C01A"));
        tabs_up1.getTabStyleDelegate().setNotDrawIcon(true).setNeedTabTextColorScrollUpdate(true).setCornerRadio(40)
                //.setIndicatorHeight(60)
                .setTextColor(Color.WHITE, Color.parseColor("#009688")).setIndicatorColor(Color.parseColor("#009688"))
                .setFrameColor(Color.parseColor("#009688")).setDividerColor(Color.TRANSPARENT);

        tabs_up2.getTabStyleDelegate().setNotDrawIcon(true)//不现实图标
                .setDividerColor(Color.TRANSPARENT).setIndicatorColor(Color.WHITE).setTextColor(Color.WHITE, Color.GRAY)
                .setFrameColor(Color.TRANSPARENT).setDividerPadding(20).setIndicatorHeight(5);

        //tabs_up3.getTabStyleDelegate().getJTabStyle().moveStyle = MOVESTYLE_DEFAULT;
        tabs_up3.getTabStyleDelegate().setNotDrawIcon(true).setDividerColor(Color.TRANSPARENT)
                .setNeedTabTextColorScrollUpdate(true).setIndicatorColor(Color.parseColor("#4B6A8A"))
                .setTextColor(Color.WHITE, Color.DKGRAY).setFrameColor(Color.TRANSPARENT);
    }


    private void setupStrip(JTabStyleDelegate tabStyleDelegate, int type){
        tabStyleDelegate.setJTabStyle(type).setShouldExpand(true).setFrameColor(Color.parseColor("#45C01A"))
                .setTabTextSize(getDimen(R.dimen.tabstrip_textsize))
                .setTextColor(Color.parseColor("#45C01A"), Color.GRAY)
//                .setDividerWidth(6)
                //.setTextColor(R.drawable.tabstripbg)
                .setDividerColor(Color.parseColor("#45C01A")).setDividerPadding(0)
                .setUnderlineColor(Color.parseColor("#3045C01A")).setUnderlineHeight(0)
                .setIndicatorColor(Color.parseColor("#7045C01A"))
                .setIndicatorHeight(getDimen(R.dimen.sug_event_tabheight)).setFrameColor(Color.TRANSPARENT);
    }


    private void setupViewpager(){

        mTitles = getResources().getStringArray(R.array.tabs);
        mNormal = new int[]{R.drawable.ic_tab_msg, R.drawable.ic_tab_contact, R.drawable.ic_tab_moments, R.drawable.ic_tab_profile};
        mPressed = new int[]{R.drawable.ic_tab_msg_h, R.drawable.ic_tab_contact_h, R.drawable.ic_tab_moments_h, R.drawable.ic_tab_profile_h};
        mSelectors = new int[]{R.drawable.tab1, R.drawable.tab2, R.drawable.tab3, R.drawable.tab4};

        pager = (ViewPager)findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int)TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs_up.bindViewPager(pager);
        tabs_up1.bindViewPager(pager);
        tabs_up2.bindViewPager(pager);
        //tabs_up2.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);
        tabs_up3.bindViewPager(pager);
        //tabs_up3.setPromptNum(2, 18);
        tabs_buttom.bindViewPager(pager);
        //tabs_buttom.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);
        //tabs_up1.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);
        //tabs_up.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);
        //tabs_up3.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);

        dots.bindViewPager(pager);
    }


    public void setPromptMsg(ISlidingTabStrip slidingTabStrip){
        for(int i = 0; i<pager.getAdapter().getCount(); i++) {
            slidingTabStrip.setPromptNum(i, -3+new SecureRandom().nextInt(10));
        }
    }


    public void clearPromptMsg(ISlidingTabStrip slidingTabStrip){
        for(int i = 0; i<pager.getAdapter().getCount(); i++) {
            slidingTabStrip.setPromptNum(i, 0);
        }
    }


    public void showPromptMsg(ISlidingTabStrip slidingTabStrip){
        slidingTabStrip.setPromptNum(1, 9).setPromptNum(0, 10).setPromptNum(2, -9).setPromptNum(3, 100);
    }


    @Subscribe
    public void randomMsg(PromptMsg msg){
        if(msg.mType == PromptMsg.RANDOM) {
            setPromptMsg(tabs_up);
            setPromptMsg(tabs_up1);
            setPromptMsg(tabs_up2);
            setPromptMsg(tabs_up3);
            setPromptMsg(tabs_buttom);
        }
    }


    @Subscribe
    public void showMsg(PromptMsg msg){
        if(msg.mType == PromptMsg.SHOW) {
            showPromptMsg(tabs_up);
            showPromptMsg(tabs_up1);
            showPromptMsg(tabs_up2);
            showPromptMsg(tabs_up3);
            showPromptMsg(tabs_buttom);
        }
    }


    @Subscribe
    public void clearMsg(PromptMsg msg){
        if(msg.mType == PromptMsg.CLEAR) {
            clearPromptMsg(tabs_up);
            clearPromptMsg(tabs_up1);
            clearPromptMsg(tabs_up2);
            clearPromptMsg(tabs_up3);
            clearPromptMsg(tabs_buttom);
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter implements ISlidingTabStrip.IconTabProvider {

        public MyPagerAdapter(FragmentManager fm){
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position){
            return mTitles[position%4];
        }


        @Override
        public int getCount(){
            return mTitles.length;
        }


        @Override
        public Fragment getItem(int position){
            return DemoCardFragment.newInstance(position);
        }


        @Override
        public int[] getPageIconResIds(int position){
            //return new int[]{mNormal[position%4],mPressed[position%4]};
            return null;
        }


        @Override
        public int getPageIconResId(int position){
            //		return mPressed[position];
            return mSelectors[position%4];
        }
    }


    private int getDimen(int dimen){
        return (int)getResources().getDimension(dimen);
    }
}
