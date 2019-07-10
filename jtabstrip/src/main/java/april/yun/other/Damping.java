package april.yun.other;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.annotation.Keep;

/**
 * @author yun.
 * @date 2017/7/15
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public class Damping implements View.OnTouchListener {
    private static final long ANIDURATION = 250;
    private static final int NODIRECTION = -110;
    private HorizontalScrollView mView;
    float mScale = 1;
    private PointF mTdown = new PointF(0, 0);
    private float mDistance;
    private int direction = NODIRECTION;
    private ValueAnimator mRestoreAnimator;

    {
        mRestoreAnimator = ValueAnimator.ofFloat(1f, 1f);
        mRestoreAnimator.setDuration(250);
        mRestoreAnimator.setInterpolator(new OvershootInterpolator(1.6f));
//        mRestoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation){
//                mScale = (float)animation.getAnimatedValue();
//                mView.setScaleY(mScale);
//            }
//        });
    }

    private final Context mApplicationContext;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event){
        return dampOnTouch(event);
    }

    @Keep
    public static Damping wrapper(HorizontalScrollView view){
        view.setClickable(true);
        return new Damping(view);
    }

    private Damping(HorizontalScrollView view){
        mApplicationContext = view.getContext().getApplicationContext().getApplicationContext();
        mView = view;
        direction = LinearLayout.HORIZONTAL;
        mView.setOnTouchListener(this);
    }

    @Keep
    public Damping configDirection(int direction){
        this.direction = direction;
        return this;
    }

    public void animateRestore(){
        if(mScale != 1) {
            mRestoreAnimator.cancel();
            mRestoreAnimator.setFloatValues(mScale, 1f);
            mRestoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation){
                    float scale = (float)animation.getAnimatedValue();
                    if(direction == LinearLayout.VERTICAL) {
                        mView.setScaleY(scale);
                    }else {
                        mView.setScaleX(scale);
                    }
                }
            });
            mRestoreAnimator.start();
        }
    }

    /**
     * 处于顶部/左边 往下/右拉动
     */
    public void pull(float mScale){
        if(direction == LinearLayout.VERTICAL) {
            mView.setPivotY(mView.getPaddingTop());
            mView.setScaleY(mScale);
        }else {
            mView.setPivotX(mView.getPaddingLeft());
            mView.setScaleX(mScale);
        }
    }

    /**
     * 处于底部/右边 往上/左拉动
     */
    public void push(float mScale){
        if(direction == LinearLayout.VERTICAL) {
            mView.setPivotY(mView.getHeight());
            mView.setScaleY(mScale);
        }else {
            mView.setPivotX(mView.getRight());
            mView.setScaleX(mScale);
        }

    }

    /**
     * 根据滑动距离计算 横向缩放值
     *
     * @param distance
     * @return
     */
    public float calculateHorizontalDamping(float distance){
        float dragRadio = distance/( mApplicationContext.getResources().getDisplayMetrics().widthPixels );
        float dragPercent = Math.min(1f, dragRadio);
        float rate = 2f*dragPercent-(float)Math.pow(dragPercent, 2f);
        return 1+rate/6f;
    }

    /**
     * 根据滑动距离计算 竖向缩放值
     *
     * @param distance
     * @return
     */
    public float calculateVerticalDamping(float distance){
        float dragRadio = distance/( mApplicationContext.getResources().getDisplayMetrics().heightPixels );
        float dragPercent = Math.min(1f, dragRadio);
        float rate = 2f*dragPercent-(float)Math.pow(dragPercent, 2f);
        return 1+rate/6f;
    }

    public static boolean isScrollToLeft(View view){
        return !view.canScrollHorizontally(-1);
    }

    public static boolean isScrollToRight(View view){
        return !view.canScrollHorizontally(1);
    }

    public boolean isScrollToTop(){
        return !mView.canScrollVertically(-1);
    }

    public boolean isScrollToBottom(){
        return !mView.canScrollVertically(1);
    }

    public boolean dampOnTouch(MotionEvent event){
        if(mView != null && ( isScrollToTop() || isScrollToBottom() )) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    direction = LinearLayout.HORIZONTAL;
                    mTdown.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(mTdown.equals(0, 0)) {
                        direction = LinearLayout.HORIZONTAL;
                        mTdown.set(event.getX(), event.getY());
                    }
                    float c = event.getX();
                    float l = mTdown.x;
                    if(direction != NODIRECTION) {
                        calcuteMove(c, l);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mTdown.set(0, 0);
                    if(direction != NODIRECTION) {
                        animateRestore();
                    }
                    break;
            }
        }else {
            animateRestore();
        }
        return false;
    }

    /**
     * In RecyclerView, overScrollBy does not work. Call absorbGlows instead of
     * this method. If super.overScrollBy return true and isTouchEvent, means
     * current scroll is fling-overscroll, we use the deltaY to compute
     * velocityY.
     */
    public void overScrollBy(int deltaX){
        if(mTdown.equals(0, 0)) {
            //左边向右 --
            if(deltaX<0) {
                mView.setPivotX(mView.getPaddingLeft());
            }else {
                mView.setPivotX(mView.getRight());
            }
            mRestoreAnimator.setFloatValues(1, calculateHorizontalDamping(Math.abs(deltaX)), 1);
            mRestoreAnimator.start();
        }
    }

    private void calcuteMove(float y, float ly){
        calcureHorizontalMove(y, ly);
    }

    private void calcureHorizontalMove(float y, float ly){
        if(isScrollToLeft(mView) && !isScrollToRight(mView)) {
            // 在左边不在右边
            mDistance = y-ly;
            mScale = calculateHorizontalDamping(mDistance);
            pull(mScale);
        }else if(!isScrollToLeft(mView) && isScrollToRight(mView)) {
            // 在右边不在左边
            mDistance = ly-y;
            mScale = calculateHorizontalDamping(mDistance);
            push(mScale);
        }else if(isScrollToLeft(mView) && isScrollToRight(mView)) {
            // 在右边也在左边
            mDistance = y-ly;
            if(mDistance>0) {
                mScale = calculateHorizontalDamping(mDistance);
                pull(mScale);
            }else {
                mScale = calculateHorizontalDamping(-mDistance);
                push(mScale);
            }
        }
        //去掉 会没有底部反弹效果
        mDistance = y-ly;
    }

//    private void calcureVerticalMove(float y, float ly){
//        if(isScrollToTop() && !isScrollToBottom()) {
//            // 在顶部不在底部
//            mDistance = y-ly;
//            mScale = calculateVerticalDamping(mDistance);
//            pull(mScale);
//        }else if(!isScrollToTop() && isScrollToBottom()) {
//            // 在底部不在顶部
//            mDistance = ly-y;
//            mScale = calculateVerticalDamping(mDistance);
//            push(mScale);
//        }else if(isScrollToTop() && isScrollToBottom()) {
//            // 在底部也在顶部
//            mDistance = y-ly;
//            if(mDistance>0) {
//                mScale = calculateVerticalDamping(mDistance);
//                pull(mScale);
//            }else {
//                mScale = calculateVerticalDamping(-mDistance);
//                push(mScale);
//            }
//        }
//        //去掉 会没有底部反弹效果
//        mDistance = y-ly;
//        //顶部下拉 有filing 底部上拉有filing 两侧都会有回弹效果
//        //        mDistance = ly-y;
//    }
}

