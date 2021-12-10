package com.ft.ftimkit.event

import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * @author LDL
 * @date: 2021/11/5
 * @description: 发送消息回调
 */
class SendMessageCallBackEvent(var message: EMMessage) : LiveEvent