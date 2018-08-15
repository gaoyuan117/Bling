package com.xzwzz.blings.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.blings.AppConfig;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseListObserver;
import com.xzwzz.blings.api.http.HttpResult;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseFragment;
import com.xzwzz.blings.bean.AvVideoListBean;
import com.xzwzz.blings.module.video.VideoPlayActivity;
import com.xzwzz.blings.ui.AvDetailActivity;
import com.xzwzz.blings.ui.adapter.AvAdapter;
import com.xzwzz.blings.utils.PayUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by a on 2018/8/5.
 */

public class AvItemFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    private RecyclerView recycler;

    private List<AvVideoListBean> list = new ArrayList<>();
    private AvAdapter adapter;
    private String id;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_av_item;
    }

    @Override
    public void initView(View view) {
        id = getArguments().getString("id");
        Log.e("gy", "id:" + id);
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                int offest = SizeUtils.dp2px(6f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest / 2, 0);
                } else if (position % 2 == 2) {
                    outRect.set(offest / 2, offest, 0, 0);
                }
            }
        });

        adapter = new AvAdapter(R.layout.item_av, list);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        getVideoList();
    }

    public void getVideoList() {
        RetrofitClient.getInstance().createApi().videoList("Home.VideoList", id).compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<AvVideoListBean>() {
                    @Override
                    protected void onHandleSuccess(List<AvVideoListBean> avList) {
                        if (avList == null || avList.size() == 0) return;
                        list.clear();
                        list.addAll(avList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        AvVideoListBean bean = list.get(position);
        toActivity(bean);
    }

    private void toActivity(AvVideoListBean bean) {
        Bundle bundle = new Bundle();
        bundle.putString("title", bean.getTitle());
        bundle.putString("url", bean.getVideo_url());
        bundle.putString("id", bean.getId());
        bundle.putSerializable("type", "av");
        ActivityUtils.startActivity(bundle, AvDetailActivity.class);
    }
}
