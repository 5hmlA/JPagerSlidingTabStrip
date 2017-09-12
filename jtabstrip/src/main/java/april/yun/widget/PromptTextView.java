package april.yun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * @another 江祖赟
 * @date 2017/9/12 0012.
 */
public class PromptTextView extends android.support.v7.widget.AppCompatTextView {

    private SuperPrompt mPromptHelper;

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
            protected void refreshNotifyBg(){
                float textWidth = getTextWidth(getPaint(), getText().toString());
                float msgWidth = getTextWidth(mNumPaint, msg_str);
                //prompt背景和 prompt文字的offset
                float promptOffset = mNumHeight/2;
                mHalfMsgBgW = msgWidth/2f+promptOffset;
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
                    promptOffset = -promptOffset/3;
                }

                mPointCenterY = mHalfH-getTextHeight(getPaint(), getText().toString())*getLineCount()/2f-mNumHeight/2f;

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

                mMsgBg = new RectF(mPromptCenterPoint.x-mHalfMsgBgW, mPromptCenterPoint.y-mHalfMsgBgH,
                        mPromptCenterPoint.x+mHalfMsgBgW, mPromptCenterPoint.y+mHalfMsgBgH);

                if(mPromptOutOffset != null) {
                    mPromptCenterPoint.offset(-mPromptOutOffset[0], mPromptOutOffset[1]);
                    mMsgBg.offset(-mPromptOutOffset[0], mPromptOutOffset[1]);
                }
                //防止画到屏幕外  右上角
                if(mMsgBg.right>2*mHalfW || mMsgBg.top<0) {
                    //顺序不可变 因为mPromptCenterPoint依赖mMsgBg
                    float offsetX = 2*mHalfW-mMsgBg.right;
                    offsetX = offsetX<0 ? offsetX : 0;
                    float offsetY = mMsgBg.top<0 ? -mMsgBg.top : 0;
                    mPromptCenterPoint.offset(offsetX, offsetY);
                    mMsgBg.offset(offsetX, offsetY);
                }
                mPointCenterY = mPromptCenterPoint.y;
            }
        };
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

    public PromptTextView showNotify(){
        mPromptHelper.setPromptMsg(SuperPrompt.NOTIFY);
        return this;
    }
}
