package april.yun.tabstyle;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import april.yun.ISlidingTabStrip;
import april.yun.other.JTabStyleDelegate;

/**
 * @author yun.
 * @date 2017/4/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public abstract class JTabStyle {
    public static final long SHOWANI = 500;
    protected final JTabStyleDelegate mTabStyleDelegate;
    protected Paint mDividerPaint;
    protected Paint mIndicatorPaint;
    protected ISlidingTabStrip mTabStrip;
    protected boolean mDragRight;
    protected View mCurrentTab;
    protected View mNextTab;
    protected int mTabCounts;
    protected float padingOffect = 0.3f;
    /**
     * x:left  y:fight <br>
     * the left and right position of indicator
     */
    protected PointF mLinePosition = new PointF(0, 0);

    public int moveStyle = MOVESTYLE_STIKY;
    public static final int MOVESTYLE_DEFAULT = 0;
    public static final int MOVESTYLE_STIKY = 1;
    protected View mLastTab;
    protected float mW;
    protected float mH;


    public JTabStyle(ISlidingTabStrip slidingTabStrip) {
        mTabStyleDelegate = slidingTabStrip.getTabStyleDelegate();
        mTabStrip = slidingTabStrip;
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
    }


    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        float pading = dp2dip(0.5f);
        mW = w - pading;
        mH = h - pading;
        mTabCounts = mTabStrip.getTabsContainer().getChildCount();
        mLastTab = mTabStrip.getTabsContainer().getChildAt(mTabCounts - 1);
    }


    public abstract void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition);


    public float dp2dip(float dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }


    public boolean needChildView() {
        return true;
    }


    protected void calcuteIndicatorLinePosition(ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        // default: line below current tab
        mCurrentTab = tabsContainer.getChildAt(mTabStyleDelegate.getCurrentPosition());
        mLinePosition.x = mCurrentTab.getLeft();
        mLinePosition.y = mCurrentTab.getRight();
        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f &&
                mTabStyleDelegate.getCurrentPosition() < tabsContainer.getChildCount() - 1) {

            mNextTab = tabsContainer.getChildAt(mTabStyleDelegate.getCurrentPosition() + 1);
            final float nextTabLeft = mNextTab.getLeft();
            final float nextTabRight = mNextTab.getRight();
            if (moveStyle == MOVESTYLE_DEFAULT) {
                moveStyle_normal(currentPositionOffset, nextTabLeft, nextTabRight);
            }
            else {
                moveStyle_sticky(currentPositionOffset, lastCheckedPosition, nextTabLeft, nextTabRight);
            }
        }
    }


    protected void moveStyle_normal(float currentPositionOffset, float nextTabLeft, float nextTabRight) {
        mLinePosition.x = (currentPositionOffset * nextTabLeft +
                (1f - currentPositionOffset) * mLinePosition.x);
        mLinePosition.y = (currentPositionOffset * nextTabRight +
                (1f - currentPositionOffset) * mLinePosition.y);
    }


    protected void moveStyle_sticky(float currentPositionOffset, int lastCheckedPosition, float nextTabLeft, float nextTabRight) {
        if (mTabStrip.getState() == ViewPager.SCROLL_STATE_DRAGGING ||
                mTabStrip.getState() == ViewPager.SCROLL_STATE_IDLE) {
            if (lastCheckedPosition == mTabStyleDelegate.getCurrentPosition()) {
                mDragRight = true;
                //Log.d(TAG, "indicator 往右滑动 ------>> ");
            }
            else {
                mDragRight = false;
                //Log.d(TAG, "往左 <<------");
            }
        }
        if (mDragRight) {
            //                ------>>
            if (currentPositionOffset >= 0.5) {
                mLinePosition.x = (
                        2 * (nextTabLeft - mLinePosition.x) * currentPositionOffset + 2 * mLinePosition.x -
                                nextTabLeft);
            }
            mLinePosition.y = (currentPositionOffset * nextTabRight +
                    (1f - currentPositionOffset) * mLinePosition.y);
        }
        else {
            //                <<------
            mLinePosition.x = (currentPositionOffset * nextTabLeft +
                    (1f - currentPositionOffset) * mLinePosition.x);
            if (currentPositionOffset <= 0.5) {
                mLinePosition.y = (2 * (nextTabRight - mLinePosition.y) * currentPositionOffset +
                        mLinePosition.y);
            }
            else {
                mLinePosition.y = nextTabRight;
            }
        }
    }


    public void afterSetViewPager(LinearLayout tabsContainer) {

        mDividerPaint.setStrokeWidth(mTabStyleDelegate.getDividerWidth());
        mDividerPaint.setStrokeWidth(mTabStyleDelegate.getDividerColor());
        mIndicatorPaint.setColor(mTabStyleDelegate.getIndicatorColor());

        mDividerPaint.setStrokeWidth(mTabStyleDelegate.getDividerWidth());
        mDividerPaint.setColor(mTabStyleDelegate.getDividerColor());
        mIndicatorPaint.setColor(mTabStyleDelegate.getIndicatorColor());
    }
}

