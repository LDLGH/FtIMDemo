package com.ft.ftimkit.adapter

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.hyphenate.chat.EMMessage
import com.ft.ftimkit.bean.MessageMultiEntity


/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class VideoSendItemProvider : BaseMessageProvider() {

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_SEND_VIDEO
    override val layoutId: Int
        get() = R.layout.item_video_send

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        super.convert(helper, item)

    }
}