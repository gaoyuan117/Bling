package com.xzwzz.blings.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.blings.AppConfig;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseObjObserver;
import com.xzwzz.blings.api.http.HttpResult;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseActivity;
import com.xzwzz.blings.bean.AvVideoListBean;
import com.xzwzz.blings.bean.DiamondAdBean;
import com.xzwzz.blings.bean.VideoDetailBean;
import com.xzwzz.blings.module.video.VideoPlayActivity;
import com.xzwzz.blings.ui.adapter.AvDetailAdapter;
import com.xzwzz.blings.ui.adapter.VideoDetailAdapter;
import com.xzwzz.blings.utils.GlideUtils;
import com.xzwzz.blings.utils.PayUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AvDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private LinearLayout layoutTips;
    private ImageView videoImg;
    private ImageView imgAd;
    private TextView tvNum;
    private TextView tvTitle;
    private TextView tvWatch;
    private TextView tvCount;
    private RecyclerView recyclerView;

    private String id;
    private List<VideoDetailBean.ListBean> list = new ArrayList<>();
    private AvDetailAdapter adapter;
    private DiamondAdBean adBean;
    private boolean vip = false;
    private VideoDetailBean detailBean;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        if (title.length() > 8) {
            title = title.substring(0, 8);
        }
        setToolbar(title, true);

        layoutTips = findViewById(R.id.layout_tips);
        videoImg = findViewById(R.id.img_diamond);
        imgAd = findViewById(R.id.img_ad);
        tvNum = findViewById(R.id.tv_view);
        tvTitle = findViewById(R.id.tv_diamond_title);
        tvCount = findViewById(R.id.tv_diamond);
        tvWatch = findViewById(R.id.tv_watch);
        tvCount.setVisibility(View.GONE);
        tvNum.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recycler);
        layoutTips.setVisibility(View.GONE);
        imgAd.setVisibility(View.GONE);
        tvWatch.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        adapter = new AvDetailAdapter(R.layout.item_av_detail, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        ad();
        videoImg.setOnClickListener(v -> toActivity());
        findViewById(R.id.close).setOnClickListener(v -> layoutTips.setVisibility(View.GONE));
        imgAd.setOnClickListener(v -> toBrower());
    }

    @Override
    protected void onResume() {
        super.onResume();
        video();
        getFreeNum();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (detailBean == null || adBean == null) return;
        Intent intent = new Intent(this, AvDetailActivity.class);
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("id", list.get(position).getId());
        startActivity(intent);
        finish();
    }

    private void video() {
        RetrofitClient.getInstance().createApi().videoDetail("Home.Videodetails", AppContext.getInstance().getLoginUid(), id)
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<VideoDetailBean>() {
                    @Override
                    protected void onHandleSuccess(VideoDetailBean bean) {
                        detailBean = bean;
                        GlideUtils.glide(AvDetailActivity.this, bean.getDetails().getVideo_img(), videoImg);

                        GlideUtils.glide(mContext, bean.getDetails().getVideo_img(), videoImg);
                        tvNum.setText(bean.getDetails().getWatch_num() + "");
                        tvTitle.setText(bean.getDetails().getTitle());
                        tvCount.setText(bean.getDetails().getCoin() + "");
                        tvWatch.setText(bean.getDetails().getWatch_num() + "");
                        list.clear();
                        if (bean.getList() == null || bean.getList().size() == 0) return;
                        list.addAll(bean.getList());
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void ad() {
        RetrofitClient.getInstance().createApi().diamondAv("Home.avneiye")
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<DiamondAdBean>() {
                    @Override
                    protected void onHandleSuccess(DiamondAdBean bean) {
                        imgAd.setVisibility(View.VISIBLE);
                        adBean = bean;
                        GlideUtils.glide(AvDetailActivity.this, bean.getThumb(), imgAd);
                    }
                });
    }

    private void toBrower() {
        if (adBean == null) return;
        Uri uri = Uri.parse(adBean.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    private void getFreeNum() {
        RetrofitClient.getInstance().createApi().getfreenum("Home.getfreenum", AppContext.getInstance().getLoginUid(), "1")
                .compose(RxUtils.io_main())
                .subscribe(new Observer<HttpResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (httpResult.ret == 200) {
                            if (httpResult.data.code == 0) {
                                vip = true;
                                layoutTips.setVisibility(View.VISIBLE);
                            } else {
                                vip = false;
                                layoutTips.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void toActivity() {
        if (detailBean == null || adBean == null) return;
        if (vip) {
            startActivity();
        } else {
            if (!AppConfig.AVMEMBER) {
                PayUtils.payDialog(AvDetailActivity.this, R.mipmap.av_pay_bg, "AV区", "新用户免费观看5部影片", 2, AppContext.avChargeList);
                return;
            }
            startActivity();
        }

    }

    private void startActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("title", detailBean.getDetails().getTitle());
        bundle.putSerializable("url", detailBean.getDetails().getVideo_url());
        bundle.putSerializable("type", "1");
        bundle.putSerializable("id", detailBean.getDetails().getId());
        ActivityUtils.startActivity(bundle, VideoPlayActivity.class);
    }
}
