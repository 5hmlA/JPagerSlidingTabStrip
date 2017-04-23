package com.jonas.acase.CustomTabStyle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.view.ViewGroup;
import april.yun.ISlidingTabStrip;
import april.yun.tabstyle.JTabStyle;

/**
 * @author yun.
 * @date 2017/4/23
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class CustomTabStyle extends JTabStyle {
    private Path mTrianglePath = new Path();
    private int mTrigangleHeight = 10;

    public CustomTabStyle(ISlidingTabStrip slidingTabStrip) {
        super(slidingTabStrip);
    }

    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);
        if (mTabStyleDelegate.getIndicatorColor() != Color.TRANSPARENT) {
            // draw indicator line
            calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);
            //draw indicator
            calcuteTrianglePath();
            canvas.drawPath(mTrianglePath,mIndicatorPaint);
        }
    }


    private void calcuteTrianglePath() {
        float tabWidth = mLinePosition.y - mLinePosition.x;
        float vertex = mH-mTrigangleHeight;
        float tr_left = mLinePosition.x+tabWidth/2-mTrigangleHeight;
        float tr_right = mLinePosition.x+tabWidth/2+mTrigangleHeight;
        mTrianglePath.reset();
        mTrianglePath.moveTo(tr_left, mH);
        mTrianglePath.lineTo(mLinePosition.x + tabWidth / 2, vertex);
        mTrianglePath.lineTo(tr_right, mH);
        mTrianglePath.close();
    }
}
