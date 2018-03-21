package com.withyou.demo.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.chad.library.adapter.base.BaseViewHolder;
import com.withyou.demo.R;
import com.withyou.demo.contract.MVPListContract;
import com.withyou.demo.model.MVPListModel;
import com.withyou.demo.presenter.MVPListPresenter;
import com.withyou.fastlib.base.BaseMVPListFragment;

import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public class MVPListDemoFragment extends BaseMVPListFragment<MVPListPresenter, MVPListModel, String>
        implements MVPListContract.View{

    private boolean isRefresh = false;
    private boolean isFail = false;

    public static MVPListDemoFragment newInstance() {

        MVPListDemoFragment fragment = new MVPListDemoFragment();
        return fragment;

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initLogic() {

        autoRefresh();

    }

    @Override
    protected int initItemLayout() {
        return R.layout.item_list_demo;
    }

    @Override
    protected void initSetting() {

        isOpenLoad(true, true);
    }

    @Override
    public void initPresenter() {
        super.initPresenter();
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected void MyHolder(BaseViewHolder baseViewHolder, String t) {

        baseViewHolder.setText(R.id.tv_item_test, t);

    }

    @Override
    protected void refreshListener() {

        isRefresh = true;
        setPage(1);
        mPresenter.getDataRequest(getPage());
    }

    @Override
    protected void loadMoreListener() {

        isRefresh = false;
        mPresenter.getDataRequest(getPage());
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void showData(List<String> list) {

        if (isRefresh) {
            autoListLoad(list, "暂无数据，下拉刷新", 0);
        }else {
            if (isFail) {
                isFail = false;
                autoListLoad(list, "暂无数据，下拉刷新", 0, true);
            }else {
                isFail = true;
                autoListLoad(list, "暂无数据，下拉刷新", 0);
            }
        }

    }
}
