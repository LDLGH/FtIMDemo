package com.ft.ftimkit.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.*
import com.ft.ftimkit.R
import com.ft.ftimkit.adapter.ChatAdapter
import com.ft.ftimkit.databinding.FragmentChatBinding
import com.ft.ftimkit.event.BubbleLongClickEvent
import com.ft.ftimkit.event.ChatAvatarEvent
import com.ft.ftimkit.event.ResendMsgEvent
import com.ft.ftimkit.event.SendMessageCallBackEvent
import com.ft.ftimkit.model.FangIMMessage
import com.ft.ftimkit.model.FangTextIMMessage
import com.ft.ftimkit.model.MsgType
import com.ft.ftimkit.util.ChatHelper
import com.ft.ftimkit.util.ChatUiHelper
import com.ft.ftimkit.util.IMConstants.USER_ID_SELECT
import com.ft.ftimkit.util.IMConstants.USER_NAME_SELECT
import com.ft.ftimkit.util.MediaManager
import com.ft.ftimkit.util.PictureFileUtil
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import java.io.File

/**
 * @author LDL
 * @date: 2021/7/6
 * @description:基础聊天页面
 */
class ChatFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val REQUEST_CODE_IMAGE = 0x00
        const val REQUEST_CODE_VIDEO = 0x01
        const val REQUEST_CODE_FILE = 0x02
        const val REQUEST_CODE_AT = 0x03

        const val TO_CHAT_ID = "to_chat_id"
        const val CHAT_TYPE = "chat_type"

        @JvmStatic
        fun getInstance(toChatId: String, chatType: Int): ChatFragment {
            val fragment = ChatFragment()
            val bundle = Bundle()
            bundle.apply {
                putString(TO_CHAT_ID, toChatId)
                putInt(CHAT_TYPE, chatType)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private val mViewModel by viewModels<ChatViewModel>()
    private lateinit var mBinding: FragmentChatBinding
    private val mAdapter: ChatAdapter by lazy { ChatAdapter() }
    private lateinit var mChatUiHelper: ChatUiHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        mBinding.rvChatList.adapter = mAdapter
        mBinding.swipeChat.setOnRefreshListener(this)
        initChatUi()
        initListener()
        initObserver()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initChatUi() {
        val chatType = arguments?.getInt(CHAT_TYPE)
        mChatUiHelper = ChatUiHelper.with(activity)
        mChatUiHelper.bindContentLayout(mBinding.llContent)
            .bindttToSendButton(mBinding.btnSend)
            .bindEditText(mBinding.etContent)
            .bindBottomLayout(mBinding.bottomLayout)
            .bindEmojiLayout(mBinding.includeEmotion.llEmotion)
            .bindAddLayout(mBinding.includeAdd.llAdd)
            .bindToAddButton(mBinding.ivAdd)
            .bindToEmojiButton(mBinding.ivEmo)
            .bindAudioBtn(mBinding.btnAudio)
            .bindAudioIv(mBinding.ivAudio)
            .bindChatType(chatType!!)
            .bindEmojiData(view)
        //点击空白区域关闭键盘
        mBinding.rvChatList.setOnTouchListener { _, _ ->
            mChatUiHelper.hideBottomLayout(false)
            mChatUiHelper.hideSoftInput()
            mBinding.etContent.clearFocus()
            mBinding.ivEmo.setImageResource(R.mipmap.ic_emoji)
            return@setOnTouchListener false
        }
        mBinding.btnAudio.setOnFinishedRecordListener { audioPath, time ->
            val file = File(audioPath)
            if (file.exists()) {
                mViewModel.sendVoiceMessage(file, time)
            }
        }
    }

    private fun initListener() {
        mBinding.btnSend.setOnClickListener {
            val content = mBinding.etContent.text.toString()
            if (content.isEmpty()) {
                return@setOnClickListener
            }
            mBinding.etContent.text?.clear()
            mViewModel.sendTextMessage(content)
        }
        mBinding.includeAdd.rlPhoto.setOnClickListener {
            PictureFileUtil.openGalleryPic(activity, REQUEST_CODE_IMAGE)
        }
        mBinding.includeAdd.rlVideo.setOnClickListener {
            PictureFileUtil.openGalleryAudio(activity, REQUEST_CODE_VIDEO)
        }
        mBinding.includeAdd.rlFile.setOnClickListener {
            PictureFileUtil.openFile(activity, REQUEST_CODE_FILE)
        }
        mBinding.includeAdd.rlLocation.setOnClickListener {

        }
    }

    private fun initData() {
        arguments?.let {
            val toChatId = it.getString(TO_CHAT_ID, "")
            val chatType = it.getInt(CHAT_TYPE)
            mViewModel.setupConversation(chatId = toChatId, chatType)
            mViewModel.loadLocalMessages()
        }
    }

    private fun initObserver() {
        LiveEventBus.get(ChatAvatarEvent::class.java)
            .observe(this, {

            })
        LiveEventBus.get(ResendMsgEvent::class.java)
            .observe(this, {
                mViewModel.resendMessage(it.message)
            })
        LiveEventBus.get(BubbleLongClickEvent::class.java)
            .observe(this, {
                showBubbleDialog(it.pos)
            })
        LiveEventBus.get(SendMessageCallBackEvent::class.java)
            .observe(this, {
                mAdapter.addData(it.message)
                refreshSelectLast()
            })
        mViewModel.mLoadMessageListLiveData.observe(viewLifecycleOwner, {
            mAdapter.setList(it)
            refreshSelectLast()
        })
        mViewModel.mLoadMoreMessageListLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                mAdapter.addData(0, it)
            }
            mBinding.swipeChat.isRefreshing = false
        })
        mViewModel.mMessageReceivedLiveData.observeForever {
            mAdapter.addData(it)
            if (mBinding.rvChatList.isLastItemVisibleCompleted) {
                refreshSelectLast()
            }
        }
        mViewModel.mMessageCallBackLiveData.observe(viewLifecycleOwner, {
            val data = mAdapter.data
            data.forEachIndexed { index, emMessage ->
                if (emMessage.msgId == it.msgId) {
                    mAdapter.data[index] = emMessage
                    mAdapter.notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        })
    }

    override fun onRefresh() {
        if (ObjectUtils.isNotEmpty(mAdapter.data)) {
            mViewModel.loadLocalMessages(mAdapter.data[0].msgId)
        } else {
            mBinding.swipeChat.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FILE -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (selectList.isNotEmpty()) {
//                        mViewModel.sendFileMessage(selectList.first())
                    }
                }
                REQUEST_CODE_IMAGE -> {
                    // 图片选择结果回调
                    val selectListPic = PictureSelector.obtainMultipleResult(data)
                    mViewModel.sendImageMessage(selectListPic)
                }
                REQUEST_CODE_VIDEO -> {
                    // 视频选择结果回调
                    val selectListVideo = PictureSelector.obtainMultipleResult(data)
                    for (media in selectListVideo) {
                        LogUtils.d("获取视频路径成功:" + media.path)
                    }
                }
            }
        }
    }

    fun updateInputText(data: Intent?) {
        val resultId = data?.getStringExtra(USER_ID_SELECT)
        val resultName = data?.getStringExtra(USER_NAME_SELECT)
        mChatUiHelper.updateInputText(resultName, resultId)
    }

    /**
     * 滑动消息至底部
     *
     */
    private fun refreshSelectLast() {
        mBinding.rvChatList.scrollToPosition(mAdapter.itemCount - 1)
    }

    private fun showBubbleDialog(pos: Int) {
        val message = mAdapter.data[pos]
        val items = mutableListOf<String>()
        when (ChatHelper.getMsgBody<FangIMMessage>(message).msgType) {
            MsgType.TXT.ordinal -> {
                items.add("复制")
                items.add("删除")
            }
            else -> {
                items.add("删除")
            }
        }
        if (ObjectUtils.isEmpty(items)) {
            return
        }
        AlertDialog.Builder(requireActivity())
            .setTitle("操作")
            .setItems(
                items.toTypedArray()
            ) { dialog, which ->
                if (items[which] == "删除") {
                    mViewModel.removeMessage(message.msgId)
                    mAdapter.removeAt(pos)
                }
                if (items[which] == "复制") {
                    val msgBody = ChatHelper.getMsgBody<FangTextIMMessage>(message)
                    ClipboardUtils.copyText(msgBody.body.message)
                    ToastUtils.showShort("已复制")
                }
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onPause() {
        super.onPause()
        MediaManager.release()
    }
}