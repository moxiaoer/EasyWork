package com.withyou.fastlib.base;

import android.os.Bundle;

import com.withyou.fastlib.R;
import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;

import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by dusan on 2018/3/15.
 */

public abstract class BaseSupportFragmentActivity<T extends BasePresenter, E extends BaseModel>
    extends BaseActivity<T, E>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != setFragment()) {
            loadRootFragment(R.id.fl_base_container, setFragment());
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_base;
    }

    /**
     * 设置整个架构的第一个fragment
     *
     * @return
     */
    public abstract SupportFragment setFragment();

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }
}
