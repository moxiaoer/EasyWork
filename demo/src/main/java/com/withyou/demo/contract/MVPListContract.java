package com.withyou.demo.contract;

import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;
import com.withyou.fastlib.basemvp.BaseView;

import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public interface MVPListContract {

    interface View extends BaseView {
        void showData(List<String> list);
    }

    interface Model extends BaseModel {

        interface onFinishedListener {
            void onFinished(List<String> items);
        }

        void getData(int page, onFinishedListener listener);
    }

    abstract static class Presenter extends BasePresenter<View, Model> {

        public abstract void getDataRequest(int page);

    }
}
