package april.yun.tabstyle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import april.yun.ISlidingTabStrip;

/**
 * @author yun.
 * @date 2017/4/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class DefaultTabStyle extends JTabStyle {

    private float mOutRadio = 0;


    public DefaultTabStyle(ISlidingTabStrip slidingTabStrip) {
        super(slidingTabStrip);
    }


    @Override public void afterSetViewPager(LinearLayout tabsContainer) {
        super.afterSetViewPager(tabsContainer);
        mOutRadio = mTabStyleDelegate.getCornerRadio();
    }


    @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float pading = dp2dip(padingOffect);
        mH = h - pading;
    }


    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        if (mTabStyleDelegate.getFrameColor() != Color.TRANSPARENT) {
            //画边框
            mDividerPaint.setColor(mTabStyleDelegate.getFrameColor());
            canvas.drawRoundRect(dp2dip(padingOffect), dp2dip(padingOffect),
                    mLastTab.getRight() - dp2dip(padingOffect), this.mH, mOutRadio, mOutRadio, mDividerPaint);
        }

        if (mTabStyleDelegate.getIndicatorColor() != Color.TRANSPARENT) {
            // draw indicator line
            mIndicatorPaint.setColor(mTabStyleDelegate.getIndicatorColor());
            calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);
            //draw indicator
            if (mTabStyleDelegate.getIndicatorHeight() >= mH / 2) {
                //画在中间
                int halfIndHeight = mTabStyleDelegate.getIndicatorHeight() / 2;
                float indPading = mH / 2 - halfIndHeight;

                canvas.drawRoundRect(mLinePosition.x + indPading, indPading, mLinePosition.y - indPading,
                        mH - indPading, mOutRadio, mOutRadio, mIndicatorPaint);
            }
            else {
                //画在底部
                //draw indicator
                canvas.drawRoundRect(mLinePosition.x, mH - mTabStyleDelegate.getIndicatorHeight(),
                        mLinePosition.y, mH, mOutRadio, mOutRadio, mIndicatorPaint);
            }
        }
        if (mTabStyleDelegate.getUnderlineColor() != Color.TRANSPARENT) {
            // draw underline
            mIndicatorPaint.setColor(mTabStyleDelegate.getUnderlineColor());
            canvas.drawRect(0, mH - mTabStyleDelegate.getUnderlineHeight(), tabsContainer.getWidth(), mH,
                    mIndicatorPaint);
        }
        if (mTabStyleDelegate.getDividerColor() != Color.TRANSPARENT) {
            // draw divider
            mDividerPaint.setColor(mTabStyleDelegate.getDividerColor());
            for (int i = 0; i < tabsContainer.getChildCount() - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), mTabStyleDelegate.getDividerPadding(), tab.getRight(),
                        mH - mTabStyleDelegate.getDividerPadding(), mDividerPaint);
            }
        }
    }
}
