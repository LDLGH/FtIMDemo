package com.ft.ftimkit.interfaces

import com.hyphenate.chat.EMMessage

/**
 * @author LDL
 * @date: 2021/11/2
 * @description: 消息状态回调接口
 */
interface IFangMessageStatusCallback {

    fun onMessageSuccess(message: EMMessage)

    fun onMessageError(message: EMMessage, code: Int, error: String?)

    fun onMessageProgress(message: EMMessage, progress: Int, status: String?)

    fun onSendMessageFinish(message: EMMessage)
}