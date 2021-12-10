package com.ft.imdemo.app

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.ft.ftimkit.FangIM
import com.ft.ftimkit.interfaces.IFangIMConnectionListener
import com.ft.ftimkit.util.QDQQFaceManager
import com.qmuiteam.qmui.qqface.QMUIQQFaceCompiler

/**
 * @author LDL
 * @date: 2021/11/2
 * @description:
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FangIM.init(this)
        FangIM.addConnectionListener(object : IFangIMConnectionListener {
            override fun onConnected() {
                LogUtils.i("已连接环信服务器")
            }

            override fun onDisconnected(errorCode: Int) {
                LogUtils.i("连接环信服务器错误$errorCode")
            }
        })
        QMUIQQFaceCompiler.setDefaultQQFaceManager(QDQQFaceManager.getInstance())
    }
}