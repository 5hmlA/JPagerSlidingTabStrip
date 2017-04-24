package april.yun.tabstyle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import april.yun.ISlidingTabStrip;

/**
 * @author yun.
 * @date 2017/4/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class GradientTabStyle extends JTabStyle {

    private static final String TAG = GradientTabStyle.class.getSimpleName();
    private final Paint mPaint;
    private int mH;
    private float mOutRadio;
    private Paint rectPaint;
    private View mLastTab;
    private int mNormalColor;
    private int mSelectedColor;

    private View mCurrentTab;
    private View mNextTab;
    private Paint dividerPaint;


    public GradientTabStyle(ISlidingTabStrip slidingTabStrip) {
        super(slidingTabStrip);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override public void afterSetViewPager(LinearLayout tabsContainer) {
        super.afterSetViewPager(tabsContainer);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(mTabStyleDelegate.getFrameColor());
        mNormalColor = Color.TRANSPARENT;
        mSelectedColor = mTabStyleDelegate.getIndicatorColor();
        mPaint.setColor(mTabStyleDelegate.getIndicatorColor());
        mCurrentTab = tabsContainer.getChildAt(mTabStyleDelegate.getCurrentPosition());
        dividerPaint.setColor(mTabStyleDelegate.getDividerColor());
    }


    @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {

        mH = (int) (h - dp2dip(padingOffect));
        mTabCounts = mTabStrip.getTabsContainer().getChildCount();
        mLastTab = mTabStrip.getTabsContainer().getChildAt(mTabCounts - 1);
    }

    private float currentAlpha = 1;

    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        Log.d(TAG, "Current: " + mTabStyleDelegate.getCurrentPosition() + "----lastChecked: " +
                lastCheckedPosition + "---currentPositionOffset: " + currentPositionOffset);
        if (mTabStyleDelegate.getFrameColor() != Color.TRANSPARENT) {
            //画边框
            rectPaint.setStrokeWidth(dp2dip(1));
            canvas.drawRoundRect(dp2dip(padingOffect), dp2dip(padingOffect),
                    mLastTab.getRight() - dp2dip(padingOffect), this.mH, mOutRadio, mOutRadio, rectPaint);
        }

        if (mTabStrip.getState() == ViewPager.SCROLL_STATE_DRAGGING ||
                mTabStrip.getState() == ViewPager.SCROLL_STATE_IDLE) {
            if (lastCheckedPosition == mTabStyleDelegate.getCurrentPosition()) {
                mDragRight = true;
                mCurrentTab = tabsContainer.getChildAt(lastCheckedPosition);
                mNextTab = tabsContainer.getChildAt(lastCheckedPosition + 1);
                //Log.d(TAG, "indicator 往右滑动 ------>> ");
            }
            else {
                mDragRight = false;
                mCurrentTab = tabsContainer.getChildAt(lastCheckedPosition);
                mNextTab = tabsContainer.getChildAt(lastCheckedPosition - 1);
                //Log.d(TAG, "往左 <<------");
            }
        }
        if (mDragRight) {
            currentAlpha = 1 - currentPositionOffset;
        }
        else {
            currentAlpha = currentPositionOffset;
        }
        //当前  上一个
        mPaint.setAlpha((int) (currentAlpha * 255));
        canvas.drawRect(mCurrentTab.getLeft(), 0, mCurrentTab.getRight(), mH, mPaint);
        if (mNextTab != null) {
            mPaint.setAlpha((int) ((1-currentAlpha) * 255));
            canvas.drawRect(mNextTab.getLeft(), 0, mNextTab.getRight(), mH, mPaint);
        }
        if (mTabStyleDelegate.getDividerColor() != Color.TRANSPARENT) {
            // draw divider
            dividerPaint.setColor(mTabStyleDelegate.getDividerColor());
            for (int i = 0; i < tabsContainer.getChildCount() - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), mTabStyleDelegate.getDividerPadding(), tab.getRight(),
                        mH - mTabStyleDelegate.getDividerPadding(), dividerPaint);
            }
        }
    }
}
