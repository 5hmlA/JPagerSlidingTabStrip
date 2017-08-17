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
    private float top;


    public DefaultTabStyle(ISlidingTabStrip slidingTabStrip){
        super(slidingTabStrip);
    }


    @Override
    public void afterSetViewPager(LinearLayout tabsContainer){
        super.afterSetViewPager(tabsContainer);
        mOutRadio = mTabStyleDelegate.getCornerRadio();
    }


    //    @Override
    //    public void onSizeChanged(int w, int h, int oldw, int oldh){
    //        super.onSizeChanged(w, h, oldw, oldh);
    //        mH = h;
    //    }


    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition){
        updateTabTextScrollColor();
        if(mTabStyleDelegate.getBackgroundColor() != Color.TRANSPARENT) {
            //画背景
            mIndicatorPaint.setColor(mTabStyleDelegate.getBackgroundColor());
            drawRoundRect(canvas, padingVerticalOffect, padingVerticalOffect, mLastTab.getRight()-padingVerticalOffect,
                    this.mH-padingVerticalOffect, mOutRadio, mOutRadio, mIndicatorPaint);
        }
        if(mTabStyleDelegate.getFrameColor() != Color.TRANSPARENT) {
            //画边框
            mDividerPaint.setColor(mTabStyleDelegate.getFrameColor());
            mDividerPaint.setStrokeWidth(mTabStyleDelegate.getFrameWidth());
            drawRoundRect(canvas, padingVerticalOffect+mTabStyleDelegate.getFrameWidth()/2f+1,
                    padingVerticalOffect+mTabStyleDelegate.getFrameWidth()/2f+1,
                    mLastTab.getRight()-mTabStyleDelegate.getFrameWidth()/2f-1-padingVerticalOffect,
                    this.mH-padingVerticalOffect-mTabStyleDelegate.getFrameWidth()/2f-1, mOutRadio, mOutRadio,
                    mDividerPaint);
        }

        if(mTabStyleDelegate.getUnderlineColor() != Color.TRANSPARENT) {
            // draw underline
            mIndicatorPaint.setColor(mTabStyleDelegate.getUnderlineColor());
            canvas.drawRect(padingVerticalOffect, mH-mTabStyleDelegate.getUnderlineHeight(),
                    tabsContainer.getWidth()-padingVerticalOffect, mH-padingVerticalOffect, mIndicatorPaint);
        }

        if(mTabStyleDelegate.getDividerColor() != Color.TRANSPARENT) {
            // draw divider
            mDividerPaint.setColor(mTabStyleDelegate.getDividerColor());
            mDividerPaint.setColor(mTabStyleDelegate.getDividerWidth());
            for(int i = 0; i<tabsContainer.getChildCount()-1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), mTabStyleDelegate.getDividerPadding(), tab.getRight(),
                        mH-mTabStyleDelegate.getDividerPadding(), mDividerPaint);
            }
        }

        if(mTabStyleDelegate.getIndicatorColor() != Color.TRANSPARENT) {
            // draw indicator line
            mIndicatorPaint.setColor(mTabStyleDelegate.getIndicatorColor());
            calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);
            //draw indicator
            float left = 0;
            float top = 0;
            float right = 0;
            float bottom = 0;
            if(mTabStyleDelegate.getIndicatorHeight()>=mH/2) {
                //画在中间
                int halfIndHeight = mTabStyleDelegate.getIndicatorHeight()/2;
                float indPading = mH/2-halfIndHeight;

                left = mLinePosition.x+indPading;
                top = indPading;
                right = mLinePosition.y-indPading;
                bottom = mH-indPading;

                //                drawRoundRect(canvas, mLinePosition.x+indPading, indPading, mLinePosition.y-indPading, mH-indPading,
                //                        mOutRadio, mOutRadio, mIndicatorPaint);
            }else if(mTabStyleDelegate.getIndicatorHeight()>=0) {
                //画在底部
                left = mLinePosition.x+padingVerticalOffect;
                top = mH-mTabStyleDelegate.getIndicatorHeight()-padingVerticalOffect;
                right = mLinePosition.y-padingVerticalOffect;
                bottom = mH-padingVerticalOffect;
                //                drawRoundRect(canvas, left,
                //                        mH-mTabStyleDelegate.getIndicatorHeight()-padingVerticalOffect,
                //                        mLinePosition.y-padingVerticalOffect, mH-padingVerticalOffect, mOutRadio, mOutRadio,
                //                        mIndicatorPaint);
            }else {
                //IndicatorHeight<0 画在顶部
                //                drawRoundRect(canvas, left, padingVerticalOffect,
                //                        mLinePosition.y-padingVerticalOffect,
                //                        padingVerticalOffect-mTabStyleDelegate.getIndicatorHeight(), mOutRadio, mOutRadio,
                //                        mIndicatorPaint);
                left = mLinePosition.x+padingVerticalOffect;
                top = padingVerticalOffect;
                right = mLinePosition.y-padingVerticalOffect;
                bottom = padingVerticalOffect-mTabStyleDelegate.getIndicatorHeight();
            }
            int underLineFixWidth = mTabStyleDelegate.getUnderLineFixWidth();
            if(underLineFixWidth>0) {
                //                float tabWidth = mLinePosition.y-mLinePosition.x;
                float tabWidth = mCurrentTab.getWidth();
                underLineFixWidth = (int)Math.min(underLineFixWidth, tabWidth);
                left = mLinePosition.x+tabWidth/2-underLineFixWidth/2;
                right = mLinePosition.y-tabWidth/2+underLineFixWidth/2;
            }
            drawRoundRect(canvas, left, top, right, bottom, mOutRadio, mOutRadio, mIndicatorPaint);
        }
    }
}
