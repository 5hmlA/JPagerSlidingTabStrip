package april.yun.other;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import april.yun.ISlidingTabStrip;
import april.yun.tabstyle.JTabStyle;
import april.yun.widget.PromptView;
import com.jonas.librarys.R;

import static april.yun.other.JTabStyleBuilder.STYLE_DEFAULT;

/**
 * @author yun.
 * @date 2017/4/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class JTabStyleDelegate {

    private ISlidingTabStrip mTabStrip;
    private Context mContext;

    /**
     * 是否使用IconTabProvider提供的资源
     * true 不使用
     */
    private boolean mNotDrawIcon = false;
    private ColorStateList mTabTextColorStateList;

    private int mTabIconGravity = Gravity.NO_GRAVITY;

    private int currentPosition = 0;
    private int indicatorColor = Color.TRANSPARENT;
    private int underlineColor = 0;
    private int dividerColor = 0;
    //边框颜色
    private int mFrameColor = Color.TRANSPARENT;
    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};


    private boolean shouldExpand = true;
    private boolean textAllCaps = false;
    private int scrollOffset = 52;
    private int indicatorHeight = 5;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int tabPadding = 2;
    private int dividerWidth = 1;

    private int tabTextSize = 13;
    private int tabTextColor = 0xFF666666;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;
    private int mTabStyle = STYLE_DEFAULT;
    private JTabStyle mJTabStyle;
    private int mCornerRadio;
    private int mPromptBgColor=Color.RED;
    private int mPromptNumColor=Color.WHITE;
    private int mBackgroundColor;
    private boolean mNeedTabTextColorScrollUpdate;

    public JTabStyleDelegate obtainAttrs(ISlidingTabStrip tabStrip, AttributeSet attrs, Context context) {
        mTabStrip = tabStrip;mContext = context;
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight,
                indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight,
                underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding,
                dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight,
                tabPadding);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset,
                scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        return this;
    }


    public boolean isNotDrawIcon() {
        return mNotDrawIcon;
    }


    public ColorStateList getTabTextColorStateList() {
        return mTabTextColorStateList;
    }

    private ColorStateList getColorStateList(int... colots) {
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_checked};
        states[1] = new int[] {};
        ColorStateList colorStateList = new ColorStateList(states,colots);
        return colorStateList;
    }


    public int getCurrentPosition() {
        return currentPosition;
    }


    public int setCurrentPosition(int currentPosition) {
        return this.currentPosition = currentPosition;
    }


    public int getIndicatorColor() {
        return indicatorColor;
    }


    public JTabStyleDelegate setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        return this;
    }


    public int getUnderlineColor() {
        return underlineColor;
    }


    public JTabStyleDelegate setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        return this;
    }


    public int getDividerColor() {
        return dividerColor;
    }


    public JTabStyleDelegate setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        return this;
    }


    public boolean isShouldExpand() {
        return shouldExpand;
    }


    public JTabStyleDelegate setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        return this;
    }


    public boolean isTextAllCaps() {
        return textAllCaps;
    }


    public JTabStyleDelegate setTextAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
        return this;
    }

    /**
     * checked ,normol
     * @param colorStrs
     * @return
     */
    public JTabStyleDelegate setTextColor(@Size(value = 2) String... colorStrs) {
        int[][] states = new int[][] {
                new int[] {android.R.attr.state_checked},// unchecked
                new int[] {}   //normal
        };
        int[] colors = new int[2];
        for (int i = 0; i < colorStrs.length; i++) {
            colors[i] = Color.parseColor(colorStrs[i]);
        }
        mTabTextColorStateList = new ColorStateList(states,colors);
        return this;
    }

    public JTabStyleDelegate setTextColor(@Size(value = 2)@ColorInt int... colors) {
        int[][] states = new int[][] {
                new int[] {android.R.attr.state_checked},// unchecked
                new int[] {}   //normal
        };
        mTabTextColorStateList = new ColorStateList(states,colors);
        return this;
    }

    public JTabStyleDelegate setTextColor(int resId) {
        mTabTextColorStateList = ContextCompat.getColorStateList(mContext, resId);
        return this;
    }


    public int getScrollOffset() {
        return scrollOffset;
    }


    public JTabStyleDelegate setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        return this;
    }


    public int getIndicatorHeight() {
        return indicatorHeight;
    }


    public JTabStyleDelegate setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
        return this;
    }


    public int getUnderlineHeight() {
        return underlineHeight;
    }


    public JTabStyleDelegate setUnderlineHeight(int underlineHeight) {
        this.underlineHeight = underlineHeight;
        return this;
    }


    public int getDividerPadding() {
        return dividerPadding;
    }


    public JTabStyleDelegate setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;
        return this;
    }


    public int getTabPadding() {
        if (shouldExpand) {
            return tabPadding;
        }
        else {
            return tabPadding> PromptView.dp2px(13) ? tabPadding : (int) PromptView.dp2px(13);
        }
    }


    public JTabStyleDelegate setTabPadding(int tabPadding) {
        this.tabPadding = tabPadding;
        return this;
    }


    public int getDividerWidth() {
        return dividerWidth;
    }


    public JTabStyleDelegate setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        return this;
    }


    public int getTabTextSize() {
        return tabTextSize;
    }


    public JTabStyleDelegate setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        return this;
    }


    public int getTabTextColor() {
        return tabTextColor;
    }


    public JTabStyleDelegate setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
        return this;
    }


    public Typeface getTabTypeface() {
        return tabTypeface;
    }


    public JTabStyleDelegate setTabTypeface(Typeface tabTypeface) {
        this.tabTypeface = tabTypeface;
        return this;
    }


    public int getTabTypefaceStyle() {
        return tabTypefaceStyle;
    }


    public JTabStyleDelegate setTabTypefaceStyle(int tabTypefaceStyle) {
        this.tabTypefaceStyle = tabTypefaceStyle;
        return this;
    }

    /**
     * adapter有设置icon的时候 true不显示
     * @param notDrawIcon
     * @return
     */
    public JTabStyleDelegate setNotDrawIcon(boolean notDrawIcon) {
        mNotDrawIcon = notDrawIcon;
        return this;
    }


    public int getTabIconGravity() {
        return mTabIconGravity;
    }


    public JTabStyleDelegate setTabIconGravity(int tabIconGravity) {
        
        mTabIconGravity = tabIconGravity;
        return this;
    }


    public JTabStyleDelegate setJTabStyle(int tabStyle) {
        mTabStyle = tabStyle;
        mJTabStyle = JTabStyleBuilder.createJTabStyle(mTabStrip, mTabStyle);
        mTabStrip.setJTabStyle(mJTabStyle);
        return this;
    }


    public JTabStyleDelegate setJTabStyle(JTabStyle tabStyle) {
        mJTabStyle = tabStyle;
        mTabStrip.setJTabStyle(tabStyle);
        return this;
    }


    public int getFrameColor() {
        return mFrameColor;
    }


    public JTabStyleDelegate setFrameColor(int frameColor) {
        mFrameColor = frameColor;
        return this;
    }


    public JTabStyle getJTabStyle() {
        if (mJTabStyle == null) {
            mJTabStyle = JTabStyleBuilder.createJTabStyle(mTabStrip, mTabStyle);
        }
        return mJTabStyle;
    }


    public int getCornerRadio() {
        return mCornerRadio;
    }


    public JTabStyleDelegate setCornerRadio(int cornerRadio) {
        mCornerRadio = cornerRadio;
        return this;
    }


    public ISlidingTabStrip getTabStrip() {
        return mTabStrip;
    }


    public int getPromptBgColor() {
        return mPromptBgColor;
    }


    public void setPromptBgColor(int promptBgColor) {
        mPromptBgColor = promptBgColor;
    }


    public int getPromptNumColor() {
        return mPromptNumColor;
    }



    public void setPromptNumColor(int promptNumColor) {
        mPromptNumColor = promptNumColor;
    }


    public int getBackgroundColor() {
        return mBackgroundColor;
    }


    public JTabStyleDelegate setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return this;
    }


    public boolean isNeedTabTextColorScrollUpdate() {
        return mNeedTabTextColorScrollUpdate;
    }


    public JTabStyleDelegate setNeedTabTextColorScrollUpdate(boolean needTabTextColorScrollUpdate) {
        mNeedTabTextColorScrollUpdate = needTabTextColorScrollUpdate;
        return this;
    }
}
