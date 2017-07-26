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
            drawRoundRect(canvas, padingVerticalOffect+mTabStyleDelegate.getDividerWidth()/2,
                    padingVerticalOffect+mTabStyleDelegate.getDividerWidth()/2,
                    mLastTab.getRight()-mTabStyleDelegate.getDividerWidth()/2-padingVerticalOffect,
                    this.mH-padingVerticalOffect-mTabStyleDelegate.getDividerWidth()/2, mOutRadio, mOutRadio,
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
            if(mTabStyleDelegate.getIndicatorHeight()>=mH/2) {
                //画在中间
                int halfIndHeight = mTabStyleDelegate.getIndicatorHeight()/2;
                float indPading = mH/2-halfIndHeight;
                drawRoundRect(canvas, mLinePosition.x+indPading, indPading, mLinePosition.y-indPading, mH-indPading,
                        mOutRadio, mOutRadio, mIndicatorPaint);
            }else if(mTabStyleDelegate.getIndicatorHeight()>=0) {
                //画在底部
                drawRoundRect(canvas, mLinePosition.x+padingVerticalOffect,
                        mH-mTabStyleDelegate.getIndicatorHeight()-padingVerticalOffect,
                        mLinePosition.y-padingVerticalOffect, mH-padingVerticalOffect, mOutRadio, mOutRadio,
                        mIndicatorPaint);
            }else {
                //IndicatorHeight<0 画在顶部
                drawRoundRect(canvas, mLinePosition.x+padingVerticalOffect, padingVerticalOffect,
                        mLinePosition.y-padingVerticalOffect,
                        padingVerticalOffect-mTabStyleDelegate.getIndicatorHeight(), mOutRadio, mOutRadio,
                        mIndicatorPaint);
            }
        }
    }
}
