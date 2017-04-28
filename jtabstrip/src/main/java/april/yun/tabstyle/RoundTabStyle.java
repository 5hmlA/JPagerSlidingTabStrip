package april.yun.tabstyle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import april.yun.ISlidingTabStrip;

/**
 * @author yun.
 * @date 2017/4/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class RoundTabStyle extends JTabStyle {

    private float mOutRadio;
    private float mH;
    private Path mClipath;

    private static final String TAG = RoundTabStyle.class.getSimpleName();


    public RoundTabStyle(ISlidingTabStrip slidingTabStrip) {
        super(slidingTabStrip);
    }


    @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float pading = dp2dip(padingOffect);
        mH = h - pading;
        if (mLastTab != null) {
            getClipPath(pading, mLastTab.getRight());
            mOutRadio = mTabStyleDelegate.getCornerRadio() == 0 ? mH / 2 : mTabStyleDelegate.getCornerRadio();
        }
    }


    private void getClipPath(float pading, float width) {
        RectF clip = new RectF(pading, pading, width - pading, mH);
        mClipath = new Path();
        mClipath.addRoundRect(clip, mH / 2f, mH / 2f, Path.Direction.CCW);
    }


    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {

        updateTabTextScrollColor();

        if (mTabStyleDelegate.getFrameColor() != Color.TRANSPARENT) {
            //画边框
            mDividerPaint.setColor(mTabStyleDelegate.getFrameColor());
            drawRoundRect(canvas, dp2dip(padingOffect), dp2dip(padingOffect),
                    mLastTab.getRight() - dp2dip(padingOffect), this.mH, mOutRadio, mOutRadio, mDividerPaint);
        }
        if (mTabStyleDelegate.getIndicatorColor() != Color.TRANSPARENT) {
            canvas.save();
            canvas.clipPath(mClipath);
            // draw indicator line
            mIndicatorPaint.setColor(mTabStyleDelegate.getIndicatorColor());

            calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);

            //draw indicator
            canvas.drawRect(mLinePosition.x, 0, mLinePosition.y, mH, mIndicatorPaint);
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
