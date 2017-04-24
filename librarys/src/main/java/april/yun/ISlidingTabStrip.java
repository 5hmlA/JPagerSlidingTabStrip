package april.yun;

import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import april.yun.other.JTabStyleDelegate;
import april.yun.tabstyle.JTabStyle;

/**
 * @author yun.
 * @date 2017/4/22
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public interface ISlidingTabStrip {

    ISlidingTabStrip setPromptNum(int index, int num);

    public interface IconTabProvider {
        /**
         * 如果 返回 null 則調用getPageIconResId
         *
         * @param position 1,简单的背景图片
         * 2，0为checked pressed背景  1为normal背景
         */
        public int[] getPageIconResIds(int position);

        /**
         * 兩個都實現的話 默認使用getPageIconResIds
         */
        @DrawableRes public int getPageIconResId(int position);
    }

    JTabStyleDelegate getTabStyleDelegate();

    void bindViewPager(ViewPager pager);

    ViewGroup getTabsContainer();

    int getTabCount();

    int getState();

    void setJTabStyle(JTabStyle JTabStyle);
}

