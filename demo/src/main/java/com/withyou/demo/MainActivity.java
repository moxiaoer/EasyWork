package com.withyou.demo;

import android.os.Bundle;

import com.withyou.demo.fragment.NormalListDemoFragment;
import com.withyou.fastlib.base.BaseSupportFragmentActivity;

import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseSupportFragmentActivity {

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initLogic() {

    }

    @Override
    public SupportFragment setFragment() {
//        return MVPListDemoFragment.newInstance();
        return NormalListDemoFragment.newInstance();
    }

    @Override
    public boolean showToolBar() {
        return false;
    }
}
