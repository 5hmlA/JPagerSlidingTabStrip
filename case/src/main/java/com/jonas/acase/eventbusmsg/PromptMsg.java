package com.jonas.acase.eventbusmsg;

/**
 * @author yun.
 * @date 2017/4/23
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 * <p><a href="https://github.com/ZuYun">github</a>
 */
public class PromptMsg {
    public int mPosition;
    public int mType;
    public static final int RANDOM = 1;
    public static final int CLEAR = 11;
    public static final int SHOW = 12;



    public PromptMsg(int position,int type) {
        mPosition = position;
        mType = type;
    }
}

