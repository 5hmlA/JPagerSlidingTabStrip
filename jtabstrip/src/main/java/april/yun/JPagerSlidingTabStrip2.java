package april.yun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import april.yun.other.JTabStyleDelegate;
import april.yun.other.SavedState;
import april.yun.tabstyle.JTabStyle;
import april.yun.widget.PromptView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author yun.
 * @date 2016/12/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JPagerSlidingTabStrip2 extends LinearLayout implements ISlidingTabStrip {

    private static final String TAG = JPagerSlidingTabStrip2.class.getSimpleName();
    private JTabStyleDelegate mTabStyleDelegate;
    private JTabStyle mJTabStyle;
    private int mLastCheckedPosition = -1;
    private int mState = -1;

    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    //private LinearLayout tabsContainer;
    private ViewPager pager;
    private Locale locale;
    private int mTabCount;
    private int lastScrollX = 0;
    private float currentPositionOffset = 0f;
    private List<TextPaint> mTextPaints = new ArrayList<>();


    public JPagerSlidingTabStrip2(Context context) {
        this(context, null);
    }


    public JPagerSlidingTabStrip2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JPagerSlidingTabStrip2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);
        //tabsContainer = this;
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        //tabsContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
        mTabStyleDelegate = new JTabStyleDelegate().obtainAttrs(this, attrs, getContext());
    }


    public void bindViewPager(ViewPager pager) {
        this.pager = pager;
        mJTabStyle = mTabStyleDelegate.getJTabStyle();
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }


    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


    public void notifyDataSetChanged() {

        //tabsContainer.
        removeAllViews();
        mTabCount = pager.getAdapter().getCount();
        if (mJTabStyle.needChildView()) {
            for (int i = 0; i < mTabCount; i++) {
                if (pager.getAdapter() instanceof IconTabProvider) {
                    if (((IconTabProvider) pager.getAdapter()).getPageIconResIds(i) != null) {
                        addIconTab(i, pager.getAdapter().getPageTitle(i).toString(),
                                ((IconTabProvider) pager.getAdapter()).getPageIconResIds(i));
                    }
                    else {
                        addIconTab(i, pager.getAdapter().getPageTitle(i).toString(),
                                ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
                    }
                }
                else {
                    addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
                }
            }
            updateTabStyles();
            check(mTabStyleDelegate.setCurrentPosition(pager.getCurrentItem()));
        }
        mJTabStyle.afterSetViewPager(this);
    }


    private void addTextTab(final int position, String title) {
        addIconTab(position, title, 0);
    }


    private void addIconTab(final int position, String title, @NonNull @Size(min = 1) int... resId) {
        if (TextUtils.isEmpty(title)) {
            Log.e(TAG, "title is null ");
            return;
        }
        PromptView tab = new PromptView(getContext());
        tab.setColor_bg(mTabStyleDelegate.getPromptBgColor());
        tab.setColor_num(mTabStyleDelegate.getPromptNumColor());
        if (!mTabStyleDelegate.isNotDrawIcon() && resId.length > 0) {
            if (mTabStyleDelegate.getTabIconGravity() == Gravity.NO_GRAVITY) {
                if (resId.length > 1) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        tab.setBackground(getListDrable(resId));
                    }
                    else {
                        tab.setBackgroundDrawable(getListDrable(resId));
                    }
                }
                else {
                    tab.setBackgroundResource(resId[0]);
                }
            }
            else {
                setPadding(0, getPaddingTop(), 0, getPaddingBottom());
                mTabStyleDelegate.setShouldExpand(true);
                tab.setCompoundDrawablePadding(0);
                Drawable tabIcon = ContextCompat.getDrawable(getContext(), resId[0]);
                if (resId.length > 1) {
                    tabIcon = getListDrable(resId);
                }
                switch (mTabStyleDelegate.getTabIconGravity()) {
                    case Gravity.TOP:
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, tabIcon, null, null);
                        break;
                    case Gravity.BOTTOM:
                        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, null, tabIcon);
                        break;
                    case Gravity.START:
                        tab.setCompoundDrawablesWithIntrinsicBounds(tabIcon, null, null, null);
                        break;
                    case Gravity.END:
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
        if (mTabStyleDelegate.getCurrentPosition() == 0) {
            pageListener.onPageSelected(0);
        }
    }


    private StateListDrawable getListDrable(@NonNull int... resId) {
        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable.addState(new int[] { android.R.attr.state_checked },
                ContextCompat.getDrawable(getContext(), resId[0]));
        listDrawable.addState(new int[] { android.R.attr.state_pressed },
                ContextCompat.getDrawable(getContext(), resId[0]));
        listDrawable.addState(new int[] {}, ContextCompat.getDrawable(getContext(), resId[1]));
        return listDrawable;
    }


    private void addTab(final int position, View tab) {
        //        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });
        tab.setPadding(mTabStyleDelegate.getTabPadding(), 0, mTabStyleDelegate.getTabPadding(), 0);
        addView(tab, position, expandedTabLayoutParams);
    }


    private void updateTabStyles() {

        for (int i = 0; i < mTabCount; i++) {

            View v = getChildAt(i);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabStyleDelegate.getTabTextSize());
                tab.setTypeface(mTabStyleDelegate.getTabTypeface(), mTabStyleDelegate.getTabTypefaceStyle());
                if (mTabStyleDelegate.getTabTextColorStateList() == null) {
                    tab.setTextColor(mTabStyleDelegate.getTabTextColor());
                }
                else {
                    tab.setTextColor(mTabStyleDelegate.getTabTextColorStateList());
                }
                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (mTabStyleDelegate.isTextAllCaps()) {
                    tab.setAllCaps(true);
                }
            }
        }
    }


    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!mJTabStyle.needChildView() || getChildCount() > 0) {
            mJTabStyle.onSizeChanged(w, h, oldw, oldh);
        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mJTabStyle.needChildView()  && getChildCount() == 0 || isInEditMode() ||
                mTabCount == 0) {
            return;
        }

        mJTabStyle.onDraw(canvas, this, currentPositionOffset, mLastCheckedPosition);
    }


    private class PageListener implements OnPageChangeListener {

        private int mSelectedPosition;


        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            mTabStyleDelegate.setCurrentPosition(position);
            currentPositionOffset = positionOffset;
            if (mLastCheckedPosition != mSelectedPosition) {
                check(mSelectedPosition);
            }
            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }


        @Override public void onPageScrollStateChanged(int state) {
            //setCurrentItem触发 2--0
            //由手指滑动触发 1--2--0
            if (state == 1) {
                mJTabStyle.scrollSelected(true);
            }
            if (state == 2) {
                mJTabStyle.scrollSelected(mState==1);//由手指滑动触发 1--2--0
            }
            mState = state;
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                mJTabStyle.scrollSelected(false);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }


        @Override public void onPageSelected(int position) {
            mSelectedPosition = position;
            //check(position);
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }
    }


    //单选
    private void check(int position) {
        if (mLastCheckedPosition != -1 && mJTabStyle.needChildView()) {
            ((Checkable) getChildAt(mLastCheckedPosition)).setChecked(false);
        }
        mLastCheckedPosition = position;
        if (!mJTabStyle.needChildView()) {
            return;
        }
        ((Checkable) getChildAt(position)).setChecked(true);
    }


    public ISlidingTabStrip setPromptNum(int index, int num) {
        if (index < getChildCount()) {
            ((PromptView) getChildAt(index)).setPromptNum(num);
        }
        return this;
    }


    @Override public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mTabStyleDelegate.setCurrentPosition(savedState.currentPosition);
        requestLayout();
    }


    @Override public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mTabStyleDelegate.getCurrentPosition();
        return savedState;
    }


    public JTabStyleDelegate getTabStyleDelegate() {
        return mTabStyleDelegate;
    }


    public void setJTabStyle(JTabStyle JTabStyle) {
        mJTabStyle = JTabStyle;
    }


    public int getTabCount() {
        return mTabCount;
    }


    public int getState() {
        return mState;
    }


    public ViewGroup getTabsContainer() {
        return this;
    }
}
