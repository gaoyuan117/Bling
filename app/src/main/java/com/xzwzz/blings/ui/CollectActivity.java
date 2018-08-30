package com.xzwzz.blings.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.base.BaseActivity;
import com.xzwzz.blings.bean.ChannelDataBean;
import com.xzwzz.blings.bean.CollectBean;
import com.xzwzz.blings.bean.CollectBeanDao;
import com.xzwzz.blings.module.live.LivePlayActivity;
import com.xzwzz.blings.module.live.adapter.LiveChannelAdapter;
import com.xzwzz.blings.utils.MemberUtil;
import com.xzwzz.blings.utils.PayUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

    private CollectBeanDao collectBeanDao;
    private LiveChannelAdapter adapter;
    private List<ChannelDataBean.DataBean> mList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initView() {
        mList.clear();
        setToolbar("收藏", true);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildPosition(view);
                int offest = SizeUtils.dp2px(5f);
                if (position % 2 == 0) {
                    outRect.set(offest, offest, offest / 2, 0);
                } else if (position % 2 == 1) {
                    outRect.set(offest / 2, offest, offest, 0);
                }
            }
        });

        adapter = new LiveChannelAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        adapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void initData() {
        collectBeanDao = AppContext.getDaoInstant().getCollectBeanDao();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        List<CollectBean> list = collectBeanDao.queryBuilder().list();
        if (list == null || list.size() == 0) {
            adapter.setNewData(mList);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ChannelDataBean.DataBean bean = new ChannelDataBean.DataBean();
            CollectBean collectBean = list.get(i);
            bean.setBigpic(collectBean.getImg());
            bean.setName(collectBean.getName());
            bean.setUrl(collectBean.getUrl());
            bean.setNum(collectBean.getNum());
            bean.setUid(collectBean.getId());
            mList.add(bean);
        }
        adapter.setNewData(mList);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ChannelDataBean.DataBean item = (ChannelDataBean.DataBean) adapter.getItem(position);

        Intent intent = new Intent(this,LivePlayActivity.class);
        intent.putExtra("data",item);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                PayUtils.payDialog(this, R.mipmap.zb_pay_bg, "直播区", "", 1, AppContext.zbChargeList);
            }
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

        new AlertDialog.Builder(this).setMessage("是否移除该收藏")
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    CollectBean collectBean = new CollectBean();
                    collectBean.setUrl(mList.get(position).getUrl());
                    collectBean.setNum(mList.get(position).getNum());
                    collectBean.setName(mList.get(position).getName());
                    collectBean.setImg(mList.get(position).getBigpic());
                    collectBeanDao.deleteByKey(mList.get(position).getUid());
                    mList.remove(position);
                    adapter.notifyDataSetChanged();

                }).show();

        return true;
    }
}
