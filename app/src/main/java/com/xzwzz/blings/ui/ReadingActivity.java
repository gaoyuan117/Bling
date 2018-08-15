package com.xzwzz.blings.ui;

import android.app.ProgressDialog;
import android.webkit.WebView;

import com.xzwzz.blings.R;
import com.xzwzz.blings.api.http.BaseObjObserver;
import com.xzwzz.blings.api.http.RetrofitClient;
import com.xzwzz.blings.api.http.RxUtils;
import com.xzwzz.blings.base.BaseActivity;
import com.xzwzz.blings.bean.BookBean;
import com.xzwzz.blings.bean.NovieceBean;
import com.xzwzz.blings.utils.StatusBarUtil;

public class ReadingActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_reading;
    }

    @Override
    protected void initView() {
        webView = findViewById(R.id.webview);
        setToolbar("新手必看",true);
        noviece();
    }

    // 绘制HTML
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


    private void noviece(){
        RetrofitClient.getInstance().createApi().noviece("Home.noviece")
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<NovieceBean>() {
                    @Override
                    protected void onHandleSuccess(NovieceBean bean) {
                        webView.loadData(getHtmlData(bean.getContent()), "text/html; charset=utf-8", "utf-8");
                    }
                });
    }

}
