package com.ft.imdemo.adapter

import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.model.FangConversationInfo
import com.ft.ftimkit.util.ChatHelper
import com.ft.ftimkit.util.TimeUtils
import com.ft.imdemo.R
import com.hyphenate.chat.EMConversation

/**
 * @author LDL
 * @date: 2021/11/2
 * @description:
 */
class ConversationListAdapter :
    BaseQuickAdapter<FangConversationInfo, BaseViewHolder>(R.layout.item_conversation) {

    override fun convert(holder: BaseViewHolder, item: FangConversationInfo) {
        val conversation = item.info as EMConversation
        val lastMessage = conversation.lastMessage
        holder.setText(R.id.tv_name, lastMessage.userName)
        holder.setText(R.id.tv_content, ChatHelper.getMessageDigest(lastMessage))
        holder.setText(R.id.tv_time, TimeUtils.getTimeString(lastMessage.msgTime))

        val unreadMsgCount = conversation.unreadMsgCount
        holder.setVisible(R.id.tv_unreadMsgCount, unreadMsgCount > 0)
        holder.setText(R.id.tv_unreadMsgCount, conversation.unreadMsgCount.toString())
        if (item.isTop) {
            holder.itemView.setBackgroundColor(ColorUtils.getColor(R.color.purple_200))
        } else {
            holder.itemView.setBackgroundColor(ColorUtils.getColor(R.color.white))
        }
    }
}