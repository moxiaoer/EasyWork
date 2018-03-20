package com.withyou.fastlib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;


import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.withyou.fastlib.R;
import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;
import com.withyou.fastlib.entity.TabEntity;
import com.withyou.fastlib.util.LogU;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by dusan on 2018/3/15.
 */

public abstract class BaseHomeFragment<T extends BasePresenter, E extends BaseModel>
        extends BaseFragment<T, E>{

    private Bundle bundle;

    protected CommonTabLayout mTabLayout; //导航条
    protected ViewPager mViewPager;
    protected FrameLayout mFrameLayout;

    protected BaseFragment[] mFragments;//fragment集合
    private int[] mIconUnSelectIds = new int[]{};//未选中图标数组
    private int[] mIconSelectIds = new int[]{};//选中图标数组
    private String[] mTitles;//title文字部分
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();//图标信息对象

    private int initChooseTab; //初始化被选中的tab

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBundle(savedInstanceState);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void initView(View rootView) {

        mTabLayout = rootView.findViewById(R.id.base_tabLayout);
        mViewPager = rootView.findViewById(R.id.base_tabLayout_viewPager);
        mFrameLayout = rootView.findViewById(R.id.base_tabLayout_frameLayout);

        initTable();

        if (null == mFragments || mFragments.length == 0) {
            throw new RuntimeException("mFragments is null!");
        }

        initTabEntities();

        if (null == mTabLayout) {
            throw new RuntimeException("CommonTabLayout is null!");
        }
        if (null == mTitles || mTitles.length == 0) {
            mTabLayout.setTextsize(0);
        }

        if (null != mViewPager) {
            LogU.e("choose ViewPager");
            initViewPagerAdapter();
        } else {
            initFragments();
            LogU.e("choose frameLayout");
        }

        setTabSelect();

        if (null != mViewPager) {
            mViewPager.setCurrentItem(initChooseTab);
        }else {
            mTabLayout.setCurrentTab(initChooseTab);
        }
    }

    /**
     * 如果使用viewpager，初始化选中必须用该方法
     *
     * @param initChooseTab 选中position
     */
    public void setInitChooseTab(int initChooseTab) {
        this.initChooseTab = initChooseTab;
    }

    /**
     * 设置TabLayout属性，所有关于TabLayout属性在这里设置
     */
    protected abstract void initTable();

    /**
     * 初始化图标图片文字fragment数据
     */
    private void initTabEntities() {

        if (null == mFragments || mFragments.length == 0) {
            throw new RuntimeException("mFragments is null!");
        }

        if (null != mIconSelectIds & mFragments.length == mIconSelectIds.length
                && null != mIconUnSelectIds & mFragments.length == mIconUnSelectIds.length) {

            for (int i = 0; i < mFragments.length; i++) {
                mTabEntities.add(new TabEntity(mTitles == null ? "" : mTitles[i], mIconSelectIds[i],
                        mIconUnSelectIds[i]));
                mTabLayout.setTabData(mTabEntities);
            }

        }else {
            LogU.d("Fragments and the number of ICONS do not meet");
            for (int i = 0; i < mFragments.length; i++) {
                mTabEntities.add(new TabEntity(mTitles == null ? "" : mTitles[i], 0, 0));
            }
            mTabLayout.setIconVisible(false);
            mTabLayout.setTabData(mTabEntities);
        }
    }

    /**
     * 初始化viewpage的adapter
     */
    private void initViewPagerAdapter() {

        mViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(mFragments.length - 1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //viewpager滑动状态判断，1为滑动状态，2为滑动完成，0为停止
                if (state == 0) {
                    BaseHomeFragment.this.onTabSelect(mViewPager.getCurrentItem());
                }
            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles == null ? "" : mTitles[position];
        }
    }

    /**
     * tab选中的回调
     * @param position
     */
    protected abstract void onTabSelect(int position);

    /**
     * tab再次被选中的回调
     * @param position
     */
    protected abstract void onTabReselect(int position);

    private void initFragments() {
        //加载mFragments
        SupportFragment firstFragment = findChildFragment(mFragments[0].getClass());
        if (firstFragment == null) {
            loadMultipleRootFragment(R.id.base_tabLayout_frameLayout, initChooseTab, mFragments);
        }else {
            for (int i = 0; i < mFragments.length; i++) {
                LogU.e("initFragments: " + i);
                mFragments[i] = findFragment(mFragments[i].getClass());
            }
        }
    }

    /**
     * 为mTabLayout设置监听
     */
    private void setTabSelect() {

        LogU.e("setTabSelect");
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (null != mViewPager) {
                    mViewPager.setCurrentItem(position);
                }else {
                    int toDoHidden = -1;
                    for (int i = 0; i < mFragments.length; i++) {
                        if (!mFragments[i].isHidden()) {
                            toDoHidden = i;
                            LogU.e("查找显示中的fragment-------" + toDoHidden);
                        }
                    }
                    LogU.e("选中的fragment-------" + position);
                    LogU.e("确定显示中的fragment-------" + toDoHidden);
                    beforeOnclick(position, toDoHidden);
                }


                BaseHomeFragment.this.onTabSelect(position);
            }

            @Override
            public void onTabReselect(int position) {

                LogU.e("再次选中项" + position);
                BaseHomeFragment.this.onTabReselect(position);

            }
        });

    }

    /**
     *
     * @param position 点击的下标
     * @param toDoHidden 需要隐藏的fragment的下标
     */
    public void beforeOnclick(int position, int toDoHidden) {
        //这是方法是显示隐藏调用，如果你不不需要进行判断就不用处理这个方法，如果你的app切换需要状态判断
        //才能确定是否允许切换就需要在这个方法写文章了举个例子，下面的例子是判断登录正常调整，不登录跳转登录页
//        if ((position == 3 || position == 4)&&!isLogin){
//            LoginActivity.Start(_mActivity);
//            mTabLayout.setCurrentTab(toDoHidden);
//            return;
//        }else {
//            showHideFragment(mFragments[position], mFragments[toDoHidden]);
//        }
        showHideFragment(mFragments[position], mFragments[toDoHidden]);
    }

    public BaseFragment[] getmFragments() {
        return mFragments;
    }

    public void setmFragments(BaseFragment[] mFragments) {
        this.mFragments = mFragments;
    }

    public int[] getmIconUnSelectIds() {
        return mIconUnSelectIds;
    }

    public void setmIconUnSelectIds(int[] mIconUnSelectIds) {
        this.mIconUnSelectIds = mIconUnSelectIds;
    }

    public int[] getmIconSelectIds() {
        return mIconSelectIds;
    }

    public void setmIconSelectIds(int[] mIconSelectIds) {
        this.mIconSelectIds = mIconSelectIds;
    }

    public String[] getmTitles() {
        return mTitles;
    }

    public void setmTitles(String[] mTitles) {
        this.mTitles = mTitles;
    }
}
