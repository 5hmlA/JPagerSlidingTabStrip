package april.yun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import april.yun.other.IPrompt;
import com.jonas.librarys.R;

/**
 * @another 江祖赟
 * @date 2017/9/12 0012.
 */
public class PromptImageView extends androidx.appcompat.widget.AppCompatImageView implements IPrompt {

    protected SuperPrompt mPromptHelper;

    @Keep
    public PromptImageView(Context context){
        this(context, null);
    }

    @Keep
    public PromptImageView(Context context, @Nullable AttributeSet attrs){
        this(context, attrs, 0);
    }

    @Keep
    public PromptImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mPromptHelper = new SuperPrompt(this);
        mPromptHelper.mIsAniShow = getContext().getResources().getBoolean(R.bool.jtabstrip_anishow);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mPromptHelper.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(isClickable()) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setColorFilter(mPromptHelper.mDimColorFilter);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    clearColorFilter();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    clearColorFilter();
                    invalidate();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        mPromptHelper.onDraw(canvas);
    }

    @Keep
    public SuperPrompt getPromptHelper(){
        return mPromptHelper;
    }

    public PromptImageView setPromptMsg(String promptMsg){
        mPromptHelper.setPromptMsg(promptMsg);
        return this;
    }

    @Keep
    public PromptImageView showNotify(){
        mPromptHelper.setPromptMsg(SuperPrompt.NOTIFY);
        return this;
    }


    @Keep
    public PromptImageView forcePromptCircle(){
        mPromptHelper.forcePromptCircle(true);
        return this;
    }


    /**
     * @param offset
     *         px
     * @return
     */
    @Keep
    public PromptImageView setPromptOffset(int offset){
        mPromptHelper.setPromptOutOffset(offset);
        return this;
    }

    @Keep
    public PromptImageView forceCenterVertical(){
        mPromptHelper.centerVertical(true);
        return this;
    }


    @Keep
    public PromptImageView configPrompt(int promptBgColor, int promptColor){
        mPromptHelper.setColor_bg(promptBgColor).setColor_num(promptBgColor);
        return this;
    }

    @Keep
    @Override
    public IPrompt asOnlyNum(){
        mPromptHelper.asNewMsgNums();
        return this;
    }

    @Keep
    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mPromptHelper.cancelAni();
    }

    @Keep
    @Override
    public void setImageDrawable(@Nullable Drawable drawable){
//        LayerDrawable
        super.setImageDrawable(drawable);
    }
}
