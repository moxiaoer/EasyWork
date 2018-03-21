package com.withyou.demo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;

import com.chad.library.adapter.base.BaseViewHolder;
import com.withyou.demo.R;
import com.withyou.fastlib.base.BaseNormalListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public class NormalListDemoFragment extends BaseNormalListFragment<String>{


    public static NormalListDemoFragment newInstance() {
        NormalListDemoFragment fragment = new NormalListDemoFragment();
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
    protected void MyHolder(BaseViewHolder baseViewHolder, String t) {

        baseViewHolder.setText(R.id.tv_item_test, t);

    }

    @Override
    protected void refreshListener() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setPage(1);
                autoListLoad(getData(getPage()), "", 0);

            }
        }, 1000);

    }

    @Override
    protected void loadMoreListener() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                autoListLoad(getData(getPage()), "", 0);

            }
        }, 1000);

    }

    private List<String> getData(final int page) {

        List<String> list = new ArrayList<>();
        String string;
        for (int i = 0; i < (page < 4 ? 10 : 5); i++) {
            string = "第" + page + "页" + "第" + i + "个";
            list.add(string);
        }
        return list;

    }
}
