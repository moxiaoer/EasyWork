package com.withyou.demo.presenter;

import com.withyou.demo.contract.MVPListContract;

import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public class MVPListPresenter extends MVPListContract.Presenter
        implements MVPListContract.Model.onFinishedListener{

    @Override
    public void getDataRequest(int page) {
        mModel.getData(page, this);
    }

    @Override
    public void onFinished(List<String> items) {
        mView.showData(items);
    }
}
