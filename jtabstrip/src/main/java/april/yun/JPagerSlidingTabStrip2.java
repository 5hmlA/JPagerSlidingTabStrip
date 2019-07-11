package april.yun;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import april.yun.other.JTabStyleDelegate;
import april.yun.other.OntheSamePositionClickListener;
import april.yun.other.SavedState;
import april.yun.tabstyle.JTabStyle;
import april.yun.widget.PromptTextView;

/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JPagerSlidingTabStrip2 extends HorizontalScrollView implements ISlidingTabStrip, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private static final String TAG = JPagerSlidingTabStrip2.class.getSimpleName();
    private JTabStyleDelegate mTabStyleDelegate;
    private int scrollOffset = 52;
    private JTabStyle mJTabStyle;
    private int mLastCheckedPosition = -1;
    private int mState = -1;
    int mDrawablePadding;
    //            case com.android.internal.R.styleable.TextView_drawablePadding:
    //    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;
    private Locale locale;
    private int mTabCount;
    private int lastScrollX = 0;
    private float currentPositionOffset = 0f;
    private List<TextPaint> mTextPaints = new ArrayList<>();
    private ValueAnimator mValueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(400);
    private int mLastLastCheckPosition = -1;
    private int[] mIconIds = new int[1];
    private int[][] mIconsIds = new int[1][2];
    public boolean mPromptOnlyNum;
    private OntheSamePositionClickListener mSamePositionClickListener;

    @keep
    public JPagerSlidingTabStrip2(Context context){
        this(context, null);
    }


    @keep
    public JPagerSlidingTabStrip2(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }


    @keep
    public JPagerSlidingTabStrip2(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(tabsContainer, layoutParams);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        //expandedTabLayoutParams.gravity=Gravity.CENTER_VERTICAL;
        //defaultTabLayoutParams.gravity=Gravity.CENTER_VERTICAL;
        if(locale == null) {
            locale = getResources().getConfiguration().locale;
        }
        mTabStyleDelegate = new JTabStyleDelegate().obtainAttrs(this, attrs, getContext());
        mJTabStyle = mTabStyleDelegate.getJTabStyle();
    }

    @keep
    public void bindViewPager(ViewPager pager){
        this.pager = pager;
        if(pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    @keep
    public void setOnPageChangeListener(OnPageChangeListener listener){
        this.delegatePageListener = listener;
    }


    public void notifyDataSetChanged(){

        tabsContainer.removeAllViews();
        mTabCount = pager.getAdapter().getCount();
        if(mJTabStyle.needChildView()) {
            for(int i = 0; i<mTabCount; i++) {
                if(pager.getAdapter() instanceof IconTabProvider) {
                    //有提供icon
                    Log.d(TAG, "haove tabIcon");
                    if(( (IconTabProvider)pager.getAdapter() ).getPageIconResIds(i) != null) {
                        addIconTab(i, pager.getAdapter().getPageTitle(i),
                                ( (IconTabProvider)pager.getAdapter() ).getPageIconResIds(i));
                    }else {
                        addIconTab(i, pager.getAdapter().getPageTitle(i),
                                ( (IconTabProvider)pager.getAdapter() ).getPageIconResId(i));
                    }
                }else {
                    //没有提供icon
                    Log.d(TAG, "haove no tabIcon");
                    mTabStyleDelegate.setNotDrawIcon(true);
                    addTextTab(i, pager.getAdapter().getPageTitle(i));
                }
            }
            updateTabStyles();
            check(mTabStyleDelegate.setCurrentPosition(pager.getCurrentItem()));
        }
    }


    private void addTextTab(final int position, CharSequence title){
        addIconTab(position, title, 0);
    }


    private void addIconTab(final int position, CharSequence title, @NonNull @Size(min = 1) int... resId){
        if(TextUtils.isEmpty(title)) {
            Log.e(TAG, "title is null ");
            return;
        }
        PromptTextView tab = new PromptTextView(getContext());
        tab.setGravity(Gravity.CENTER);
        tab.configPrompt(mTabStyleDelegate.getPromptBgColor(), mTabStyleDelegate.getPromptNumColor());
        tab.setMaxLines(mTabStyleDelegate.getMaxLines());
        setPadding(0, getPaddingTop(), 0, getPaddingBottom());
        if(!mTabStyleDelegate.isNotDrawIcon()) {
            if(mTabStyleDelegate.getTabIconGravity() == Gravity.NO_GRAVITY) {
                setPadding(0, 0, 0, 0);//disable pading
                if(resId.length>1) {
                    if(Build.VERSION.SDK_INT>Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        tab.setBackground(getListDrable(resId));
                    }else {
                        tab.setBackgroundDrawable(getListDrable(resId));
                    }
                }else {
                    tab.setBackgroundResource(resId[0]);
                }
            }else {
                mTabStyleDelegate.setShouldExpand(true);
                tab.setCompoundDrawablePadding(mTabStyleDelegate.getDrawablePading());
                Drawable tabIcon = ContextCompat.getDrawable(getContext(), resId[0]);
                if(resId.length>1) {
                    tabIcon = getListDrable(resId);
                }
                switch(mTabStyleDelegate.getTabIconGravity()) {
                    case Gravity.TOP:
                        //有图片 需要设置pading不然可能图片和文字的pading很大
                        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, tabIcon, null, null);
                        break;
                    case Gravity.BOTTOM:
                        //有图片 需要设置pading不然可能图片和文字的pading很大
                        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, null, tabIcon);
                        break;
                    case Gravity.LEFT:
                        tab.setCompoundDrawablesWithIntrinsicBounds(tabIcon, null, null, null);
                        break;
                    case Gravity.RIGHT:
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, tabIcon, null);
                        break;
                    default:
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, tabIcon, null, null);
                        break;
                }
            }
        }
        tab.setText(title);
        addTab(position, tab);
        //        if(mTabStyleDelegate.getCurrentPosition() == 0) {
        //            pageListener.onPageSelected(0);
        //        }
    }


    private StateListDrawable getListDrable(@NonNull int... resId){
        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable
                .addState(new int[]{android.R.attr.state_checked}, ContextCompat.getDrawable(getContext(), resId[0]));
        listDrawable
                .addState(new int[]{android.R.attr.state_pressed}, ContextCompat.getDrawable(getContext(), resId[0]));
        listDrawable.addState(new int[]{}, ContextCompat.getDrawable(getContext(), resId[1]));
        return listDrawable;
    }


    private void addTab(final int position, View tab){
        //        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                setCurrentPosition(position);
            }
        });
        tab.setPadding(mTabStyleDelegate.getTabPadding(), 0, mTabStyleDelegate.getTabPadding(), 0);
        tabsContainer.addView(tab, position,
                mTabStyleDelegate.isShouldExpand() ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles(){

        for(int i = 0; i<mTabCount; i++) {

            View v = tabsContainer.getChildAt(i);
            if(v instanceof TextView) {
                TextView tab = (TextView)v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabStyleDelegate.getTabTextSize());
                tab.setTypeface(mTabStyleDelegate.getTabTypeface(), mTabStyleDelegate.getTabTypefaceStyle());
                if(mTabStyleDelegate.getTabTextColorStateList() == null) {
                    tab.setTextColor(mTabStyleDelegate.getTabTextColor());
                }else {
                    tab.setTextColor(mTabStyleDelegate.getTabTextColorStateList());
                }
                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                tab.setAllCaps(mTabStyleDelegate.isTextAllCaps());
            }
        }
        mJTabStyle.afterSetViewPager(tabsContainer);
    }


    private void scrollToChild(int position, int offset){
        if(!mJTabStyle.needChildView()) {
            return;
        }
        if(mTabCount == 0) {
            return;
        }
        int newScrollX = tabsContainer.getChildAt(position).getLeft()+offset;

        if(position>0 || offset>0) {
            newScrollX -= scrollOffset;
        }
        if(newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        scrollOffset = mTabStyleDelegate.getScrollOffset();
        if(!mJTabStyle.needChildView() || tabsContainer.getChildCount()>0) {
            mJTabStyle.onSizeChanged(w, h, oldw, oldh);
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(mJTabStyle.needChildView() && tabsContainer.getChildCount() == 0 || isInEditMode() || mTabCount == 0) {
            return;
        }
        mJTabStyle.onDraw(canvas, tabsContainer, currentPositionOffset, mLastCheckedPosition);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        super.onLayout(changed, l, t, r, b);
        mJTabStyle.afterLayout();
    }


    @keep
    public void bindTitles(String... titles){
        bindTitles(0, titles);
    }

    /**
     * 设置title的icon必须在bindTitles之前调用
     *
     * @param iconIds
     */
    @keep
    public JPagerSlidingTabStrip2 coifigTabIcons(@DrawableRes @Size(min = 1) int... iconIds){
        mIconIds = iconIds;
        return this;
    }

    /**
     * 两张图片为一组[选中,默认]
     *
     * @param iconsIds
     * @return
     */
    @keep
    public JPagerSlidingTabStrip2 coifigTabIcons(@DrawableRes @Size(min = 1) int[]... iconsIds){
        mIconsIds = iconsIds;
        return this;
    }

    @keep
    public void bindTitles(int current, String... titles){
        if(titles.length == 1) {
            for(int i = 0; i<titles.length; i++) {
                addTextTab(i, titles[i]);
            }
        }else {
            if(titles.length == mIconsIds.length) {
                for(int i = 0; i<titles.length; i++) {
                    addIconTab(i, titles[i], mIconsIds[i][0], mIconsIds[i][1]);
                }
            }else if(titles.length == mIconIds.length) {
                for(int i = 0; i<titles.length; i++) {
                    addIconTab(i, titles[i], mIconIds[i]);
                }
            }else {
                for(int i = 0; i<titles.length; i++) {
                    addTextTab(i, titles[i]);
                }
            }
        }
        setCurrentPosition(current);
        setTag(current);
        mTabCount = titles.length;
        updateTabStyles();
    }

    @keep
    public void setCurrentPosition(int position){
        setTag(position);
        if(mLastCheckedPosition == position || mValueAnimator.isRunning()) {
            if(mSamePositionClickListener != null) {
                mSamePositionClickListener.onClickTheSamePosition(position);
            }
            return;
        }
        if(pager != null) {
            pager.setCurrentItem(position);
        }else {
            tabsContainer.setTag(true);
            mValueAnimator = ValueAnimator.ofFloat(1, 0);
            if(mLastCheckedPosition != -1 && mLastCheckedPosition<position) {
                tabsContainer.setTag(false);
                mValueAnimator = ValueAnimator.ofFloat(0, 1);
            }
            check(position);
            mValueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            mValueAnimator.addUpdateListener(this);
            mValueAnimator.addListener(this);
            mValueAnimator.start();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation){
        float positionOffset = (float)animation.getAnimatedValue();
        mTabStyleDelegate.setCurrentPosition(mLastCheckedPosition);
        currentPositionOffset = positionOffset;
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation){
        if(!( (Boolean)tabsContainer.getTag() )) {
            mLastCheckedPosition -= 1;
            mLastCheckedPosition = Math.max(mLastCheckedPosition, 0);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation){
        if(!( (Boolean)tabsContainer.getTag() )) {
            mLastCheckedPosition += 1;
            invalidate();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation){

    }

    @Override
    public void onAnimationRepeat(Animator animation){

    }

    private class PageListener implements OnPageChangeListener {

        private int mSelectedPosition;


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
            mTabStyleDelegate.setCurrentPosition(position);
            currentPositionOffset = positionOffset;
            if(mLastCheckedPosition != mSelectedPosition) {
                check(mSelectedPosition);
            }
            if(mJTabStyle.needChildView()) {
                scrollToChild(position, (int)( positionOffset*tabsContainer.getChildAt(position).getWidth() ));
            }
            invalidate();

            if(delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }


        @Override
        public void onPageScrollStateChanged(int state){
            //setCurrentItem触发 2--0
            //由手指滑动触发 1--2--0
            if(state == 1) {
                mJTabStyle.scrollSelected(true);
            }
            if(state == 2) {
                mJTabStyle.scrollSelected(mState == 1);//由手指滑动触发 1--2--0
            }
            mState = state;
            if(state == ViewPager.SCROLL_STATE_IDLE) {
                mJTabStyle.scrollSelected(false);
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if(delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }


        @Override
        public void onPageSelected(int position){
            mSelectedPosition = position;
            //check(position);
            if(delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }
    }


    //单选
    private void check(int position){
        if(mLastCheckedPosition != -1 && mJTabStyle.needChildView()) {
            ( (Checkable)tabsContainer.getChildAt(mLastCheckedPosition) ).setChecked(false);
        }
        mLastLastCheckPosition = mLastCheckedPosition;
        mLastCheckedPosition = position;
        if(pager == null && delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
        if(!mJTabStyle.needChildView()) {
            return;
        }
        ( (Checkable)tabsContainer.getChildAt(position) ).setChecked(true);
    }


    @Override
    public void onRestoreInstanceState(Parcelable state){
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mTabStyleDelegate.setCurrentPosition(savedState.currentPosition);
        requestLayout();
    }


    @Override
    public Parcelable onSaveInstanceState(){
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mTabStyleDelegate.getCurrentPosition();
        return savedState;
    }


    @keep
    public JTabStyleDelegate getTabStyleDelegate(){
        return mTabStyleDelegate;
    }


    @keep
    public void setJTabStyle(JTabStyle JTabStyle){
        mJTabStyle = JTabStyle;
    }


    @keep
    public int getTabCount(){
        return mTabCount;
    }


    @keep
    public int getState(){
        return mState;
    }


    @keep
    public ViewGroup getTabsContainer(){
        return tabsContainer;
    }


    @keep
    public ISlidingTabStrip setPromptNum(int index, int num){
        if(index<tabsContainer.getChildCount()) {
            ( (PromptTextView)tabsContainer.getChildAt(index) ).setPromptMsg(num);
        }
        return this;
    }

    @keep
    public ISlidingTabStrip setPromptStr(int index, String str){
        if(index<tabsContainer.getChildCount()) {
            ( (PromptTextView)tabsContainer.getChildAt(index) ).setPromptMsg(str);
        }
        return this;
    }


    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
    }

    @keep
    public JPagerSlidingTabStrip2 setOntheSamePositionClickListener(OntheSamePositionClickListener samePositionClickListener){
        mSamePositionClickListener = samePositionClickListener;
        return this;
    }

}
