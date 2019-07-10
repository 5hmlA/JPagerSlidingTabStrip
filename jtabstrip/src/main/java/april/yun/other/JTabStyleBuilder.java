package april.yun.other;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import april.yun.ISlidingTabStrip;
import april.yun.tabstyle.DefaultTabStyle;
import april.yun.tabstyle.DotsTabStyle;
import april.yun.tabstyle.JTabStyle;
import april.yun.tabstyle.RoundTabStyle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
public class JTabStyleBuilder {
    public static final int STYLE_DEFAULT = 0;
    public static final int STYLE_ROUND = 1;
    public static final int STYLE_DOTS = 2;
    public static final int STYLE_GRADIENT = -1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ STYLE_DEFAULT, STYLE_ROUND, STYLE_DOTS }) public @interface TabStyle {}


    public static JTabStyle createJTabStyle(ISlidingTabStrip slidingTabStrip, @TabStyle int tabStyle) {
        if (tabStyle == STYLE_DEFAULT) {
            return new DefaultTabStyle(slidingTabStrip);
        }
        else if (tabStyle == STYLE_ROUND) {
            return new RoundTabStyle(slidingTabStrip);
        }
        else if (tabStyle == STYLE_DOTS) {
            return new DotsTabStyle(slidingTabStrip);
        }
        return new DefaultTabStyle(slidingTabStrip);
    }
}