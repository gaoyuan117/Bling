package com.xzwzz.blings.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.blings.R;
import com.xzwzz.blings.bean.VideoDetailBean;
import com.xzwzz.blings.utils.GlideUtils;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/14.
 */

public class VideoDetailAdapter extends BaseQuickAdapter<VideoDetailBean.ListBean,BaseViewHolder>{

    public VideoDetailAdapter(int layoutResId, @Nullable List<VideoDetailBean.ListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoDetailBean.ListBean item) {
        ImageView imageView = helper.getView(R.id.img_av);
        GlideUtils.glide(mContext,item.getImg_url(),imageView);
        helper.setText(R.id.tv_diamond_title,item.getTitle());
        helper.setText(R.id.tv_diamond,item.getCoin()+"");
    }
}
