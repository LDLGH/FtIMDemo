package com.ft.ftimkit.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class MessageMultiEntity(override val itemType: Int) : MultiItemEntity {

    companion object {
        const val TYPE_SEND_TEXT = 1
        const val TYPE_RECEIVE_TEXT = 2
        const val TYPE_SEND_IMAGE = 3
        const val TYPE_RECEIVE_IMAGE = 4
        const val TYPE_SEND_VIDEO = 5
        const val TYPE_RECEIVE_VIDEO = 6
        const val TYPE_SEND_FILE = 7
        const val TYPE_RECEIVE_FILE = 8
        const val TYPE_SEND_AUDIO = 9
        const val TYPE_RECEIVE_AUDIO = 10
        const val TYPE_UNKNOWN = -1
    }

}
