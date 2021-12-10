package com.ft.ftimkit.adapter

import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.ft.ftimkit.bean.MessageMultiEntity
import com.ft.ftimkit.model.FangIMMessage
import com.ft.ftimkit.model.MsgType
import com.ft.ftimkit.util.ChatHelper
import com.hyphenate.chat.EMMessage

/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class ChatAdapter : BaseProviderMultiAdapter<EMMessage>() {

    init {
        addItemProvider(TextSendItemProvider())
        addItemProvider(TextReceiveItemProvider())
        addItemProvider(ImageSendItemProvider())
        addItemProvider(ImageReceiveItemProvider())
        addItemProvider(AudioSendItemProvider())
        addItemProvider(AudioReceiveItemProvider())
        addItemProvider(VideoSendItemProvider())
        addItemProvider(VideoReceiveItemProvider())
        addItemProvider(FileSendItemProvider())
        addItemProvider(FileReceiveItemProvider())
        addItemProvider(UnknownItemProvider())
    }

    override fun getItemType(data: List<EMMessage>, position: Int): Int {
        val emMessage = data[position]
        val msgBody = ChatHelper.getMsgBody<FangIMMessage>(emMessage)
        val isSend = emMessage.direct() == EMMessage.Direct.SEND
        return when (msgBody.msgType) {
            MsgType.TXT.ordinal -> {
                if (isSend) {
                    MessageMultiEntity.TYPE_SEND_TEXT
                } else {
                    MessageMultiEntity.TYPE_RECEIVE_TEXT
                }
            }
            MsgType.IMAGE.ordinal -> {
                if (isSend) {
                    MessageMultiEntity.TYPE_SEND_IMAGE
                } else {
                    MessageMultiEntity.TYPE_RECEIVE_IMAGE
                }
            }
            MsgType.VOICE.ordinal -> {
                if (isSend) {
                    MessageMultiEntity.TYPE_SEND_AUDIO
                } else {
                    MessageMultiEntity.TYPE_RECEIVE_AUDIO
                }
            }
            else -> MessageMultiEntity.TYPE_UNKNOWN
        }
    }
}