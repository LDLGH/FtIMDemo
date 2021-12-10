package com.ft.ftimkit.util.image

import android.content.Context
import android.widget.ImageView

/**
 * @author LDL
 * @date: 2021/5/24
 * @description:图片加载器
 */
interface IChatGlideLoader {

    fun load(context: Context, url: String, imageView: ImageView)


    fun loadCircle(context: Context, url: String, imageView: ImageView)


    fun loadRounded(context: Context, url: String, roundingRadius: Int, imageView: ImageView)

    fun loadChatImage(context: Context, url: String, imageView: ImageView)

}