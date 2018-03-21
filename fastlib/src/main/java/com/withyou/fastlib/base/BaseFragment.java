package com.withyou.fastlib.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.withyou.fastlib.R;
import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;
import com.withyou.fastlib.util.TUtil;

import me.yokeyword.fragmentation.SupportFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by dusan on 2018/3/15.
 */

public abstract class BaseFragment<T extends BasePresenter, E extends BaseModel> extends SupportFragment{

    protected View rootView;

    private boolean isMvp = false; //控制fragment是否使用mvp模式

    public T mPresenter;
    public E mModel;
    protected View emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != getArguments()) {
            //如果传递的数据不为空，则执行该方法接收相关数据
            getBundleExtras(getArguments());
        }

        if (rootView == null) {
            //为空时初始化
            if (showToolBar() && getToolBarResId() != 0) {
                //如果需要显示自定义toolbar,并且资源id存在的情况下，实例化baseView;
                rootView = inflater.inflate(toolbarCover() ? R.layout.activity_base_toolbar_cover:
                    R.layout.activity_base, container, false);

                ViewStub viewStub = rootView.findViewById(R.id.vs_base_toolbar);
                FrameLayout flContainer = rootView.findViewById(R.id.fl_base_container);

                viewStub.setLayoutResource(getToolBarResId());
                View view = viewStub.inflate();
                Toolbar toolbar = view.findViewById(R.id.toolbar);

                inflater.inflate(getLayoutRes(), flContainer, true);

                initToolBar(toolbar);

            }else {
                rootView = inflater.inflate(getLayoutRes(), container, false);
            }
        }

        initView(rootView);
        mvpCreate();
        return rootView;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initLogic();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (immersionEnabled()) {
            initImmersion();
        }
        hideInput();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
    }

    /**
     * 初始化toolbar可重写覆盖自定的toolbar,base中实现的是通用的toolbar
     */
    public abstract void initToolBar(Toolbar toolbar);

    /**
     * 获取Bundle传递的信息
     * @param bundle
     */
    protected abstract void getBundleExtras(Bundle bundle);

    /**
     * 是否显示通用ToolBar，默认不显示
     * @return
     */
    public boolean showToolBar() {
        return false;
    }

    /**
     * 获取自定义toolbarview 资源id 默认为0，showToolBar()方法必须返回true才有效
     */
    public int getToolBarResId() {
        return R.layout.layout_common_toolbar;
    }

    /**
     * 获取布局的资源文件
     * @return
     */
    protected abstract int getLayoutRes();

    /**
     * toolbar是否覆盖在内容区上方
     *
     * @return false 不覆盖  true 覆盖
     */
    protected boolean toolbarCover() {
        return false;
    }

    /**
     * 初始化view
     * @param rootView
     */
    protected abstract void initView(View rootView);

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
            mPresenter.mContext = this.getActivity();
        }

        initPresenter();
    }

    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     */
    public void initPresenter() {
    }

    /**
     * 逻辑内容初始化，懒加载模式
     */
    protected abstract void initLogic();

    /**
     * 当前页面Fragment支持沉浸式初始化。默认返回false，可设置支持沉浸式初始化
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean immersionEnabled() {
        return false;
    }

    /**
     * 状态栏初始化（immersionEnabled默认为false时不走该方法）
     */
    protected void initImmersion() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f) //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.trans)
                .init();
    }

    /**
     * 关闭软键盘
     */
    public void hideInput() {
        if (getActivity().getCurrentFocus() == null) return;
        ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 获取只有图片和文字描述的view
     * @param str
     * @param drawRes
     * @return
     */
    public View getEmptyView(String str, @DrawableRes int drawRes) {
        if (emptyView != null) {
            return emptyView;
        }
        emptyView = LayoutInflater.from(_mActivity).inflate(R.layout.layout_empty_view, null, false);
        if (!TextUtils.isEmpty(str)) {
            TextView textView = emptyView.findViewById(R.id.tv_text);
            ImageView imageView = emptyView.findViewById(R.id.iv_empty);
            imageView.setImageResource(drawRes);
            textView.setText(str);
        }
        return emptyView;
    }
}
