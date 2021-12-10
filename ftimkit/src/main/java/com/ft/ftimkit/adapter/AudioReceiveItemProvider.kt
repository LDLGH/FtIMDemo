package com.ft.ftimkit.adapter

import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ObjectUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ft.ftimkit.R
import com.ft.ftimkit.bean.MessageMultiEntity
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.http.DownloadUtil
import com.ft.ftimkit.interfaces.IFangIMProgressFileCallback
import com.ft.ftimkit.model.FangVoiceIMMessage
import com.ft.ftimkit.model.FangVoiceIMMessageBody
import com.ft.ftimkit.util.*
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import java.io.File


/**
 * @author LDL
 * @date: 2021/6/25
 * @description:
 */
class AudioReceiveItemProvider : BaseMessageProvider() {

    init {
        addChildClickViewIds(R.id.rlAudio)
        addChildLongClickViewIds(R.id.rlAudio)
    }

    private var mIvAudio: ImageView? = null
    private var mLastPosition = -1

    override val itemViewType: Int
        get() = MessageMultiEntity.TYPE_RECEIVE_AUDIO
    override val layoutId: Int
        get() = R.layout.item_audio_receive

    override fun convert(helper: BaseViewHolder, item: EMMessage) {
        super.convert(helper, item)
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessage>(item).body
        helper.setText(R.id.tvDuration, "${msgBody.duration}\"")
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
        mIvAudio = helper.getView(R.id.ivAudio)
        if (mIvAudio != null) {
            mIvAudio?.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
            mIvAudio = null
            if (position != mLastPosition) {
                mLastPosition = position
                mIvAudio = helper.getView(R.id.ivAudio)
                playSound(msgBody.remoteUrl)
            }
        } else {
            mLastPosition = position
            playSound(msgBody.remoteUrl)
        }
    }

    private fun playSound(localPath: String?) {
        val audioUri: Uri = Uri.parse(localPath)
        mIvAudio?.let { iv ->
            MediaManager.reset()
            iv.setBackgroundResource(R.drawable.audio_animation_left_list)
            val drawable = iv.background as AnimationDrawable
            drawable.start()
            AudioPlayManager.getInstance()
                .startPlay(context, audioUri, object : IAudioPlayListener {
                    override fun onStart(var1: Uri?) {

                    }

                    override fun onStop(var1: Uri?) {
                        drawable.stop()
                        iv.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
                    }

                    override fun onComplete(var1: Uri?) {
                        drawable.stop()
                        iv.setBackgroundResource(R.mipmap.audio_animation_list_left_3)
                    }
                })
        }
    }

    private fun downloadFile(message: EMMessage) {
        val msgBody = ChatHelper.getMsgBody<FangVoiceIMMessage>(message).body
        val file = File(IMConstants.PATH_RECORD, message.msgId)
        if (!file.exists()) {
            DownloadUtil.get().download(
                msgBody.remoteUrl,
                IMConstants.PATH_RECORD,
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

    override fun onViewDetachedFromWindow(holder: BaseViewHolder) {
        super.onViewDetachedFromWindow(holder)
        mHandler.removeCallbacksAndMessages(null)
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