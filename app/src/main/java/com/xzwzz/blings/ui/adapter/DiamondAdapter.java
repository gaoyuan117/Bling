package com.xzwzz.blings.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.blings.R;
import com.xzwzz.blings.bean.MoviesLinkListBean;
import com.xzwzz.blings.bean.PayDialogBean;

import java.util.List;

/**
 * Created by gaoyuan on 2018/8/5.
 */

public class DiamondAdapter extends BaseQuickAdapter<MoviesLinkListBean,BaseViewHolder>{

    public DiamondAdapter(int layoutResId, @Nullable List<MoviesLinkListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MoviesLinkListBean item) {
        ImageView imageView = helper.getView(R.id.img_diamond);
        Glide.with(mContext).load(item.getImg_url()).into(imageView);
        helper.setText(R.id.tv_diamond_title,item.getTitle());
        helper.setText(R.id.tv_diamond_time,item.getUptime());
        helper.setText(R.id.tv_diamond_view,item.getWatch_num());
        helper.setText(R.id.tv_diamond,item.getCoin()+"");
    }
}
