package com.ft.ftimkit.adapter

import ChatGlideLoader
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.ft.ftimkit.bean.MessageMultiEntity
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.model.FangImageIMMessage
import com.ft.ftimkit.util.BitmapUtil
import com.ft.ftimkit.util.ChatHelper
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader


/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class ImageSendItemProvider : BaseMessageProvider() {

    init {
        addChildClickViewIds(R.id.bivPic)
        addChildLongClickViewIds(R.id.bivPic)
    }

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_SEND_IMAGE
    override val layoutId: Int
        get() = R.layout.item_image_send

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        super.convert(helper, item)
        val msgBody = ChatHelper.getMsgBody<FangImageIMMessage>(item).body
        val ivPic = helper.getView<ImageView>(R.id.bivPic)
        val imageSize = BitmapUtil.getImageSize(msgBody.width, msgBody.height)
        val layoutParams = RelativeLayout.LayoutParams(imageSize.width, imageSize.height)
        ivPic.layoutParams = layoutParams

        val imageUrl: String =
            if (ObjectUtils.isNotEmpty(msgBody.localUrl) && FileUtils.isFileExists(msgBody.localUrl)) {
                msgBody.localUrl!!
            } else {
                msgBody.remoteUrl!!
            }
        ChatGlideLoader.loadRounded(context, imageUrl, 5, ivPic)
    }

    override fun onChildClick(helper: BaseViewHolder, view: View, data: EMMessage, position: Int) {
        super.onChildClick(helper, view, data, position)
        if (view.id == R.id.bivPic) {
            getAdapter()!!.data[position].let {
                val msgBody = ChatHelper.getMsgBody<FangImageIMMessage>(it).body
                val imageUrl: String =
                    if (ObjectUtils.isNotEmpty(msgBody.localUrl) && FileUtils.isFileExists(msgBody.localUrl)) {
                        msgBody.localUrl!!
                    } else {
                        msgBody.remoteUrl!!
                    }
                XPopup.Builder(context)
                    .asImageViewer(
                        helper.getView(R.id.bivPic),
                        imageUrl,
                        SmartGlideImageLoader()
                    )
                    .show()
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