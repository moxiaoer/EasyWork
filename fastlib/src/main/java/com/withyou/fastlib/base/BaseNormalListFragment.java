package com.withyou.fastlib.base;

import android.view.View;

import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;

/**
 * Created by dusan on 2018/3/21.
 */

public abstract class BaseNormalListFragment<T> extends BaseMVPListFragment<BasePresenter, BaseModel, T>{

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        setMvp(false);
    }
}
