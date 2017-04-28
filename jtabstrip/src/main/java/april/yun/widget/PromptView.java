package april.yun.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.security.SecureRandom;

/**
 * @author yun.
 * @date 2017/4/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class PromptView extends android.support.v7.widget.AppCompatCheckedTextView
        implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = PromptView.class.getSimpleName();
    private static final int SHOWTIME = 666;
    private static final int CLEARPROMPT = 0;
    private static final int DOTSNOTIFY = -1991;
    private Paint mBgPaint;
    private Paint mNumPaint;
    private int color_bg = Color.RED;
    private int color_num = Color.WHITE;
    private int num_size = 12;
    private float mHalfW;
    private float mNumHeight;
    private String msg_str = "";
    private PointF mPromptCenterPoint;
    private RectF mMsgBg;
    private static final String NOTIFY = "n";
    private static final String ALOT = "~";
    //private static final String ALOT = "...~~";
    private String mLastMsg = "";
    private ValueAnimator mShowAni;
    //是否要清楚消息
    private boolean msgIs_dirty;
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private ColorStateList mTextColorsList;
    private int[] mTextScrollColors;
    private int mColorForChecked;
    private int mColorForNormal;
    private boolean mChecked2 = true;


    public static float dp2px(float px) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
    }


    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -fontMetrics.top - fontMetrics.bottom;
    }


    public PromptView(Context context) {
        this(context, null);
    }


    public PromptView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public PromptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
        setGravity(Gravity.CENTER);
        setIncludeFontPadding(false);//去除顶部和底部额外空白
        //setSingleLine();//must not be singleLine
        mNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumPaint.setTextAlign(Paint.Align.CENTER);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShowAni = ValueAnimator.ofFloat(0, 1);
        mShowAni.setDuration(SHOWTIME);
        //mShowAni.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAni.addUpdateListener(this);
        setTag(DOTSNOTIFY);//存储上一次显示的内容
    }


    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfW = w / 2f;
        mNumPaint.setTextSize(dp2px(num_size));
        mNumHeight = getFontHeight(mNumPaint);
        refreshNotifyBg();
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (haveCompoundDrawable(compoundDrawables)) {
            setPadding(getPaddingLeft(), (int) (mNumHeight / 2), getPaddingRight(), (int) (mNumHeight / 2));
        }
        else {
            setPadding(getPaddingLeft(), (int) (mNumHeight), getPaddingRight(), (int) (mNumHeight));
        }
        //TypedValue outValue = new TypedValue();
        //getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        //cardView.setBackgroundResource(outValue.resourceId);
        //setClickable(true);
        //setForeground(ContextCompat.getDrawable(getContext(),android.R.attr.selectableItemBackground));
        mBgPaint.setColor(color_bg);
        mNumPaint.setColor(color_num);
        startShowAni();
        SecureRandom secureRandom = new SecureRandom();
        float alphaStart = secureRandom.nextInt(60) / 100f;
        if (secureRandom.nextInt(100) > 50) {
            ObjectAnimator.ofFloat(this, "alpha", alphaStart, 1)
                          .setDuration(SHOWTIME).start();
        }
        mTextColorsList = getTextColors();
        mColorForChecked = mTextColorsList.getColorForState(new int[] { android.R.attr.state_checked }, 0);
        mColorForNormal = mTextColorsList.getColorForState(new int[] {}, 0);
        mTextScrollColors = new int[] { mColorForChecked, mColorForNormal };
        mLinearGradient = new LinearGradient(0, 0, mHalfW * 2, 0, mTextScrollColors,
                new float[] { 0f, 0.001f }, Shader.TileMode.CLAMP);
        mMatrix = new Matrix();
        mLinearGradient.setLocalMatrix(mMatrix);
    }


    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //before onSizechange
    }


    private boolean haveCompoundDrawable(Drawable[] compoundDrawables) {
        for (Drawable compoundDrawable : compoundDrawables) {
            if (compoundDrawable != null) {
                return true;
            }
        }
        return false;
    }


    private void refreshNotifyBg() {
        int textWidth = getTextWidth(getPaint(), getText().toString());
        int msgWidth = getTextWidth(mNumPaint, msg_str);
        float promptOffset = mNumHeight / 2;
        float halfMsgBgW = msgWidth / 2f + promptOffset;
        halfMsgBgW = halfMsgBgW > mNumHeight ? halfMsgBgW : mNumHeight;

        if (!TextUtils.isEmpty(getText())) {
            //textWidth的宽度不小于3个字的宽度
            textWidth = getText().length() < 3 ? textWidth / getText().length() * 3 : textWidth;
        }
        else {
            textWidth = (int) (mHalfW * 2);
        }
        //compoundDrawables size allways 4
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (!haveCompoundDrawable(compoundDrawables)) {
            promptOffset = -promptOffset / 3;
        }
        mPromptCenterPoint = new PointF(mHalfW + textWidth / 2 - promptOffset, mNumHeight);
        mMsgBg = new RectF(mPromptCenterPoint.x - halfMsgBgW, 0, mPromptCenterPoint.x + halfMsgBgW,
                mPromptCenterPoint.y + mNumHeight);
        //防止画到屏幕外
        if (mMsgBg.right > 2 * mHalfW) {
            //顺序不可变 因为mPromptCenterPoint依赖mMsgBg
            mPromptCenterPoint.offset(2 * mHalfW - mMsgBg.right, 0);
            mMsgBg.offset(2 * mHalfW - mMsgBg.right, 0);
        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(msg_str)) {
            if (msg_str.equals(NOTIFY)) {
                //画提示圆点即可
                canvas.drawCircle(mPromptCenterPoint.x, mPromptCenterPoint.y, mNumHeight / 2, mBgPaint);
            }
            else {
                canvas.drawRoundRect(mMsgBg, mNumHeight, mNumHeight, mBgPaint);
                canvas.drawText(msg_str, mPromptCenterPoint.x, mPromptCenterPoint.y + mNumHeight / 2,
                        mNumPaint);
            }
        }
    }


    public static int getTextWidth(Paint paint, String str) {
        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        //int iRet = 0;
        //if (str != null && str.length() > 0) {
        //    int len = str.length();
        //    float[] widths = new float[len];
        //    paint.getTextWidths(str, widths);
        //    for (int j = 0; j < len; j++) {
        //        iRet += (int) Math.ceil(widths[j]);
        //    }
        //}
        //return iRet;
        return bounds.width();
    }


    /**
     * 当num的值小于0 显示提示小圆点
     * 等于0 不现实任何
     */
    public PromptView setPromptNum(int num) {
        if (num == ((int) getTag())) {
            Log.e(TAG, "set the same num width last time");
            return this;
        }
        setTag(num);
        msgIs_dirty = false;
        if (num > 99) {
            msg_str = ALOT;
        }
        else if (num == 0) {
            //清除消息
            msgIs_dirty = !msgIs_dirty;
        }
        else if (num < 0) {
            msg_str = NOTIFY;
        }
        else {
            msg_str = String.format("%d", num);
        }
        Log.d(TAG, "num: " + num);
        if (mHalfW > 0) {
            refreshNotifyBg();
            startShowAni();
        }
        return this;
    }


    private void startShowAni() {
        //为空表示不显示提示信息，清除提示信息会把msg_str置为空但是在动画结束之后
        if (!TextUtils.isEmpty(msg_str)) {
            if (msgIs_dirty) {//移除消息
                Log.d(TAG, "remove prompt msg");
                mLastMsg = "";
                //有消息到没消息
                mShowAni.cancel();
                mShowAni.setInterpolator(new DecelerateInterpolator());
                mShowAni.start();
            }
            else if (TextUtils.isEmpty(mLastMsg)) {//没消息到 显示消息
                Log.d(TAG, "ani show prompt msg");
                //没消息到有消息
                mLastMsg = NOTIFY;
                mShowAni.cancel();
                mShowAni.setInterpolator(new BounceInterpolator());
                mShowAni.start();
            }
            invalidate();
        }
    }


    public void setColor_bg(int color_bg) {
        this.color_bg = color_bg;
    }


    public void setColor_num(int color_num) {
        this.color_num = color_num;
    }


    public void setNum_size(int num_size) {
        this.num_size = num_size;
    }


    @Override public void onAnimationUpdate(ValueAnimator animation) {
        float ratio = (float) animation.getAnimatedValue();
        if (msgIs_dirty && ratio == 1) {
            Log.d(TAG, "clear msg aready");
            msg_str = "";//动画结束后情空消息
        }
        ratio = TextUtils.isEmpty(mLastMsg) ? 1 - ratio : ratio;
        mPromptCenterPoint.y = mNumHeight * (3 * ratio / 2f - 1 / 2f);
        mMsgBg.bottom = mNumHeight * 2 * ratio;
        invalidate();
    }


    /**
     * 进度为0--1之间
     */
    public PromptView setScrollOffset(float offset) {
        if (offset > 0.1 && offset < 0.9) {
            //mMatrix.postTranslate(offset*mHalfW*2, 0);
            mMatrix.setTranslate(offset * mHalfW * 2, 0);
            mLinearGradient.setLocalMatrix(mMatrix);
            getPaint().setShader(mLinearGradient);
        }
        else {
            getPaint().setShader(null);
        }
        postInvalidate();
        return this;
    }


    public PromptView setScroll2Checked(boolean checked2) {
        if (mChecked2 != checked2) {
            mChecked2 = checked2;
            if (checked2) {
                mTextScrollColors = new int[] { mColorForChecked, mColorForNormal };
                mLinearGradient = new LinearGradient(0, 0, mHalfW * 2, 0, mTextScrollColors,
                        new float[] { 0f, 0.001f }, Shader.TileMode.CLAMP);
            }
            else {
                mTextScrollColors = new int[] { mColorForNormal, mColorForChecked };
                mLinearGradient = new LinearGradient(0, 0, mHalfW * 2, 0, mTextScrollColors,
                        new float[] { 0f, 0.001f }, Shader.TileMode.CLAMP);
            }
        }
        return this;
    }


    @Override public void setChecked(boolean checked) {
        super.setChecked(checked);
    }
}
