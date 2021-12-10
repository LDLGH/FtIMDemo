package com.ft.ftimkit.model

/**
 * @author LDL
 * @date: 2021/11/5
 * @description:
 */
class FangConversationInfo : Comparable<FangConversationInfo> {

    //会话列表条目对象，可以是会话消息，可以是系统消息等
    var info: Any? = null

    //条目是否选中
    var isSelected = false

    //时间戳
    var timestamp: Long = 0

    //是否置顶
    var isTop = false

    //是否是群组
    var isGroup = false

    override fun compareTo(other: FangConversationInfo): Int {
        return if (timestamp > other.timestamp) -1 else 1
    }

}