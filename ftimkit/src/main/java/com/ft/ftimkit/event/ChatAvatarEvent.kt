package com.ft.ftimkit.event

import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * @author LDL
 * @date: 2021/7/2
 * @description: 点击用户头像
 */
class ChatAvatarEvent(var userId: String) : LiveEvent