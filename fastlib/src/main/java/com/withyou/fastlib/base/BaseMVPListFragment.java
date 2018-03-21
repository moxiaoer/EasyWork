package com.withyou.fastlib.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.withyou.fastlib.R;
import com.withyou.fastlib.basemvp.BaseModel;
import com.withyou.fastlib.basemvp.BasePresenter;
import com.withyou.fastlib.entity.ListType;
import com.withyou.fastlib.util.LogU;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dusan on 2018/3/21.
 */

public abstract class BaseMVPListFragment<T extends BasePresenter, E extends BaseModel, D> extends
        BaseFragment<T, E>{

    protected RecyclerView mRecyclerView;
    protected RefreshLayout mRefreshLayout;
    protected ListAdapter mListAdapter;
    private LoadMoreView mLoadMoreView;

    /**
     * grid布局与瀑布流布局默认行数
     */
    private int mSpanCount = 1;

    /**
     * 默认为普通list布局
     */
    private ListType mListType = ListType.LINEAR_LAYOUT_MANAGER;

    /**
     * 排列方式默认垂直
     */
    private boolean isVertical = true;

    /**
     * 是否开启下拉刷新，默认不开启
     */
    private boolean isOpenRefresh = false;

    /**
     * 是否开启上拉加载更多，默认不开启
     */
    private boolean isOpenLoadMore = false;

    private int mPage = 1;

    private int mPageSize = 10;


    @Override
    protected int getLayoutRes() {
        return R.layout.layout_comment_list;
    }

    @Override
    protected void initView(View rootView) {

        if (0 == getLayoutRes()) {
            throw new RuntimeException("LayoutResId is null!");
        }

        mRecyclerView = rootView.findViewById(R.id.base_list);
        mRefreshLayout = rootView.findViewById(R.id.refreshLayout);

        initRecycler();
        setMvp(true);
    }

    @Override
    public void initToolBar(Toolbar toolbar) {

    }

    public class ListAdapter extends BaseQuickAdapter<D, BaseViewHolder> {

        public ListAdapter(int layoutResId, @Nullable List<D> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, D item) {
            MyHolder(helper, item);
        }
    }

    /**
     * 设置load界面的多种状态 没有更多、loading、加载失败三种三种状态
     *
     * @param loadMoreView load界面多状态布局
     */
    protected void setLoadMordTypeLayout(LoadMoreView loadMoreView) {
        this.mLoadMoreView = loadMoreView;
    }

    /**
     * @param type       布局管理type
     * @param isVertical 是否是垂直的布局 ，true垂直布局，false横向布局
     */
    protected void setListType(ListType type, boolean isVertical) {
        this.mListType = type;
        this.isVertical = isVertical;
    }

    /**
     * 为grid样式和瀑布流设置横向或纵向数量
     *
     * @param spanCount 数量
     */
    protected void setSpanCount(int spanCount) {
        if (spanCount > 0)
            mSpanCount = spanCount;
    }

    /**
     * 获取加载更多的view对象
     * @return
     */
    public LoadMoreView getLoadMoreView() {

        return mLoadMoreView == null ? new LoadMoreView() {
            @Override
            public int getLayoutId() {
                return R.layout.quick_view_load_more;
            }

            @Override
            protected int getLoadingViewId() {
                return R.id.load_more_loading_view;
            }

            @Override
            protected int getLoadFailViewId() {
                return R.id.load_more_load_fail_view;
            }

            @Override
            protected int getLoadEndViewId() {
                return R.id.load_more_load_end_view;
            }
        } : mLoadMoreView;

    }

    /**
     * 初始化子布局
     * @return
     */
    @LayoutRes
    protected abstract int initItemLayout();

    /**
     * 在这个方法里对recyclerview进行初始化
     */
    protected abstract void initSetting();

    /**
     * 选择布局种类
     * @param listType 布局类型
     * @param isVertical
     */
    private void chooseListType(ListType listType, boolean isVertical) {
        switch (listType) {
            case LINEAR_LAYOUT_MANAGER:
                //设置布局管理器
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                linearLayoutManager.setOrientation(isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);

                mRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            case GRID_LAYOUT_MANAGER:

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mSpanCount);

                gridLayoutManager.setOrientation(isVertical ? GridLayoutManager.VERTICAL : GridLayoutManager.HORIZONTAL);

                mRecyclerView.setLayoutManager(gridLayoutManager);
                break;
            case STAGGERED_GRID_LAYOUT_MANAGER:
                //设置布局管理器
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                        (mSpanCount, isVertical ? StaggeredGridLayoutManager.VERTICAL : StaggeredGridLayoutManager.HORIZONTAL);

                mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
            default:
                //设置布局管理器
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                layoutManager.setOrientation(isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);

                mRecyclerView.setLayoutManager(layoutManager);
                break;
        }
        mRecyclerView.setAdapter(mListAdapter);
    }

    /**
     * 处理mRefreshLayout与mRecyclerView的未知空情况bug
     */
    private void judgeViewIsNull() {

        if (mRefreshLayout == null) {
            mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
        }

        if (mRecyclerView == null) {
            mRecyclerView = rootView.findViewById(R.id.base_list);
        }

        if (mListAdapter == null) {
            initRecycler();
        }
    }

    /**
     * adapter内的处理
     *
     * @param baseViewHolder BaseViewHolder
     * @param t              泛型T
     */
    protected abstract void MyHolder(BaseViewHolder baseViewHolder, D t);

    /**
     * 初始化Recycler的适配器和刷新控件，以及其他设置
     */
    private void initRecycler() {
        mListAdapter = new ListAdapter(initItemLayout(), new ArrayList<D>());
        initSetting();
        mListAdapter.setLoadMoreView(getLoadMoreView());
        chooseListType(mListType, isVertical);

        if (isOpenRefresh && mRefreshLayout != null ) {
            if (isOpenLoadMore){
                mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
                    @Override
                    public void onLoadmore(RefreshLayout refreshlayout) {
                        judgeViewIsNull();
                        if (mRefreshLayout != null)
                            loadMoreListener();
                    }

                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        judgeViewIsNull();
                        refreshListener();
                    }
                });
            }else {
                mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        judgeViewIsNull();
                        refreshListener();
                    }
                });
            }

        }
    }

    /**
     * 初始化子布局
     */
    protected abstract void refreshListener();

    /**
     * 初始化子布局
     */
    protected abstract void loadMoreListener();


    public int getPageSize() {
        return mPageSize;
    }

    /**
     * 设置每页的条数
     * @param mPageSize
     */
    public void setPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    /**
     * 是否开启刷新和加载更多，默认不开启
     *
     * @param isOpenRefresh
     * @param isOpenLoadMore
     */
    public void isOpenLoad(boolean isOpenRefresh, boolean isOpenLoadMore) {
        this.isOpenRefresh = isOpenRefresh;
        this.isOpenLoadMore = isOpenLoadMore;
    }

    public void okRefresh() {
        judgeViewIsNull();
        if (mListAdapter != null) {
            mPage = 2;
            mRefreshLayout.finishRefresh();
            mRefreshLayout.setLoadmoreFinished(false);//恢复上拉状态
            LogU.d("refresh_complete");
        }
    }

    public void autoListLoad(@Nullable List<D> tList, String empty_str, @DrawableRes int Empty_res) {
        tList = tList == null ? new ArrayList<D>() : tList;
        if (getPage() == 1) {
            okRefresh();
            mListAdapter.setNewData(tList);
            if (tList.size() == 0) {
                mListAdapter.setEmptyView(getEmptyView(empty_str, Empty_res));
            }
        } else {
            if (tList.size() == mPageSize) {
                okLoadMore(true);
            } else {
                okLoadMore(false);
            }
            mListAdapter.addData(tList);
        }
    }

    /**
     * 包含错误处理自动化，在接口返回错误处使用
     *
     * @param tList
     * @param empty_str
     * @param empty_res
     * @param isFail
     */
    public void autoListLoad(@Nullable List<D> tList, String empty_str, @DrawableRes int empty_res, boolean isFail) {
        if (isFail && getPage() != 1) {
            failLoadMore();
        } else {
            autoListLoad(tList, empty_str, empty_res);
        }
    }


    public void okLoadMore(boolean isHashNext) {
        judgeViewIsNull();

        mPage++;
        mRefreshLayout.finishLoadmore();

        if (!isHashNext) {
            //false为显示加载结束，true为不显示
            mRefreshLayout.setLoadmoreFinished(true);//设置之后，将不会再触发加载事件
        }
    }


    public void failLoadMore() {
        judgeViewIsNull();
        mRefreshLayout.finishLoadmore();
    }

    /**
     * 进入自动加载
     */
    protected void autoRefresh() {
        mRefreshLayout.autoRefresh();
    }

    /**
     * 提供改变显示方法（该方法用于布局显示后动态改变显示方式）
     */
    protected void changeShowType(ListType listType, boolean isVertical) {
        chooseListType(listType, isVertical);
    }

    @Override
    public void onDestroy() {
        mListAdapter = null;
        mRefreshLayout = null;
        super.onDestroy();
    }
}
