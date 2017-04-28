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
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
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
public class JPagerSlidingTabStrip extends HorizontalScrollView implements ISlidingTabStrip {

    private static final String TAG = JPagerSlidingTabStrip.class.getSimpleName();
    private JTabStyleDelegate mTabStyleDelegate;
    private int scrollOffset = 52;
    private JTabStyle mJTabStyle;
    private int mLastCheckedPosition = -1;
    private int mState = -1;

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


    public JPagerSlidingTabStrip(Context context) {
        this(context, null);
    }


    public JPagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JPagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFillViewport(true);
        setWillNotDraw(false);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(tabsContainer, layoutParams);
        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        //expandedTabLayoutParams.gravity=Gravity.CENTER_VERTICAL;
        //defaultTabLayoutParams.gravity=Gravity.CENTER_VERTICAL;
        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
        mTabStyleDelegate = new JTabStyleDelegate().obtainAttrs(this, attrs, getContext());
        mJTabStyle = mTabStyleDelegate.getJTabStyle();
    }


    public void bindViewPager(ViewPager pager) {
        this.pager = pager;
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

        tabsContainer.removeAllViews();
        mTabCount = pager.getAdapter().getCount();
        if (mJTabStyle.needChildView()) {
            for (int i = 0; i < mTabCount; i++) {
                if (pager.getAdapter() instanceof IconTabProvider) {
                    //有提供icon
                    Log.d(TAG, "haove tabIcon");
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
                    //没有提供icon
                    Log.d(TAG, "haove no tabIcon");
                    mTabStyleDelegate.setNotDrawIcon(true);
                    addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
                }
            }
            updateTabStyles();
            check(mTabStyleDelegate.setCurrentPosition(pager.getCurrentItem()));
        }
        mJTabStyle.afterSetViewPager(tabsContainer);
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
        if (!mTabStyleDelegate.isNotDrawIcon()) {
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
        tabsContainer.addView(tab, position,
                mTabStyleDelegate.isShouldExpand() ? expandedTabLayoutParams : defaultTabLayoutParams);
    }


    private void updateTabStyles() {

        for (int i = 0; i < mTabCount; i++) {

            View v = tabsContainer.getChildAt(i);
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
                tab.setAllCaps(mTabStyleDelegate.isTextAllCaps());
            }
        }
    }


    private void scrollToChild(int position, int offset) {
        if (!mJTabStyle.needChildView()) return;
        if (mTabCount == 0) {
            return;
        }
        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }


    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!mJTabStyle.needChildView() || tabsContainer.getChildCount() > 0) {
            mJTabStyle.onSizeChanged(w, h, oldw, oldh);
        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mJTabStyle.needChildView() && tabsContainer.getChildCount() == 0 || isInEditMode() ||
                mTabCount == 0) {
            return;
        }

        mJTabStyle.onDraw(canvas, tabsContainer, currentPositionOffset, mLastCheckedPosition);
    }


    private class PageListener implements OnPageChangeListener {

        private int mSelectedPosition;


        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mTabStyleDelegate.setCurrentPosition(position);
            currentPositionOffset = positionOffset;
            if (mLastCheckedPosition != mSelectedPosition) {
                check(mSelectedPosition);
            }
            if (mJTabStyle.needChildView()) {
                scrollToChild(position,
                        (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
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
                scrollToChild(pager.getCurrentItem(), 0);
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
            ((Checkable) tabsContainer.getChildAt(mLastCheckedPosition)).setChecked(false);
        }
        mLastCheckedPosition = position;
        if (!mJTabStyle.needChildView()) {
            return;
        }
        ((Checkable) tabsContainer.getChildAt(position)).setChecked(true);
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
        return tabsContainer;
    }


    public ISlidingTabStrip setPromptNum(int index, int num) {
        if (index < tabsContainer.getChildCount()) {
            ((PromptView) tabsContainer.getChildAt(index)).setPromptNum(num);
        }
        return this;
    }


    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
