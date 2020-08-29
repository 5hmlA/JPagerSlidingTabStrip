package april.yun.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import april.yun.other.IPrompt;
import com.jonas.librarys.R;

/**
 * @another 江祖赟
 * @date 2017/9/12 0012.
 */
public class PromptTextView extends androidx.appcompat.widget.AppCompatCheckedTextView implements IPrompt {

    protected SuperPrompt mPromptHelper;
    protected boolean mPromptRight;
    private int mForceRightOffset = 80000;
    private int mForceLeftOffset = 80000;
    /**
     * 默认：以第一行 为准计算提示框位置（提示信息提示在第一行右上角）
     */
    private boolean mPromptForFirstLine = true;

    @Keep
    public PromptTextView(Context context){
        this(context, null);
    }

    @Keep
    public PromptTextView(Context context, @Nullable AttributeSet attrs){
        this(context, attrs, 0);
    }

    @Keep
    public PromptTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        mPromptHelper = new SuperPrompt(this) {
            @Override
            public void refreshNotifyBg(){
                if(mPromptRight) {
                    //提示信息固定 右上角 和 默认superPrompt一样
                    super.refreshNotifyBg();
                }else if(getLayout() != null) {
                    if(TextUtils.isEmpty(getText())) {
                        return;
                    }
                    String str = getText().toString();

                    //以第一行 为准计算提示框位置（提示信息提示在第一行右上角）
                    float textWidth = getLayout().getLineRight(0)-getLayout().getLineLeft(0);
                    if(!mPromptForFirstLine) {
                        //以最长的行 为准计算提示框位置
                        textWidth = getTextWidth(getPaint(), str);
                    }
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

                    //计算 提示框的中心Y
                    //                    if(getLineCount() == 1) {
                    //                        mPointCenterY = mHalfH-getTextHeight(getPaint(), getText().toString())*getLineCount()/2f-mNumHeight/2f;
                    //                    }else {
                    //                        mPointCenterY = mHalfH-getLayout().getHeight()/2f-mNumHeight/3f;
                    //                    }
                    mPointCenterY = mHalfH-( getLineHeight()*getLineCount()-getLayout().getLineDescent(0) )/2f-mNumHeight/2f;
                    if(color_bg != Color.TRANSPARENT && !NOTIFY.equals(msg_str)) {
                        //提示框背景不透明 同时不是 NOTIFY类型提示信息
                        mHalfMsgBgW = mHalfMsgBgW>mNumHeight ? mHalfMsgBgW : mNumHeight;
                        mPointCenterY += mHalfMsgBgH/3f;
                    }else {
                        if(NOTIFY.equals(msg_str)) {
                            //NOTIFY类型提示信息
                            mHalfMsgBgH = mHalfMsgBgW = mNumHeight/2f;
                            mPointCenterY += mHalfMsgBgH;
                        }else {
                            if(color_bg == Color.TRANSPARENT) {
                                //如果背景为透明的话 往下移动一点 因为好看
                                mPointCenterY += mNumHeight/2f;
                            }
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
                    if(mForceRightOffset != 80000) {
                        mForceRightOffset = Math.min(mForceRightOffset, getWidth()-getPaddingRight());
                        mForceRightOffset = Math.max(mForceRightOffset, -getPaddingRight());
                        mPromptCenterPoint.x = getWidth()-getPaddingRight()-mHalfMsgBgW-mForceRightOffset;
                    }
                    if(mForceLeftOffset != 80000) {
                        mForceRightOffset = Math.min(mForceLeftOffset, getWidth()-getPaddingLeft());
                        mForceRightOffset = Math.max(mForceLeftOffset, -getPaddingLeft());
                        mPromptCenterPoint.x = getPaddingLeft()+mHalfMsgBgW+mForceRightOffset;
                    }

                    mMsgBg = new RectF(mPromptCenterPoint.x-mHalfMsgBgW, mPromptCenterPoint.y-mHalfMsgBgH, mPromptCenterPoint.x+mHalfMsgBgW,
                            mPromptCenterPoint.y+mHalfMsgBgH);

                    //位置检查
                    checkPromptPosition();
                }
            }
        };
        mPromptHelper.mIsAniShow = getContext().getResources().getBoolean(R.bool.jtabstrip_anishow);
    }

    @Override
    @Keep
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

    @Keep
    public SuperPrompt getPromptHelper(){
        return mPromptHelper;
    }

    /**
     * 设置提示内容
     *
     * @param promptMsg
     * @return
     */
    @Keep
    public PromptTextView setPromptMsg(String promptMsg){
        mPromptHelper.setPromptMsg(promptMsg);
        return this;
    }

    /**
     * 设置提示 数字
     *
     * @param num
     * @return
     */
    @Keep
    public PromptTextView setPromptMsg(int num){
        mPromptHelper.setPromptMsg(mPromptHelper.getMsgByNum(num));
        return this;
    }

    /**
     * 显示 提示红点
     *
     * @return
     */
    @Keep
    public PromptTextView showNotify(){
        mPromptHelper.setPromptMsg(SuperPrompt.NOTIFY);
        return this;
    }

    @Override
    @Keep
    public IPrompt forcePromptCircle(){
        mPromptHelper.forcePromptCircle(true);
        return this;
    }

    /**
     * 调整 提示框为圆形 /圆角矩形 <br>
     * 默认圆形
     *
     * @return
     */
    @Keep
    public PromptTextView forcePromptCircle(boolean circle){
        mPromptHelper.forcePromptCircle(circle);
        return this;
    }

    /**
     * 调整 提示框的 offset
     *
     * @param offset
     *         px
     * @return
     */
    @Keep
    public PromptTextView setPromptOffset(int offset){
        mPromptHelper.setPromptOutOffset(offset);
        return this;
    }

    /**
     * 靠右边 +往左移动，0=最右边 包括padingRight
     *
     * @param offset
     * @return
     */
    @Keep
    public PromptTextView forceRightOffset(int offset){
        mForceRightOffset = offset;
        return this;
    }

    /**
     *靠右边  无视pading
     * @return
     */
    @Keep
    public PromptTextView forceRight(){
        mForceRightOffset = -Integer.MAX_VALUE;
        return this;
    }

    /**
     * 靠左边 +往右移动，0=最左边 包括padingLeft
     * @param offset
     * @return
     */
    @Keep
    public PromptTextView forceLeftOffset(int offset){
        mForceLeftOffset = offset;
        return this;
    }

    /**
     * 靠左边 无视pading
     * @return
     */
    @Keep
    public PromptTextView forceLeft(){
        mForceLeftOffset = -Integer.MAX_VALUE;
        return this;
    }

    /**
     * 强制 提示框 垂直居中
     *
     * @return
     */
    @Keep
    public PromptTextView forceCenterVertical(){
        mPromptHelper.centerVertical(true);
        return this;
    }

    /**
     * 设置背景透明，文字红色
     *
     * @return
     */
    @Keep
    public PromptTextView asOnlyNum(){
        mPromptHelper.asNewMsgNums();
        return this;
    }


    @Keep
    public PromptTextView configPrompt(int promptBgColor, int promptColor){
        mPromptHelper.setColor_bg(promptBgColor).setColor_num(promptColor);
        return this;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mPromptHelper.cancelAni();
    }

    @Keep
    public boolean isPromptRight(){
        return mPromptRight;
    }

    /**
     * 强制提示框处于右上角，其他对提示框位置相关的设置将无效{@link #forceCenterVertical()},{@link #forceRightOffset(int)}
     *
     * @param promptRight
     */
    @Keep
    public void fixedPromptRight(boolean promptRight){
        mPromptRight = promptRight;
    }

    @Keep
    public boolean isPromptForFirstLine(){
        return mPromptForFirstLine;
    }

    @Keep
    public void setPromptForFirstLine(boolean promptForFirstLine){
        mPromptForFirstLine = promptForFirstLine;
    }
}
