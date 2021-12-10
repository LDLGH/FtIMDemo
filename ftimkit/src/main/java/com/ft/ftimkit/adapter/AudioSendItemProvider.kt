package com.ft.ftimkit.adapter

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.ft.ftimkit.bean.MessageMultiEntity
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.http.DownloadUtil
import com.ft.ftimkit.interfaces.IFangIMProgressFileCallback
import com.ft.ftimkit.model.FangVoiceIMMessage
import com.ft.ftimkit.model.FangVoiceIMMessageBody
import com.ft.ftimkit.util.ChatHelper
import com.ft.ftimkit.util.IMConstants.PATH_RECORD
import com.ft.ftimkit.util.MediaManager
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import java.io.File


/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class AudioSendItemProvider : BaseMessageProvider() {

    init {
        addChildClickViewIds(R.id.rlAudio)
        addChildLongClickViewIds(R.id.rlAudio)
    }

    private var mIvAudio: ImageView? = null
    private var mLastPosition = -1

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_SEND_AUDIO
    override val layoutId: Int
        get() = R.layout.item_audio_send

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        super.convert(helper, item)
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessage>(item).body
        helper.setText(R.id.tvDuration, "${msgBody.duration}\"")
        downloadFile(item)
    }

    override fun onChildClick(
        helper: BaseViewHolder,
        view: View,
        data: EMMessage,
        position: Int
    ) {
        super.onChildClick(helper, view, data, position)
        val message = getAdapter()!!.data[position]
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessage>(message).body
        if (mIvAudio != null) {
            mIvAudio?.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
            MediaManager.reset()
            mIvAudio = null
            if (position != mLastPosition) {
                mLastPosition = position
                mIvAudio = helper.getView(R.id.ivAudio)
                if (FileUtils.isFileExists(msgBody.localUrl)) {
                    playSound(msgBody.localUrl)
                } else {
                    downloadFile(message)
                }
            }
        } else {
            mLastPosition = position
            mIvAudio = helper.getView(R.id.ivAudio)
            if (FileUtils.isFileExists(msgBody.localUrl)) {
                playSound(msgBody.localUrl)
            } else {
                downloadFile(message)
            }
        }
    }

    private fun playSound(localPath: String?) {
        mIvAudio?.let { iv ->
            MediaManager.reset()
            iv.setBackgroundResource(R.drawable.audio_animation_right_list)
            val drawable = iv.background as AnimationDrawable
            drawable.start()
            MediaManager.playSound(
                context,
                localPath
            )
            {
                drawable.stop()
                iv.setBackgroundResource(R.mipmap.audio_animation_list_right_3)
                MediaManager.release()
            }
        }
    }

    private fun downloadFile(message: EMMessage) {
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessage>(message).body
        val file = File(PATH_RECORD, message.msgId)
        if (!file.exists()) {
            DownloadUtil.get().download(
                msgBody.remoteUrl,
                PATH_RECORD,
                message.msgId,
                object : IFangIMProgressFileCallback {
                    override fun onSuccess(file: File) {

                    }

                    override fun onProgress(progress: Int) {
                    }

                    override fun onFailure(errorCode: Int, message: String) {
                    }

                })
        }
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper(), Handler.Callback {
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessageBody>(it.obj as EMMessage)
        playSound(msgBody.localUrl)
        return@Callback true
    })

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