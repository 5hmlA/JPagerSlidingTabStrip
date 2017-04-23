[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

[![License](https://img.shields.io/badge/JPagerSlidingTabStrip-V1.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[![License](https://img.shields.io/badge/JPagerSlidingTabStrip-download-yellowgreen.svg)](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/JPagerSlidingTabStrip.apk)

![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/promptmsg.gif)

![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/1.gif)

* JPagerSlidingTabStrip: 增强版的 [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip). 使用方式和PagerSlidingTabStrip一样简单，但功能和样式更丰富
* 内置3中tab风格 还可以 继承子自JTabStyle 实现自己的风格和动画 (JTabStyle)
* 增加 右上角的提示信息 (仿微信)



# 使用方法


  ##1. 添加依赖.
        
        dependencies {
            compile 'com.astuetz:pagerslidingtabstrip:1.0.1'
        }

  ##2. 通过JTabStyleDelegate设置相关属性(或者定义在布局里面)
    tabStyleDelegate来自于JPagerSlidingTabStrip
		tabStyleDelegate = JPagerSlidingTabStrip.getTabStyleDelegate();
		tabStyleDelegate.setJTabStyle(type)
                        .setShouldExpand(true)
                        .setFrameColor(Color.parseColor("#45C01A"))
                        .setTabTextSize(getDimen(R.dimen.tabstrip_textsize))
                        .setTextColorStateResource(getApplicationContext(), R.drawable.tabstripbg)
                        .setDividerColor(Color.parseColor("#45C01A"))
                        .setDividerPadding(0)
                        .setUnderlineColor(Color.parseColor("#3045C01A"))
                        .setUnderlineHeight(0)
                        .setIndicatorColor(Color.parseColor("#7045C01A"))
                        .setIndicatorHeight(getDimen(R.dimen.sug_event_tabheight));

- ###设置tab风格###
	- tabStyleDelegate.setJTabStyle(type) //provide 3 types:STYLE_DEFAULT,STYLE_ROUND,STYLE_DOTS
	- tabStyleDelegate.setJTabStyle(JTabStyle)  //define your own tabStyle

 - reference [CustomTabStyle](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/case/src/main/java/com/jonas/acase/CustomTabStyle/CustomTabStyle.java)
![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/custom.gif)
  - STYLE_DEFAULT
![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/default2.gif)
  - STYLE_ROUND
![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/round.gif)
  - STYLE_DOTS
![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/dots.gif)

  ##3. 为JPagerSlidingTabStrip绑定viewpager
     
		JPagerSlidingTabStrip.bindViewPager(viewpager);

  #### 如果需要使用 `OnPageChangeListener` 建议像下面这样添加监听即可

         JPagerSlidingTabStrip.setOnPageChangeListener(mPageChangeListener);
##4.通过JPagerSlidingTabStrip显示 提示信息
	
	//index:第几个tab  ,msgNum:(0~99 会显示数字,0 移除提示信息, 大于99 显示 ~)
	JPagerSlidingTabStrip.setPromptNum(index, msgNum);

# 相关熟悉

###*from [astuetz/PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)*

 * `pstsIndicatorColor` Color of the sliding indicator
 * `pstsUnderlineColor` Color of the full-width line on the bottom of the view
 * `pstsDividerColor` Color of the dividers between tabs
 * `pstsIndicatorHeight`Height of the sliding indicator
 * `pstsUnderlineHeight` Height of the full-width line on the bottom of the view
 * `pstsDividerPadding` Top and bottom padding of the dividers
 * `pstsTabPaddingLeftRight` Left and right padding of each tab
 * `pstsScrollOffset` Scroll offset of the selected tab
 * `pstsTabBackground` Background drawable of each tab, should be a StateListDrawable
 * `pstsShouldExpand` If set to true, each tab is given the same weight, default false
 * `pstsTextAllCaps` If true, all tab titles will be upper case, default true


##*为提示信息增加的属性*
 * `mPromptBgColor` 提示信息胶囊背景颜色
 * `mPromptNumColor` 提示信息文字颜色

#感谢

[PagerSlidingTabStrip](https://github.com/jpardogo/PagerSlidingTabStrip)


# License

    Copyright 2013 Andreas ZuYun

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
