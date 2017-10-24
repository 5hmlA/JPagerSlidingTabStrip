package april.yun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import com.jonas.librarys.R;

import april.yun.other.IPrompt;

/**
 * @another 江祖赟
 * @date 2017/9/12 0012.
 */
public class PromptTextView extends android.support.v7.widget.AppCompatCheckedTextView implements IPrompt {

    protected SuperPrompt mPromptHelper;
    protected boolean mPromptRight;

    public PromptTextView(Context context){
        this(context, null);
    }

    public PromptTextView(Context context, @Nullable AttributeSet attrs){
        this(context, attrs, 0);
    }

    public PromptTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        mPromptHelper = new SuperPrompt(this) {
            @Override
            public void refreshNotifyBg(){
                if(mPromptRight) {
                    //提示信息固定 右上角 和 默认superPrompt一样
                    super.refreshNotifyBg();
                }else {
                    if(TextUtils.isEmpty(getText())) {
                        return;
                    }
                    float textWidth = getTextWidth(getPaint(), getText().toString());
                    float msgWidth = getTextWidth(mNumPaint, msg_str);
                    //prompt背景和 prompt文字的offset
                    mPromptOffset = mPromptOffset == 0 ? mNumHeight/2f : mPromptOffset;
                    mHalfMsgBgW = msgWidth/2f+mPromptOffset;
                    mHalfMsgBgH = mNumHeight;
                    mHalfMsgBgW = mHalfMsgBgW>mNumHeight ? mHalfMsgBgW : mNumHeight;

                    //                if(!TextUtils.isEmpty(getText())) {
                    //                    //textWidth的宽度不小于3个字的宽度
                    //                    textWidth = getText().length()<3 ? textWidth/getText().length()*3 : textWidth;
                    //                }else {
                    //                    textWidth = (int)( mHalfW*2 );
                    //                }

                    //compoundDrawables size allways 4
                    Drawable[] compoundDrawables = getCompoundDrawables();
                    if(!haveCompoundDrawable(compoundDrawables)) {
                        mPromptOffset = -mPromptOffset/3;
                    }

                    mPointCenterY = mHalfH-getTextHeight(getPaint(),
                            getText().toString())*getLineCount()/2f-mNumHeight/2f;

                    if(color_bg != Color.TRANSPARENT && !NOTIFY.equals(msg_str)) {
                        mHalfMsgBgW = mHalfMsgBgW>mNumHeight ? mHalfMsgBgW : mNumHeight;
                        mPointCenterY += mHalfMsgBgH/3f;
                    }else {
                        if(NOTIFY.equals(msg_str)) {
                            mHalfMsgBgH = mHalfMsgBgW = mNumHeight/2f;
                            mPointCenterY += mHalfMsgBgH;
                        }else {
                            mHalfMsgBgW = msgWidth/2f;
                            mHalfMsgBgH = mNumHeight/2f;
                        }
                    }
                    mPromptCenterPoint = new PointF(mHalfW+textWidth/2+mHalfMsgBgW/2f, mPointCenterY);
                    if(( getGravity()&Gravity.LEFT ) == Gravity.LEFT || ( getGravity()&Gravity.START ) == Gravity.START) {
                        int padleft = getPaddingLeft();
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            padleft = Math.max(getPaddingLeft(), getPaddingStart());
                        }
                        mPromptCenterPoint.x = padleft+textWidth+mHalfMsgBgW/2f;
                        //                    mPromptCenterPoint.y = mPointCenterY;
                    }else if(( getGravity()&Gravity.END ) == Gravity.END || ( getGravity()&Gravity.RIGHT ) == Gravity.RIGHT) {
                        int paddingRight = getPaddingRight();
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            paddingRight = Math.max(getPaddingRight(), getPaddingEnd());
                        }
                        mPromptCenterPoint.x = mHalfW*2-( paddingRight+textWidth+mHalfMsgBgW/2f );
                        //                    mPromptCenterPoint.y = mPointCenterY;
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
            }
        };
        mPromptHelper.mIsAniShow = getContext().getResources().getBoolean(R.bool.jtabstrip_anishow);
    }

    @Override
    public void setText(CharSequence text, BufferType type){
        super.setText(text, type);
        if(mPromptHelper != null) {
            mPromptHelper.refreshNotifyBg();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mPromptHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mPromptHelper.onDraw(canvas);
    }

    public SuperPrompt getPromptHelper(){
        return mPromptHelper;
    }

    public PromptTextView setPromptMsg(String promptMsg){
        mPromptHelper.setPromptMsg(promptMsg);
        return this;
    }

    public PromptTextView setPromptMsg(int num){
        mPromptHelper.setPromptMsg(mPromptHelper.getMsgByNum(num));
        return this;
    }

    public PromptTextView showNotify(){
        mPromptHelper.setPromptMsg(SuperPrompt.NOTIFY);
        return this;
    }


    public PromptTextView forcePromptCircle(){
        mPromptHelper.forcePromptCircle(true);
        return this;
    }

    /**
     * @param offset
     *         px
     * @return
     */
    public PromptTextView setPromptOffset(int offset){
        mPromptHelper.setPromptOutOffset(offset);
        return this;
    }

    public PromptTextView centerVertical(){
        mPromptHelper.centerVertical(true);
        return this;
    }

    public PromptTextView asOnlyNum(){
        mPromptHelper.asNewMsgNums();
        return this;
    }


    public PromptTextView configPrompt(int promptBgColor, int promptColor){
        mPromptHelper.setColor_bg(promptBgColor).setColor_num(promptColor);
        return this;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mPromptHelper.cancelAni();
    }

    public boolean isPromptRight(){
        return mPromptRight;
    }

    public void fixedPromptRight(boolean promptRight){
        mPromptRight = promptRight;
    }
}
