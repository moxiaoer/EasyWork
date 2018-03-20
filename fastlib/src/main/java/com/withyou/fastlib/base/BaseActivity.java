package com.withyou.fastlib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.withyou.fastlib.R;
import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;
import com.withyou.fastlib.util.AppManager;
import com.withyou.fastlib.util.TUtil;

import me.yokeyword.fragmentation.SupportActivity;

public abstract class BaseActivity<T extends BasePresenter, E extends BaseModel> extends SupportActivity {

    public Toolbar mToolbar;

    private boolean isMvp = false; //控制fragment是否使用mvp模式
    public T mPresenter;
    public E mModel;
    public View rootView;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.getAppManager().addActivity(this);

        //统一activity用Bundle传递数据
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        //设置view
        setContentView(getContentViewResId());

        //初始化沉浸式
        initImmersion();

        //如果设置了显示tool bar则初始化
        if (showToolBar()) {
            mToolbar = findViewById(R.id.toolbar);
            if (null != mToolbar) {
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        mContext = this;

        this.initView();
        this.initLogic();
        this.mvpCreate();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {

        if (layoutResID == 0) {
            throw new RuntimeException("layoutResID==-1 have u create your layout?");
        }

        if (showToolBar() && getToolBarLayoutId() != -1) {
            //如果需要显示自定义toolbar,并且资源id存在的情况下，实例化baseView;
            rootView = LayoutInflater.from(this).inflate(toolbarCover() ? R.layout.activity_base_toolbar_cover :
                    R.layout.activity_base, null, false);
            ViewStub toolbar = rootView.findViewById(R.id.vs_base_toolbar);
            FrameLayout container = rootView.findViewById(R.id.fl_base_container);

            toolbar.setLayoutResource(getToolBarLayoutId());
            View view = toolbar.inflate();

            Toolbar bar = view.findViewById(getToolBarId());
            setSupportActionBar(bar);

            LayoutInflater.from(this).inflate(getContentViewResId(), container, true);
            setContentView(rootView);

        }else {
            super.setContentView(layoutResID);
        }
    }

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

    /**
     * 获取contentView 资源id
     */
    public abstract int getContentViewResId();

    /**
     * 初始化沉浸式
     */
    public void initImmersion() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .init();
    }

    /**
     * 是否显示通用toolBar
     */
    public boolean showToolBar() {
        return false;
    }

    /**
     * 设置界面是否使用mvp模式（不用关注这个方法，这个方法是专门处理列表类界面的）
     *
     * @param mvp
     */
    public void setMvp(boolean mvp) {
        isMvp = mvp;
    }

    /**
     * mvp架构的初始化
     */
    public void mvpCreate() {
        if (isMvp) {
            mPresenter = TUtil.getT(this, 0);
            mModel = TUtil.getT(this, 1);
        }

        if (mPresenter != null) {
            mPresenter.mContext = mContext;
        }

        initPresenter();
    }

    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     */
    public abstract void initPresenter();

    /**
     * 初始化view
     */
    public abstract void initView();

    /**
     * 初始化逻辑
     */
    public abstract void initLogic();

    /**
     * 获取自定义toolbarview 资源id 默认为-1，showToolBar()方法必须返回true才有效
     * @return
     */
    public int getToolBarLayoutId() {
        return R.layout.layout_common_toolbar;
    }

    /**
     * 获取toolbar的资源id
     * @return
     */
    public int getToolBarId() {
        return R.id.toolbar;
    }

    /**
     * toolbar是否覆盖在内容区上方
     *
     * @return false 不覆盖  true 覆盖
     */
    protected boolean toolbarCover() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
