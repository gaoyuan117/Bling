package com.xzwzz.blings.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseListObserver;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseActivity;
import com.xzwzz.blings.bean.MoviesLinkListBean;
import com.xzwzz.blings.module.video.VideoPlayActivity;
import com.xzwzz.blings.ui.adapter.DiamondAdapter;

import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;

    private DiamondAdapter adapter;
    private List<MoviesLinkListBean> list = new ArrayList<>();

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_buy;
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbar("已购视频", true);

        recyclerView = findViewById(R.id.recycler);
        refresh = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiamondAdapter(R.layout.item_diamond, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        refresh.setOnRefreshListener(this);
        getMoviesLinkList();

    }

    private void getMoviesLinkList() {
        RetrofitClient.getInstance().createApi().moviesLinkList("Home.mybuyvideo", AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<MoviesLinkListBean>(refresh) {
                    @Override
                    protected void onHandleSuccess(List<MoviesLinkListBean> moviesLinkList) {
                        if (moviesLinkList == null || moviesLinkList.size() == 0) return;

                        list.clear();
                        list.addAll(moviesLinkList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MoviesLinkListBean bean = list.get(position);
        toActivity(bean);
    }

    private void toActivity(MoviesLinkListBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", bean.getTitle());
        bundle.putSerializable("url", bean.getUrl());
        bundle.putSerializable("type", "diamond");
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }

    @Override
    public void onRefresh() {
        getMoviesLinkList();
    }
}
