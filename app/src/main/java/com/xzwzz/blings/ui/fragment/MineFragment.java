package com.xzwzz.blings.ui.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.blings.AppConfig;
import com.xzwzz.blings.AppContext;
import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseListObserver;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseFragment;
import com.xzwzz.blings.bean.QrCodeBean;
import com.xzwzz.blings.bean.UserBean;
import com.xzwzz.blings.bean.UserInfoBean;
import com.xzwzz.blings.ui.BuyActivity;
import com.xzwzz.blings.ui.CollectActivity;
import com.xzwzz.blings.ui.PayActivity;
import com.xzwzz.blings.ui.ReadingActivity;
import com.xzwzz.blings.ui.SettingActivity;
import com.xzwzz.blings.ui.ShareActivity;
import com.xzwzz.blings.ui.UserInfoDetailActivity;
import com.xzwzz.blings.ui.UserProfitActivity;
import com.xzwzz.blings.ui.VipActivity;
import com.xzwzz.blings.ui.login.ModifyPwdActivity;
import com.xzwzz.blings.utils.DialogHelp;
import com.xzwzz.blings.utils.PayUtils;
import com.xzwzz.blings.utils.ShareUtil;
import com.xzwzz.blings.utils.StatusBarUtil;
import com.xzwzz.blings.widget.LineControlView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private android.widget.TextView mTvName;
    private android.widget.TextView mTvId;
    private android.widget.TextView tvVipTime;
    private android.widget.TextView tvAvVipTime;
    private android.support.v7.widget.Toolbar mToolbar;
    private LineControlView tvDiamond;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mTvName = view.findViewById(R.id.tv_name);
        tvVipTime = view.findViewById(R.id.tv_vip_time);
        tvAvVipTime = view.findViewById(R.id.tv_av_vip_time);
        mTvId = view.findViewById(R.id.tv_id);
        StatusBarUtil.getInstance().setPaddingSmart(getActivity(), mToolbar);

        tvDiamond = view.findViewById(R.id.zuanshi);
        tvDiamond.setOnClickListener(this);
        view.findViewById(R.id.kaitonghuiyuan).setOnClickListener(this);
        view.findViewById(R.id.avhuiyuan).setOnClickListener(this);
        view.findViewById(R.id.xinshou).setOnClickListener(this);
        view.findViewById(R.id.yigoushipin).setOnClickListener(this);
        view.findViewById(R.id.shoucang).setOnClickListener(this);
        view.findViewById(R.id.kefu).setOnClickListener(this);
    }

    @Override
    public void initData() {
        UserBean loginUser = AppContext.getInstance().getLoginUser();
        mTvId.setText("会员号:" + loginUser.id);
        mTvName.setText(loginUser.user_nicename);
        flushUserInfo();
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zuanshi://钻石充值
                PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "钻石区", "新用户免费观看5部影片", 3, AppContext.zsChargeList);
                break;
            case R.id.kaitonghuiyuan://开通会员
                PayUtils.payDialog(getActivity(), R.mipmap.zb_pay_bg, "直播区", "", 1, AppContext.zbChargeList);
                break;
            case R.id.avhuiyuan://AV会员
                PayUtils.payDialog(getActivity(), R.mipmap.av_pay_bg, "AV区", "新用户免费观看5部影片", 2, AppContext.avChargeList);
                break;
            case R.id.xinshou://新手必看
                startActivity(new Intent(getActivity(), ReadingActivity.class));
                break;
            case R.id.yigoushipin://已购视频
                startActivity(new Intent(getActivity(), BuyActivity.class));
                break;
            case R.id.shoucang://我的收藏
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            case R.id.kefu://客服
                copy();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        flushUserInfo();
    }

    @Override
    protected void onUserVisible() {
        flushUserInfo();
    }

    private void copy() {
        ClipboardManager cm = (ClipboardManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        if (TextUtils.isEmpty(AppConfig.QQ)) {
            cm.setText("暂未设置");
        } else {
            cm.setText(AppConfig.QQ);
        }
        ToastUtils.showShort("复制成功：" + AppConfig.QQ);
    }

    private void flushUserInfo() {
        String loginUid = AppContext.getInstance().getLoginUid();
        if (loginUid.equals("0")) return;
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                            AppConfig.AVMEMBER = (bean.is_av_member == 1);

                            tvVipTime.setText(bean.member_validity);
                            tvAvVipTime.setText(bean.avmember_validity);

                            mTvName.setText("昵称:" + bean.user_nicename);

                            mTvId.setText("ID:" + bean.id);

                            String coin = " 剩余钻石数："+bean.coin;
                            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                            SpannableString spannableString = new SpannableString(coin);
                            spannableString.setSpan(span, 7, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvDiamond.setContent(spannableString);

                        }
                    }
                });
    }


}
