package com.xzwzz.blings.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.blings.AppConfig;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseListObserver;
import com.xzwzz.blings.api.http.HttpResult;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseFragment;
import com.xzwzz.blings.bean.AdListBean;
import com.xzwzz.blings.bean.AvVideoListBean;
import com.xzwzz.blings.bean.HotBean;
import com.xzwzz.blings.bean.MoviesLinkListBean;
import com.xzwzz.blings.bean.VideoListBean;
import com.xzwzz.blings.module.video.VideoPlayActivity;
import com.xzwzz.blings.ui.VideoDetailActivity;
import com.xzwzz.blings.ui.adapter.DiamondAdapter;
import com.xzwzz.blings.ui.login.LoginActivity;
import com.xzwzz.blings.utils.LoginUtils;
import com.xzwzz.blings.utils.MemberUtil;
import com.xzwzz.blings.utils.MyImageLoader;
import com.xzwzz.blings.utils.PayUtils;
import com.xzwzz.blings.utils.StatusBarUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class DiamondFragment extends BaseFragment implements OnBannerClickListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {
    private Toolbar mToolbar;
    private Banner mBanner;
    private List<String> bannerList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;

    private DiamondAdapter adapter;
    private List<MoviesLinkListBean> list = new ArrayList<>();
    private List<AdListBean> adListBean;


    @Override
    public int getLayoutId() {
        return R.layout.fragmnet_diamond;
    }

    @Override
    public void initView(View view) {

        View headView = View.inflate(getActivity(), R.layout.head_diamond, null);
        mToolbar = view.findViewById(R.id.toolbar);
        mBanner = headView.findViewById(R.id.banner);
        recyclerView = view.findViewById(R.id.recycler);
        refresh = view.findViewById(R.id.refresh);
        mBanner.setOnBannerClickListener(this);
        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new DiamondAdapter(R.layout.item_diamond, list);
        adapter.addHeaderView(headView);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        getBanner();

    }

    @Override
    public void onResume() {
        super.onResume();
        getMoviesLinkList();
    }

    @Override
    protected void setListener() {
        refresh.setOnRefreshListener(this);
    }

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBean.get(position - 1).getUrl()) || !adListBean.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBean.get(position - 1).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getBanner();
        getMoviesLinkList();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);

    }


    private void getBanner() {
        RetrofitClient.getInstance().createApi().adsList("Home.coin_adsList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AdListBean>() {
                    @Override
                    protected void onHandleSuccess(List<AdListBean> list) {
                        adListBean = list;
                        if (list == null || list.size() == 0) return;
                        bannerList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            bannerList.add(list.get(i).getThumb());
                        }
                        setBanner();
                    }
                });
    }

    private void setBanner() {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setImageLoader(new MyImageLoader());
        mBanner.setImages(bannerList);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.isAutoPlay(true);
        mBanner.setViewPagerIsScroll(true);
        mBanner.setDelayTime(3000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }

    private void getMoviesLinkList() {
        RetrofitClient.getInstance().createApi().moviesLinkList("Home.MoviesLinkList", AppContext.getInstance().getLoginUid()).compose(RxUtils.io_main())
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


}
