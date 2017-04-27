package april.yun.tabstyle;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import april.yun.ISlidingTabStrip;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yun.
 * @date 2017/4/21
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class DotsTabStyle extends JTabStyle implements ValueAnimator.AnimatorUpdateListener {
    private Paint bgPaint;
    private PointF mCurrentTab;
    private float dosRadio;
    private PointF mNextTab;
    //x:left  y:fight
    private PointF mLinePosition = new PointF(0, 0);

    private float mTabWidth;
    private List<PointF> fake_container = new ArrayList<>();
    private static final String TAG = DotsTabStyle.class.getSimpleName();
    private final ValueAnimator mShowAni;


    public DotsTabStyle(ISlidingTabStrip slidingTabStrip) {
        super(slidingTabStrip);
        mTabStyleDelegate.setShouldExpand(true);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);

        mShowAni = ValueAnimator.ofFloat(0, 1);
        mShowAni.setDuration(SHOWANI);
        mShowAni.setInterpolator(new OvershootInterpolator());
        mShowAni.addUpdateListener(this);
    }


    @Override public void afterSetViewPager(LinearLayout tabsContainer) {
        super.afterSetViewPager(tabsContainer);
        bgPaint.setColor(mTabStyleDelegate.getUnderlineColor());
        dosRadio = mTabStyleDelegate.getCornerRadio();
        dosRadio = dosRadio == 0 ? dp2dip(4) : dosRadio;
    }


    @Override public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fake_container = new ArrayList<>();
        showAnimation();
    }


    private void showAnimation() {
        mShowAni.cancel();
        mShowAni.start();
    }


    @Override
    public void onDraw(Canvas canvas, ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        // draw indicator line
        calcuteIndicatorLinePosition(tabsContainer, currentPositionOffset, lastCheckedPosition);
        //draw indicator
        drawRoundRect(canvas, mLinePosition.x + mTabWidth / 2 - dosRadio, mH / 2 - dosRadio,
                mLinePosition.y - mTabWidth / 2 + dosRadio, mH / 2 + dosRadio, dosRadio, dosRadio,
                mIndicatorPaint);
        //画默认圆
        for (int i = 0; i < fake_container.size(); i++) {
            canvas.drawCircle(mTabWidth / 2 + mTabWidth * i, mH / 2, dosRadio, bgPaint);
        }
    }


    protected void calcuteIndicatorLinePosition(ViewGroup tabsContainer, float currentPositionOffset, int lastCheckedPosition) {
        // default: line below current tab
        mCurrentTab = fake_container.get(mTabStyleDelegate.getCurrentPosition());
        mLinePosition.x = mCurrentTab.x;
        mLinePosition.y = mCurrentTab.y;
        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f &&
                mTabStyleDelegate.getCurrentPosition() < fake_container.size() - 1) {

            mNextTab = fake_container.get(mTabStyleDelegate.getCurrentPosition() + 1);
            float nextTabLeft = mNextTab.x;
            float nextTabRight = mNextTab.y;
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
                //Log.d(TAG, "往右 ------>> ");
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


    @Override public boolean needChildView() {
        return false;
    }


    @Override public void onAnimationUpdate(ValueAnimator animation) {
        float radio = (float) animation.getAnimatedValue();
        mTabWidth = radio * mW / mTabStrip.getTabCount();
        fake_container.clear();
        for (int i = 0; i < mTabStrip.getTabCount(); i++) {
            fake_container.add(new PointF(mTabWidth * i, mTabWidth * (i + 1)));
        }
        ((View) mTabStrip.getTabsContainer().getParent()).invalidate();
    }
}
