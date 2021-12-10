package com.ft.imdemo;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.hjq.xtoast.XToast;
import com.hjq.xtoast.draggable.SpringDraggable;

/**
 * @author LDL
 * @date: 2021/11/17
 * @description:
 */
public class XToastHelper {

    public static void show(Activity activity){
        new XToast<>(activity)
                .setContentView(R.layout.toast_phone)
                .setGravity(Gravity.END | Gravity.TOP)
                .setYOffset(200)
                // 设置指定的拖拽规则
                .setDraggable(new SpringDraggable())
                .setOnClickListener(android.R.id.icon, new XToast.OnClickListener<ImageView>() {

                    @Override
                    public void onClick(XToast<?> toast, ImageView view) {
                        ToastUtils.showLong("我被点击了");
                    }
                })
                .show();
    }

}
