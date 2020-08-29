package april.yun.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.jonas.librarys.R;

/**
 * @author 江祖赟.
 * @date 2017/6/7
 * <a href="http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1118/2006.html">android：ToolBar详解（手把手教程）</a>
 * ====================================================
 * android:id="@+id/toolbar"
 * android:layout_width="match_parent"
 * android:fitsSystemWindows="true" //适配状态栏
 * android:shadowColor="@color/j_white" //底部分割线颜色
 * android:shadowDy="5" //底部分割线宽度
 * android:shadowDx="5" //标题栏的左右边距 慎用
 * android:gravity="center" //标题居中 子标题无效
 * tools:layout_height="?actionBarSize"
 * android:layout_height="wrap_content"
 * ======================================
 */
public class JToolbar extends Toolbar {
    private static final String TAG = JToolbar.class.getSimpleName();
    private static final int[] ATTRS = new int[]{android.R.attr.gravity, android.R.attr.shadowColor, android.R.attr.shadowDy, android.R.attr.shadowDx};
    private int mSubtitleTextAppearance;
    private int mTitleTextAppearance;
    private boolean mIsTitleCenter = true;
    private CharSequence mTitleText;
    private PromptTextView mTitleTextView;//子类没有对其layout 和父类同名，可以偷梁换柱？？
    private PromptTextView mRightTextView;
    private int mTitleTextColor;
    private int mTitleOrignLeft;
    private PromptImageView mRightIconView;
    private int mCommonPading16 = dp2px(16);
    /**
     * 替换整个toolBar
     */
    private ViewGroup mYourTitleBarLayout;
    private int mDivideLineColor;
    private float mDivideLineHight = dp2px(0.5f);
    private Paint mDividePaint;
    private int mRight;
    private int mBottom;
    private PromptImageView mLeftIconView;
    private PromptImageView mTitleCenterIconView;
    private PromptTextView mCenterTitleTextView;


    {
        mDividePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Keep
    public JToolbar(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        setClickable(true);
        //        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.Toolbar, android.support.v7.appcompat.R.attr.toolbarStyle, 0);
        wrapperAttrs(context, attrs);
    }

    @Keep
    public JToolbar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setClickable(true);
        wrapperAttrs(context, attrs);
    }


    private void wrapperAttrs(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, R.attr.toolbarStyle, 0);
        mTitleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        mSubtitleTextAppearance = a.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
        a.recycle();
        TypedArray sa1 = context.obtainStyledAttributes(attrs, ATTRS);
        mIsTitleCenter = ( a.getInt(0, Gravity.START)&Gravity.CENTER ) == Gravity.CENTER;
        mDivideLineColor = sa1.getColor(1, Color.TRANSPARENT);
        mDivideLineHight = dp2px(sa1.getFloat(2, 0.5f));
        mCommonPading16 = dp2px(sa1.getFloat(3, 16));
        sa1.recycle();
        mDividePaint.setColor(mDivideLineColor);
        mDividePaint.setStrokeWidth(mDivideLineHight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(shouldLayout(mRightTextView)) {
            //mRightTextView的宽度不超过 toolbard的一/3
            measureChild(mRightTextView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(shouldLayout(mRightIconView)) {
            measureChild(mRightIconView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(shouldLayout(mLeftIconView)) {
            measureChild(mLeftIconView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(shouldLayout(mTitleCenterIconView)) {
            measureChild(mTitleCenterIconView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()/3, MeasureSpec.AT_MOST),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(shouldLayout(mYourTitleBarLayout)) {
            measureChild(mYourTitleBarLayout, MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(shouldLayout(mCenterTitleTextView)) {
            measureChild(mCenterTitleTextView, MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                         MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }


    private boolean shouldLayout(View view){
        return view != null && view.getParent() == this && view.getVisibility() != GONE;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b){
        mRight = r;
        mBottom = b;
        if(!shouldLayout(mYourTitleBarLayout)) {
            super.onLayout(changed, l, t, r, b);
            //layout RightTextView
            int width = 0;
            if(shouldLayout(mLeftIconView)) {
                width = mLeftIconView.getMeasuredWidth();
                mLeftIconView.layout(l, getPaddingTop(), l+width, b);
            }
            if(shouldLayout(mTitleCenterIconView)) {
                width = mTitleCenterIconView.getMeasuredWidth();
                int centerLeft = ( r-width )/2;
                mTitleCenterIconView.layout(centerLeft, getPaddingTop(), centerLeft+width, b);
            }
            width = 0;
            if(shouldLayout(mRightTextView)) {
                width = mRightTextView.getMeasuredWidth();
                mRightTextView.layout(mRight-width, getPaddingTop(), mRight, b);
            }
            if(shouldLayout(mRightIconView)) {
                width = mRightIconView.getMeasuredWidth();
                mRightIconView.layout(mRight-width, getPaddingTop(), mRight, b);
            }
            if(shouldLayout(mCenterTitleTextView)) {
                mCenterTitleTextView.layout(0, getPaddingTop(), mRight, b);
            }

            if(mTitleOrignLeft == 0 && shouldLayout(mTitleTextView)) {
                mTitleOrignLeft = mTitleTextView.getLeft();
                int righMargin = Math.max(mTitleOrignLeft, width);
                if(shouldLayout(mLeftIconView)) {
                    //去掉了系统的mNavButtonView
                    mTitleOrignLeft = 0;
                    righMargin = Math.max(mLeftIconView.getMeasuredWidth(), width);
                }
                ( (Toolbar.LayoutParams)mTitleTextView.getLayoutParams() ).rightMargin = righMargin;
                ( (LayoutParams)mTitleTextView.getLayoutParams() ).leftMargin = Math.max(0, righMargin-mTitleOrignLeft);
            }
        }else {
            mYourTitleBarLayout.layout(l, getPaddingTop(), r, b);
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(mDivideLineColor != 0) {
            float dividelineY = mBottom-mDivideLineHight/2;
            canvas.drawLine(getPaddingLeft(), dividelineY, mRight-getPaddingRight(), dividelineY, mDividePaint);
        }
    }


    @Keep
    public static int getStatusBarHeight(){
        Resources system = Resources.getSystem();
        int resourceId = system.getIdentifier("status_bar_height", "dimen", "android");
        return system.getDimensionPixelSize(resourceId);
    }


    @Keep
    public static int dp2px(float dipValue){
        float fontScale = Resources.getSystem().getDisplayMetrics().density;
        return (int)( dipValue*fontScale+0.5f );
    }

    @Keep
    public TextView setTitle2(CharSequence title){
        setTitle(title);
        return mTitleTextView;
    }

    @Keep
    @Override
    public void setTitle(CharSequence title){
        if(mIsTitleCenter) {
            if(!TextUtils.isEmpty(title)) {
                if(mTitleTextView == null) {
                    final Context context = getContext();
                    mTitleTextView = new PromptTextView(context);
                    mTitleTextView.setSingleLine();
                    mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if(mTitleTextAppearance != 0) {
                        mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                    }
                    mTitleTextView.setGravity(Gravity.CENTER);
                    if(mTitleTextColor != 0) {
                        mTitleTextView.setTextColor(mTitleTextColor);
                    }
                }
                if(mTitleTextView.getParent() != this) {
                    addView(mTitleTextView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
            }else if(mTitleTextView != null && mTitleTextView.getParent() == this) {
                removeView(mTitleTextView);
            }
            if(mTitleTextView != null) {
                mTitleTextView.setText(title);
            }
            mTitleText = title;
        }else {
            super.setTitle(title);
        }
    }


    @Keep
    public PromptImageView setTitleIcon(@DrawableRes int iconId){
        return setTitleIcon(iconId, 0);
    }


    /**
     * @param width
     *         单位 dp
     */
    @Keep
    public PromptImageView setTitleIcon(@DrawableRes int iconId, int width){
        if(iconId != 0) {
            //去掉navigation
            setNavigationIcon(null);
            if(mTitleCenterIconView == null) {
                final Context context = getContext();
                mTitleCenterIconView = new PromptImageView(context);
            }
            if(mTitleCenterIconView.getParent() != this) {
                addToolView(mTitleCenterIconView, dp2px(width));
            }
        }else if(mTitleCenterIconView != null && mTitleCenterIconView.getParent() == this) {
            removeView(mTitleCenterIconView);
        }
        if(mTitleCenterIconView != null) {
            mTitleCenterIconView.setImageResource(iconId);
        }
        return mTitleCenterIconView;
    }

    @Keep
    public PromptTextView setCenterTitle(CharSequence title){
        if(!TextUtils.isEmpty(title)) {
            setTitle2(null);
            if(mCenterTitleTextView == null) {
                final Context context = getContext();
                mCenterTitleTextView = new PromptTextView(context);
                mCenterTitleTextView.setSingleLine();
                mCenterTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if(mTitleTextAppearance != 0) {
                    mCenterTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                }
                mCenterTitleTextView.setGravity(Gravity.CENTER);
                if(mTitleTextColor != 0) {
                    mCenterTitleTextView.setTextColor(mTitleTextColor);
                }
            }
            if(mCenterTitleTextView.getParent() != this) {
                addView(mCenterTitleTextView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }else if(mCenterTitleTextView != null && mCenterTitleTextView.getParent() == this) {
            removeView(mCenterTitleTextView);
        }
        if(mCenterTitleTextView != null) {
            mCenterTitleTextView.setText(title);
        }
        mTitleText = title;
        return mCenterTitleTextView;
    }


    @Keep
    public PromptTextView setRightTitle(CharSequence title){
        if(!TextUtils.isEmpty(title)) {
            if(mRightTextView == null) {
                final Context context = getContext();
                mRightTextView = new PromptTextView(context);
                mRightTextView.setSingleLine();
                mRightTextView.setEllipsize(TextUtils.TruncateAt.END);
                mRightTextView.setPadding(1, 0, mCommonPading16, 0);
                if(mSubtitleTextAppearance != 0) {
                    mRightTextView.setTextAppearance(context, mSubtitleTextAppearance);
                }
                mRightTextView.setGravity(Gravity.CENTER);
            }
            if(mRightTextView.getParent() != this) {
                addToolView(mRightTextView, 0);
                //addView(mRightTextView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            }
        }else if(mRightTextView != null && mRightTextView.getParent() == this) {
            removeView(mRightTextView);
        }
        if(mRightTextView != null) {
            mRightTextView.setText(title);
            mTitleOrignLeft = 0;
        }
        return mRightTextView;
    }


    @Keep
    public PromptImageView setRightIcon(@DrawableRes int iconId){
        return setRightIcon(iconId, 0);
    }


    @Keep
    public PromptImageView setLeftIcon(@DrawableRes int iconId){
        return setLeftIcon(iconId, 0);
    }


    @Keep
    @Override
    public void setNavigationIcon(int resId){
        if(!shouldLayout(mLeftIconView)) {
            super.setNavigationIcon(resId);
        }else {
            Log.w(TAG, "已经设置左边的图片。。该项设置无效。。");
        }
    }


    /**
     * @param width
     *         单位 dp
     */
    @Keep
    public PromptImageView setLeftIcon(@DrawableRes int iconId, int width){
        if(iconId != 0) {
            //去掉navigation
            setNavigationIcon(null);
            if(mLeftIconView == null) {
                final Context context = getContext();
                mLeftIconView = new PromptImageView(context);
                mLeftIconView.setPadding(mCommonPading16, 0, 0, 0);
            }
            if(mLeftIconView.getParent() != this) {
                addToolView(mLeftIconView, dp2px(width));
            }
        }else if(mLeftIconView != null && mLeftIconView.getParent() == this) {
            removeView(mLeftIconView);
        }
        if(mLeftIconView != null) {
            mLeftIconView.setImageResource(iconId);
        }
        return mLeftIconView;
    }

    @Keep
    @Override
    public void setNavigationOnClickListener(OnClickListener listener){
        if(shouldLayout(mLeftIconView)) {
            mLeftIconView.setOnClickListener(listener);
        }else {
            super.setNavigationOnClickListener(listener);
        }
    }


    /**
     * @param width
     *         单位 dp
     */
    @Keep
    public PromptImageView setRightIcon(@DrawableRes int iconId, int width){
        if(iconId != 0) {
            if(mRightIconView == null) {
                final Context context = getContext();
                mRightIconView = new PromptImageView(context);
                mRightIconView.setPadding(0, 0, mCommonPading16, 0);
            }
            if(mRightIconView.getParent() != this) {
                addToolView(mRightIconView, dp2px(width));
            }
        }else if(mRightIconView != null && mRightIconView.getParent() == this) {
            removeView(mRightIconView);
        }
        if(mRightIconView != null) {
            mRightIconView.setImageResource(iconId);
            mTitleOrignLeft = 0;
        }
        return mRightIconView;
    }


    private void addToolView(View v, int width){
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if(vlp == null || !checkLayoutParams(vlp)) {
            if(width>0) {
                lp = new LayoutParams(width+v.getPaddingRight()+v.getPaddingLeft(), LayoutParams.MATCH_PARENT);
                //                lp = new LayoutParams(height+v.getPaddingStart()+v.getPaddingEnd(), LayoutParams.MATCH_PARENT);
            }else {
                lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            }
        }else {
            lp = (LayoutParams)vlp;
        }
        addView(v, lp);
    }


    @Keep
    @Override
    public void setTitleTextColor(@ColorInt int color){
        if(mIsTitleCenter) {
            mTitleTextColor = color;
            if(mTitleTextView != null) {
                mTitleTextView.setTextColor(color);
            }
        }else {
            super.setTitleTextColor(color);
        }
    }


    @Keep
    @Override
    public CharSequence getTitle(){
        if(mIsTitleCenter) {
            return mTitleText;
        }else {
            return super.getTitle();
        }
    }


    @Keep
    @Override
    public void setSubtitle(CharSequence subtitle){
        if(!mIsTitleCenter) {
            super.setSubtitle(subtitle);
        }else {
        }
    }


    @Keep
    public <VG extends ViewGroup> VG yourTitleBarLayout(VG yourTitleBarLayout){
        if(yourTitleBarLayout != null) {
            removeAllViews();
            mYourTitleBarLayout = yourTitleBarLayout;
            mYourTitleBarLayout.setClickable(true);
            if(mYourTitleBarLayout.getParent() != this) {
                addView(mYourTitleBarLayout, new ViewGroup.LayoutParams(-1, -1));
            }
        }else if(mYourTitleBarLayout != null) {
            removeView(mYourTitleBarLayout);
            mYourTitleBarLayout = null;
        }
        return yourTitleBarLayout;
    }


    @Keep
    public JToolbar setDivideLineColor(@ColorInt int divideLineColor){
        mDivideLineColor = divideLineColor;
        mDividePaint.setColor(divideLineColor);
        return this;
    }


    @Keep
    public JToolbar setDivideLineHight(int divideLineHight){
        mDivideLineHight = divideLineHight;
        mDividePaint.setStrokeWidth(divideLineHight);
        return this;
    }
}
