package com.ft.ftimkit.adapter

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.model.FangTextIMMessage
import com.ft.ftimkit.util.ChatHelper
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import com.ft.ftimkit.bean.MessageMultiEntity
import com.ft.ftimkit.util.QDQQFaceManager
import com.qmuiteam.qmui.qqface.QMUIQQFaceView


/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class TextReceiveItemProvider : BaseMessageProvider() {

    init {
        addChildClickViewIds(R.id.chat_item_header, R.id.chat_item_fail)
        addChildLongClickViewIds(R.id.chat_item_layout_content)
    }

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_RECEIVE_TEXT
    override val layoutId: Int
        get() = R.layout.item_text_receive

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        super.convert(helper, item)
        val msgBody = ChatHelper.getMsgBody<FangTextIMMessage>(item)
        QDQQFaceManager.handlerEmojiText(helper.getView(R.id.chat_item_content_text),msgBody.body.message,false)
//        val tvContent = helper.getView<QMUIQQFaceView>(R.id.chat_item_content_text)
//        tvContent.text = msgBody.body.message
//        helper.setText(R.id.chat_item_content_text, msgBody.body.message)
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