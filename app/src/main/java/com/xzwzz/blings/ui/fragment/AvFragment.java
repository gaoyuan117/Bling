package com.xzwzz.blings.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseListObserver;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseFragment;
import com.xzwzz.blings.bean.AdListBean;
import com.xzwzz.blings.ui.adapter.ViewPagerAdapter;
import com.xzwzz.blings.utils.MyImageLoader;
import com.xzwzz.blings.utils.StatusBarUtil;
import com.xzwzz.blings.widget.MyViewPager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by gaoyuan on 2018/8/5.
 */

public class AvFragment extends BaseFragment implements OnBannerClickListener {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    AppBarLayout appBarLayout;
    private android.support.v7.widget.Toolbar mToolbar;
    private Banner mBanner;

    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments;
    private List<String> titles;
    private List<String> bannerList = new ArrayList<>();
    private List<AdListBean> adListBeans;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_av;
    }

    @Override
    public void initView(View view) {
        mBanner = view.findViewById(R.id.banner);
        mToolbar = view.findViewById(R.id.toolbar);
        mTabLayout = view.findViewById(R.id.tb_my);
        mViewPager = view.findViewById(R.id.vp_my);
        appBarLayout = view.findViewById(R.id.appbar);

        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        fragments = new ArrayList<>();
        titles = new ArrayList<>();

        for (int i = 0; i < AppContext.novelTermList.size(); i++) {
            AvItemFragment f = new AvItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", AppContext.novelTermList.get(i).getTerm_id());
            f.setArguments(bundle);
            fragments.add(f);
            titles.add(AppContext.novelTermList.get(i).getName());
        }

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity(), fragments, titles);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

    }

    @Override
    public void initData() {
        getBanner();
        mBanner.setOnBannerClickListener(this);
    }

    private void getBanner() {
        RetrofitClient.getInstance().createApi().adsList("Home.av_adsList")
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AdListBean>() {

                    @Override
                    protected void onHandleSuccess(List<AdListBean> list) {
                        adListBeans = list;
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

    @Override
    public void OnBannerClick(int position) {
        if (TextUtils.isEmpty(adListBeans.get(position - 1).getUrl()) || !adListBeans.get(position - 1).getUrl().startsWith("http"))
            return;
        Uri uri = Uri.parse(adListBeans.get(position - 1).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
