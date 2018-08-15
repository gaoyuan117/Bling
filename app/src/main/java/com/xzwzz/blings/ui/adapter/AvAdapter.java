package com.xzwzz.blings.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.blings.R;
import com.xzwzz.blings.bean.AvVideoListBean;
import com.xzwzz.blings.bean.VideoListBean;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/5.
 */

public class AvAdapter extends BaseQuickAdapter<AvVideoListBean,BaseViewHolder>{

    public AvAdapter(int layoutResId, @Nullable List<AvVideoListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AvVideoListBean item) {
        ImageView imageView = helper.getView(R.id.img_av);
        Glide.with(mContext).load(item.getVideo_img()).into(imageView);

        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_time,item.getUptime());
    }
}
