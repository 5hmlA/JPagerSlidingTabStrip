package april.yun.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * @another 江祖赟
 * @date 2017/9/11.
 */
public class SuperPrompt implements ValueAnimator.AnimatorUpdateListener {
    protected static final String TAG = "SuperPrompt";
    public static final String NOTIFY = "n";
    public static final String ALOT = "~";
    public static final int SHOWTIME = 666;
    public static String MSGFORMART = "%d";
    protected Paint mBgPaint;
    protected Paint mNumPaint;
    protected int color_bg = Color.RED;
    protected int color_num = Color.WHITE;
    protected int num_size = 11;
    protected float mHalfW;
    protected float mNumHeight;
    protected String msg_str = "";
    /**
     * prompt矩阵 中点
     */
    protected PointF mPromptCenterPoint;
    protected RectF mMsgBg;
    //protected static final String ALOT = "...~~";
    protected String mLastMsg = "";
    protected ValueAnimator mShowAni;
    //是否要清楚消息
    protected boolean msgIs_dirty;
    public boolean mIsAniShow;
    protected boolean mForcePromptCircle = true;
    protected boolean mCenterVertical;
    protected float[] mPromptOutOffset;
    protected View mView;
    /**
     * 提示背景 的 中点Y
     */
    protected float mPointCenterY;
    protected float mHalfH;
    protected float mHalfMsgBgW;
    protected float mHalfMsgBgH;
    protected float mPromptRoundConor;
    /**
     * 文字与背景边框的距离
     */
    protected float mPromptOffset;

    public static final int MASK_HINT_COLOR = 0x99000000;
    /**
     * 变暗
     */
    public static final float[] SELECTED_DARK = new float[]
            {1, 0, 0, 0, -80,
                    0, 1, 0, 0, -80,
                    0, 0, 1, 0, -80,
                    0, 0, 0, 1, 0};
    /**
     * 变亮
     */

    public static final float[] SELECTED_BRIGHT = new float[]
            {1, 0, 0, 0, 80,
                    0, 1, 0, 0, 80,
                    0, 0, 1, 0, 80,
                    0, 0, 0, 1, 0};

    /**
     * 高对比度
     */

    public static final float[] SELECTED_HDR = new float[]
            {5, 0, 0, 0, -250,
                    0, 5, 0, 0, -250,
                    0, 0, 5, 0, -250,
                    0, 0, 0, 1, 0};

    /**
     * 高饱和度
     */
    public static final float[] SELECTED_HSAT = new float[]
            {(float) 3, (float) -2, (float) -0.2, 0, 50,
                    -1, 2, -0, 0, 50,
                    -1, -2, 4, 0, 50,
                    0, 0, 0, 1, 0};

    /**
     * 改变色调
     */
    public static final float[] SELECTED_DISCOLOR = new float[]
            {(float) -0.5, (float) -0.6, (float) -0.8, 0, 0,
                    (float) -0.4, (float) -0.6, (float) -0.1, 0, 0,
                    (float) -0.3, 2, (float) -0.4, 0, 0,
                    0, 0, 0, 1, 0};

    public ColorFilter mDimColorFilter = new ColorMatrixColorFilter(SELECTED_DARK);


    public static float dp2px(float px){
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
    }


    {
        mNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(color_num);
                setTextAlign(Paint.Align.CENTER);
            }
        };
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(color_bg);
            }
        };
        mShowAni = ValueAnimator.ofFloat(0, 1);
        mShowAni.setDuration(SHOWTIME);
        //mShowAni.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAni.addUpdateListener(this);
    }


    public SuperPrompt(View view){
        mView = view;
    }


    public static float getFontHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -fontMetrics.top-fontMetrics.bottom;
    }


    protected boolean haveCompoundDrawable(Drawable[] compoundDrawables){
        for(Drawable compoundDrawable : compoundDrawables) {
            if(compoundDrawable != null) {
                return true;
            }
        }
        return false;
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        mHalfW = w/2f;
        mHalfH = h/2f;
        mNumPaint.setTextSize(dp2px(num_size));
        mNumHeight = getFontHeight(mNumPaint);
        if(!TextUtils.isEmpty(msg_str)) {
            refreshNotifyBg();
            startShowAni();
        }
    }


    public SuperPrompt asNewMsgNums(){
        color_bg = Color.TRANSPARENT;
        color_num = Color.RED;
        mNumPaint.setColor(color_num);
        mBgPaint.setColor(color_bg);
        return this;
    }


    private float getHalfMsgBgW(){
        return calcutePosition(false, true);
    }


    private float getCenterPointX(boolean isX){
        return calcutePosition(isX, false);
    }


    private float calcutePosition(boolean isX, Boolean getHalfMsgBgW){
        float msgWidth = getTextWidth(mNumPaint, msg_str);
        //prompt背景和 prompt文字的offset
        float promptOffset = mNumHeight/2f;
        float centerY = mPromptRoundConor = mNumHeight;
        //prompt 背景的宽度
        float halfMsgBgW = msgWidth/2f+promptOffset;
        if(color_bg != Color.TRANSPARENT && !NOTIFY.equals(msg_str)) {
            halfMsgBgW = halfMsgBgW>mNumHeight ? halfMsgBgW : mNumHeight;
        }else {
            if(NOTIFY.equals(msg_str)) {
                centerY = halfMsgBgW = mNumHeight/2f;
            }else {
                halfMsgBgW = msgWidth/2f;
                centerY = mNumHeight/2f;
            }
        }
        if(getHalfMsgBgW) {
            return halfMsgBgW;
        }else if(isX) {
            return mHalfW*2-halfMsgBgW;
        }else {
            return centerY;
        }
    }


    /**
     * 更新 promptView的位置
     */
    protected void refreshNotifyBg(){
        float msgWidth = getTextWidth(mNumPaint, msg_str);
        //prompt背景和 prompt文字的offset
        mPromptOffset = mPromptOffset == 0 ? mNumHeight/2f : mPromptOffset;
        mHalfMsgBgW = msgWidth/2f+mPromptOffset;
        mHalfMsgBgH = mNumHeight;
        //if (mPromptRoundConor == 0) {
        //    mPromptRoundConor = mNumHeight;
        //}
        mPointCenterY = mNumHeight;
        if(color_bg != Color.TRANSPARENT && !NOTIFY.equals(msg_str)) {
            mHalfMsgBgW = mHalfMsgBgW>mNumHeight ? mHalfMsgBgW : mNumHeight;
            mPromptCenterPoint = new PointF(mHalfW*2-mHalfMsgBgW, mPointCenterY);
        }else {
            if(NOTIFY.equals(msg_str)) {
                mHalfMsgBgH = mPointCenterY = mHalfMsgBgW = mNumHeight/2f;
            }else {
                mHalfMsgBgW = msgWidth/2f;
                mHalfMsgBgH = mPointCenterY = mNumHeight/2f;
            }
            mPromptCenterPoint = new PointF(mHalfW*2-mHalfMsgBgW, mPointCenterY);
        }

        if(mForcePromptCircle) {
            mPromptRoundConor = mHalfMsgBgH = mHalfMsgBgW;
        }else if(mPromptRoundConor == 0) {
            mPromptRoundConor = mNumHeight;
        }

        mMsgBg = new RectF(mPromptCenterPoint.x-mHalfMsgBgW, mPromptCenterPoint.y-mHalfMsgBgH,
                mPromptCenterPoint.x+mHalfMsgBgW, mPromptCenterPoint.y+mHalfMsgBgH);

        //位置检查
        checkPromptPosition();
    }


    /**
     * 防止 prompt绘制到控件外
     */
    protected void checkPromptPosition(){
        //根据设置 移动prompt
        if(mPromptOutOffset != null) {
            mPromptCenterPoint.offset(-mPromptOutOffset[0], mPromptOutOffset[1]);
            mMsgBg.offset(-mPromptOutOffset[0], mPromptOutOffset[1]);
        }
        float offsetX = 0;
        float offsetY = 0;
        //左右
        if(mMsgBg.right>2*mHalfW) {
            offsetX = 2*mHalfW-mMsgBg.right;//右边 左移 -
        }else if(mMsgBg.left<0) {
            //在左边 右移 +
            offsetX = -mMsgBg.left;
        }
        //上下
        if(mMsgBg.top<0) {
            offsetY = -mMsgBg.top;//上面 下移 +
        }else if(mMsgBg.bottom>2*mHalfH) {
            //在下面 往上移 -
            offsetY = 2*mHalfH-mMsgBg.bottom;
        }
        if(offsetX != 0 || offsetY != 0) {
            //顺序不可变 因为mPromptCenterPoint依赖mMsgBg
            mPromptCenterPoint.offset(offsetX, offsetY);
            mMsgBg.offset(offsetX, offsetY);
        }
        //prompt移动的水平居中
        if(mCenterVertical) {
            float offset2CenterY = mHalfH-mMsgBg.centerY();
            mPromptCenterPoint.offset(0, offset2CenterY);
            mMsgBg.offset(0, offset2CenterY);
        }
        mPointCenterY = mPromptCenterPoint.y;
    }


    public void onDraw(Canvas canvas){
        if(!TextUtils.isEmpty(msg_str)) {
            if(msg_str.equals(NOTIFY)) {
                //画提示圆点即可
                canvas.drawCircle(mPromptCenterPoint.x, mPromptCenterPoint.y, mNumHeight/2, mBgPaint);
            }else {
                if(color_bg != Color.TRANSPARENT) {
                    canvas.drawRoundRect(mMsgBg, mPromptRoundConor, mPromptRoundConor, mBgPaint);
                }
                canvas.drawText(msg_str, mPromptCenterPoint.x, mPromptCenterPoint.y+mNumHeight/2, mNumPaint);
            }
        }
    }


    public static int computeMaxStringWidth(int currentMax, String[] strings, Paint p){
        float maxWidthF = 0.0f;
        int len = strings.length;
        for(int i = 0; i<len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int)( maxWidthF+0.5 );
        if(maxWidth<currentMax) {
            maxWidth = currentMax;
        }
        return maxWidth;
    }


    public static float getTextWidth(Paint paint, String str){
        //                Rect bounds = new Rect();
        //                paint.getTextBounds(str, 0, str.length(), bounds);
        //                return bounds.width();
        return paint.measureText(str);
    }


    /**
     * 获取单个文字的高度 比较准确
     */
    public static int getTextHeight(Paint paint, String str){
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.bottom-bounds.top;
    }


    /**
     * 获取单个字符文字的高度
     */
    public static float getTextHeight2(Paint paint, String str){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom-fontMetrics.ascent;
    }


    /**
     * 当num的值小于0 显示提示小圆点
     * 等于0 不现实任何
     */
    public SuperPrompt setPromptMsg(String msg){
        if(mLastMsg.equals(msg)) {
            Log.e(TAG, "set the same num width last time");
            return this;
        }else if(TextUtils.isEmpty(msg)) {
            msgIs_dirty = true;
            if(!mIsAniShow) {
                mLastMsg = msg_str = "";
                invalidatePrompt();
                return this;
            }
        }else {
            msgIs_dirty = false;
            msg_str = msg;
        }

        Log.d(TAG, "msg: "+msg_str);
        if(mHalfW>0) {
            refreshNotifyBg();
            startShowAni();
        }
        mLastMsg = msg;
        return this;
    }


    @SuppressLint("DefaultLocale")
    public String getMsgByNum(int num){
        if(num>99) {
            return ALOT;
        }else if(num == 0) {
            return "";
        }else if(num<0) {
            return NOTIFY;
        }else {
            return String.format(MSGFORMART, num);
        }
    }


    protected void startShowAni(){
        //为空表示不显示提示信息，清除提示信息会把msg_str置为空但是在动画结束之后
        if(!TextUtils.isEmpty(msg_str) && mIsAniShow) {
            if(msgIs_dirty) {//移除消息
                Log.d(TAG, "remove prompt msg");
                mLastMsg = "";
                //有消息到没消息
                mShowAni.cancel();
                mShowAni.setInterpolator(new DecelerateInterpolator());
                mShowAni.start();
            }else if(TextUtils.isEmpty(mLastMsg) || msg_str.equals(mLastMsg)) {//没消息到 显示消息
                Log.d(TAG, "ani show prompt msg");
                //没消息到有消息
                mLastMsg = NOTIFY;
                mShowAni.cancel();
                mShowAni.setInterpolator(new BounceInterpolator());
                mShowAni.start();
            }
        }
        invalidatePrompt();
    }


    public SuperPrompt setColor_bg(int color_bg){
        this.color_bg = color_bg;
        mBgPaint.setColor(color_bg);
        return this;
    }


    public SuperPrompt setColor_num(int color_num){
        this.color_num = color_num;
        mNumPaint.setColor(color_num);
        return this;
    }


    public SuperPrompt setNum_size(int num_size){
        this.num_size = num_size;
        return this;
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation){
        float ratio = (float)animation.getAnimatedValue();
        if(msgIs_dirty && ratio == 1) {
            Log.d(TAG, "clear msg aready");
            msg_str = "";//动画结束后情空消息
        }
        ratio = TextUtils.isEmpty(mLastMsg) ? 1-ratio : ratio;
        mPromptCenterPoint.y = mPointCenterY*( 3*ratio/2f-1/2f );
        mMsgBg.bottom = mHalfMsgBgH+mPromptCenterPoint.y;
        mMsgBg.bottom = mMsgBg.bottom<mMsgBg.top ? mMsgBg.top : mMsgBg.bottom;
        invalidatePrompt();
    }


    private void invalidatePrompt(){
        mView.invalidate();
    }


    public SuperPrompt setPromptOutOffset(@Size(value = 2) float[] promptOutOffset){
        mPromptOutOffset = promptOutOffset;
        return this;
    }

    public SuperPrompt setPromptOutOffset(float promptOutOffset){
        mPromptOutOffset = new float[]{promptOutOffset, promptOutOffset};
        return this;
    }


    public boolean isAniShow(){
        return mIsAniShow;
    }


    public void setAniShow(boolean aniShow){
        mIsAniShow = aniShow;
    }


    public boolean isForcePromptCircle(){
        return mForcePromptCircle;
    }


    public void forcePromptCircle(boolean forcePromptCircle){
        mForcePromptCircle = forcePromptCircle;
    }


    public boolean isCenterVertical(){
        return mCenterVertical;
    }


    public void centerVertical(boolean centerVertical){
        mCenterVertical = centerVertical;
    }


    public float[] getPromptOutOffset(){
        return mPromptOutOffset;
    }

    public void setDimMask(){
        mDimColorFilter = new PorterDuffColorFilter(MASK_HINT_COLOR, PorterDuff.Mode.DARKEN);
    }

    public void setDimMask(float[] filter){
        mDimColorFilter = new ColorMatrixColorFilter(filter);
    }

    public void setDimMask(@ColorInt int filter){
        mDimColorFilter = new PorterDuffColorFilter(filter, PorterDuff.Mode.DARKEN);
    }

    public void cancelAni(){
        mShowAni.cancel();
    }
}
