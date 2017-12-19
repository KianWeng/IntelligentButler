package com.kian.intelligentbutler.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by YYTD on 2017/12/19.
 */

public class MyAdapter extends PagerAdapter{
    List<View> viewLists;

    public MyAdapter(List<View> lists)
    {
        viewLists = lists;
    }

    @Override
    public int getCount() {                                                                 //获得size
        // TODO Auto-generated method stub
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) //销毁Item
    {
        view.removeView(viewLists.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)  //实例化Item
    {
        view.addView(viewLists.get(position), 0);
        return viewLists.get(position);
    }
}