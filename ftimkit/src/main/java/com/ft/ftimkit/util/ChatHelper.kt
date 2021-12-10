package com.ft.ftimkit.util

import android.text.TextUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.ft.ftimkit.R
import com.ft.ftimkit.model.ChatType
import com.ft.ftimkit.model.FangIMMessage
import com.ft.ftimkit.model.FangTextIMMessage
import com.ft.ftimkit.model.MsgType
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage


/**
 * @author LDL
 * @date: 2021/11/4
 * @description:
 */
object ChatHelper {

    /**
     * 根据类型获取自定义消息体
     *
     * @param T 相关类型
     * @param message 消息体
     * @return T
     */
    inline fun <reified T> getMsgBody(message: EMMessage): T {
        val customMessageBody = message.body as EMCustomMessageBody
        return GsonUtils.fromJson(
            customMessageBody.params[IMConstants.MSG_BODY],
            T::class.java
        )
    }

    /**
     * 获取信息摘要
     *
     * @param message EMMessage
     */
    fun getMessageDigest(message: EMMessage): String {
        val digest: String
        when (getMsgBody<FangIMMessage>(message).msgType) {
            MsgType.TXT.ordinal -> {
                val msgBody = getMsgBody<FangTextIMMessage>(message)
                digest = msgBody.body.message
            }
            MsgType.IMAGE.ordinal -> {
                digest = StringUtils.getString(R.string.im_picture)
            }
            MsgType.VOICE.ordinal -> {
                digest = StringUtils.getString(R.string.im_voice)
            }
            MsgType.FILE.ordinal -> {
                digest = StringUtils.getString(R.string.im_file)
            }
            MsgType.VIDEO.ordinal -> {
                digest = StringUtils.getString(R.string.im_video)
            }
            else -> {
                digest = StringUtils.getString(R.string.im_unknown)
            }
        }
        return digest
    }

    /**
     * 判断是否是时间戳
     * @param time
     * @return
     */
    fun isTimestamp(time: String): Boolean {
        if (TextUtils.isEmpty(time)) {
            return false
        }
        var timestamp = 0L
        try {
            timestamp = time.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return timestamp > 0
    }


    /**
     * 是否为群聊类型
     *
     * @param chatType
     * @return
     */
    @JvmStatic
    fun isGroupChat(chatType: ChatType): Boolean {
        return chatType == ChatType.GroupChat
    }
}