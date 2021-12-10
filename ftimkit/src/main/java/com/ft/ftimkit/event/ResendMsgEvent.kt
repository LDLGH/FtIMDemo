package com.ft.ftimkit.event

import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * @author LDL
 * @date: 2021/7/2
 * @description: 重新发送消息
 */
class ResendMsgEvent(var message: EMMessage) : LiveEvent