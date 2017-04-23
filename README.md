[![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

[![License](https://img.shields.io/badge/JPagerSlidingTabStrip-V1.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

[![License](https://img.shields.io/badge/JPagerSlidingTabStrip-download-yellowgreen.svg)](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/JPagerSlidingTabStrip.apk)

![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/promptmsg.gif)

![](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/gifs/1.gif)

#[中文版](https://github.com/ZuYun/JPagerSlidingTabStrip/blob/master/README_CN.md)

* JPagerSlidingTabStrip: deeply modified from [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip).
* Add 3 tabStyle and provides an entry for custom tabStyle (JTabStyle)
* Added the display of unread messages (like weichat)



# Usage


  ##1. Add dependence.
        
     	 	
		compile 'april.yun.JPagerSlidingTabStrip:library:1.0.0'
	 
	
  ##2. Set up the attribute width JTabStyleDelegate( or xml)
     
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

- ###set up tabStyle###
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

  ##3. BindViewpager for JPagerSlidingTabStrip
     
		JPagerSlidingTabStrip.bindViewPager(viewpager);

  #### *(Optional)* If you need an `OnPageChangeListener` with your view pager

         JPagerSlidingTabStrip.setOnPageChangeListener(mPageChangeListener);
##4.Show prompt message for tab with JPagerSlidingTabStrip
	
	//index:the index of tab ,msgNum:(0~99 show number,0 dismiss message,>99 show ~)
	JPagerSlidingTabStrip.setPromptNum(index, msgNum);

# Customization 

###*from [astuetz/PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)*
To not just look like another Play Store styled app, go and adjust these values to match
your brand:

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

*All attributes have their respective getters and setters to change them at runtime*

##*Add for prompt message*
 * `mPromptBgColor` the background color of prompt msg
 * `mPromptNumColor` the number color of prompt msg


#Thanks

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
