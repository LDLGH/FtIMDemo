package com.ft.ftimkit.adapter

import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.hyphenate.chat.EMMessage
import com.ft.ftimkit.bean.MessageMultiEntity

/**
 * @author LDL
 * @date: 2021/7/23
 * @description: 未知消息
 */
class UnknownItemProvider : BaseItemProvider<EMMessage>() {

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_UNKNOWN

    override val layoutId: Int
        get() = R.layout.item_unknown

    override fun convert(helper: BaseViewHolder, item: EMMessage) {

    }

}