package com.ft.ftimkit.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.event.ChatAvatarEvent
import com.ft.ftimkit.event.ResendMsgEvent
import com.ft.ftimkit.util.IMConstants.TIME_INTERVAL
import com.ft.ftimkit.util.TimeUtils
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus


/**
 * @author LDL
 * @date: 2021/6/28
 * @description:
 */
abstract class BaseMessageProvider : BaseItemProvider<EMMessage>() {

    init {
        addChildClickViewIds(R.id.chat_item_header, R.id.chat_item_fail)
        addChildLongClickViewIds(R.id.chat_item_layout_content)
    }

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        if (helper.getViewOrNull<ImageView>(R.id.chat_item_fail) != null) {
            when (item.status()) {
                EMMessage.Status.INPROGRESS -> {
                    helper.setVisible(R.id.chat_item_progress, true)
                        .setVisible(R.id.chat_item_fail, false)
                }
                EMMessage.Status.FAIL -> {
                    helper.setVisible(R.id.chat_item_progress, false)
                        .setVisible(R.id.chat_item_fail, true)
                }
                EMMessage.Status.SUCCESS -> {
                    helper.setVisible(R.id.chat_item_progress, false)
                        .setVisible(R.id.chat_item_fail, false)
                }
                else -> helper.setVisible(R.id.chat_item_progress, false)
                    .setVisible(R.id.chat_item_fail, false)
            }
        }
        getAdapter()?.data?.let { list ->
            val position = helper.layoutPosition
            if (position > 0) {
                val message = list[position - 1]
                val preTime = message.msgTime
                if (item.msgTime - preTime > TIME_INTERVAL) {
                    helper.setVisible(R.id.item_tv_time, true)
                    helper.setText(R.id.item_tv_time, TimeUtils.getTimeString(item.msgTime))
                } else {
                    helper.setVisible(R.id.item_tv_time, false)
                }
            } else {
                helper.setVisible(R.id.item_tv_time, true)
                helper.setText(R.id.item_tv_time, TimeUtils.getTimeString(item.msgTime))
            }
        }
        val ivAvatar = helper.getView<ImageView>(R.id.chat_item_header)
    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: EMMessage, position: Int) {
        super.onChildClick(helper, view, data, position)
        getAdapter()?.data?.let {
            val emMessage = it[position]
            when (view.id) {
                R.id.chat_item_header -> {
                    LiveEventBus.get(ChatAvatarEvent::class.java)
                        .post(ChatAvatarEvent(emMessage.from))
                }
                R.id.chat_item_fail -> {
                    getAdapter()?.removeAt(position)
                    LiveEventBus.get(ResendMsgEvent::class.java)
                        .post(ResendMsgEvent(emMessage))
                }
                else -> {
                }
            }
        }
    }

    override fun onChildLongClick(
        helper: BaseViewHolder,
        view: View,
        data: EMMessage,
        position: Int
    ): Boolean {
        LiveEventBus.get(BubbleLongClickEvent::class.java)
            .post(BubbleLongClickEvent(position))
        return true
    }
}