package com.withyou.demo.model;

import android.os.Handler;

import com.withyou.demo.contract.MVPListContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public class MVPListModel implements MVPListContract.Model{

    @Override
    public void onDestroy() {

    }

    @Override
    public void getData(final int page, final onFinishedListener listener) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onFinished(getData(page));
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
