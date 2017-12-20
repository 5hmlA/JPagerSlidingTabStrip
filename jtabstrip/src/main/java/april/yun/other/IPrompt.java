package april.yun.other;

import april.yun.widget.SuperPrompt;

/**
 * @another 江祖赟
 * @date 2017/9/25 0025.
 */
public interface IPrompt {

    public SuperPrompt getPromptHelper();

    public IPrompt setPromptMsg(String promptMsg);

    public IPrompt showNotify();

    public IPrompt forcePromptCircle();

    public IPrompt setPromptOffset(int offset);

    public IPrompt forceCenterVertical();

    public IPrompt configPrompt(int promptBgColor, int promptColor);

    public IPrompt asOnlyNum();

}
