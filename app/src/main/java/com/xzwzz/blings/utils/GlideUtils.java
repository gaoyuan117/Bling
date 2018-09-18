package com.xzwzz.blings.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xzwzz.blings.AppConfig;
import com.xzwzz.blings.AppContext;

public class GlideUtils {

    public static void glide(Context context, String url, ImageView imageView) {

        String img = "";

        if (url.contains("http")) {
            img = url;
        } else {
            img = AppConfig.MAIN_URL + url;
        }
        Glide.with(AppContext.getInstance()).load(img).into(imageView);
    }
}
