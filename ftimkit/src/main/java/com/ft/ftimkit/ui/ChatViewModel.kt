package com.ft.ftimkit.ui


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils
import com.ft.ftimkit.FangIM
import com.ft.ftimkit.event.SendMessageCallBackEvent
import com.ft.ftimkit.interfaces.IFangMessageStatusCallback
import com.ft.ftimkit.model.ChatType
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.entity.LocalMedia
import java.io.File


/**
 * @author LDL
 * @date: 2021/6/29
 * @description:
 */
class ChatViewModel : ViewModel(), EMMessageListener, IFangMessageStatusCallback {

    val mLoadMessageListLiveData by lazy { MutableLiveData<List<EMMessage>>() }
    val mLoadMoreMessageListLiveData by lazy { MutableLiveData<List<EMMessage>>() }
    val mMessageReceivedLiveData by lazy { MutableLiveData<List<EMMessage>>() }
    val mMessageCallBackLiveData by lazy { MutableLiveData<EMMessage>() }

    private lateinit var mChatId: String
    private var mChatType: ChatType = ChatType.Chat
    private var mConversation: EMConversation? = null

    init {
        FangIM.addMessageListener(this)
    }

    /**
     * 设置当前会话
     *
     * @param chatId 会话ID
     */
    fun setupConversation(chatId: String, chatType: Int) {
        mChatId = chatId
        mChatType = if (chatType == ChatType.Chat.ordinal) ChatType.Chat else ChatType.GroupChat
        mConversation = EMClient.getInstance().chatManager().getConversation(chatId)
        markAllMessagesAsRead()
    }

    /**
     * 加载本地消息
     *
     * @param startMsgId 加载这个id之前的message，如果传入""或者null，将从最近的消息开始加载
     */
    fun loadLocalMessages(startMsgId: String = "") {
        if (mConversation == null) {
            mConversation = EMClient.getInstance().chatManager().getConversation(mChatId)
        }
        mConversation?.let {
            val messages = FangIM.loadLocalMessages(it, startMsgId)
            if (startMsgId.isEmpty()) {
                mLoadMessageListLiveData.postValue(messages)
            } else {
                mLoadMoreMessageListLiveData.postValue(messages)
            }
        }
    }

    /**
     * 发送文本消息
     *
     * @param content 内容
     */
    fun sendTextMessage(content: String) {
        FangIM.sendTxtMessage(content, mChatId, mChatType, this)
    }

    /**
     * 发送图片消息
     *
     * @param localMedias 图片列表
     */
    fun sendImageMessage(localMedias: List<LocalMedia>) {
        val path = "http://img.baidu.com/img/logo-zhidao.gif"
        localMedias.forEach {
            FangIM.sendImageMessage(
                it.fileName,
                it.path,
                path,
                it.width,
                it.height,
                mChatId,
                mChatType,
                this
            )
        }
    }

    /**
     * 发送音频消息
     *
     * @param file 音频文件
     * @param time 秒数
     */
    fun sendVoiceMessage(file: File, time: Int) {
        val path = "http://downsc.chinaz.net/files/download/sound1/201206/1638.mp3"
        FangIM.sendVoiceMessage(file.name, file.absolutePath, path, time, mChatId, mChatType, this)
    }


    /**
     * 发送文件消息
     *
     * @param localMedia 本地文件信息
     */
    fun sendFileMessage(localMedia: LocalMedia) {
        FangIM.sendFileMessage(
            localMedia.fileName,
            localMedia.path,
            localMedia.path,
            mChatId,
            mChatType,
            this
        )
    }

    /**
     * 重发消息
     *
     * @param message message
     */
    fun resendMessage(message: EMMessage) {
        FangIM.resendMessage(message, this)
    }

    /**
     * 删除当前会话的某条聊天记录
     *
     * @param msgId 消息id
     */
    fun removeMessage(msgId: String) {
        mConversation?.let {
            FangIM.removeMessage(it, msgId)
        }
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        val list = mutableListOf<EMMessage>()
        messages?.forEach {
            //单聊
            val chatId = if (it.chatType == EMMessage.ChatType.Chat) {
                it.from
            } else {
                //群聊
                it.to
            }
            if (chatId == mChatId) {
                list.add(it)
            }
        }
        if (list.isNotEmpty()) {
            mMessageReceivedLiveData.postValue(list)
            markAllMessagesAsRead()
        }
    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {

    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {

    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
    }


    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {

    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {

    }

    override fun onMessageSuccess(message: EMMessage) {
        mMessageCallBackLiveData.postValue(message)
    }

    override fun onMessageError(message: EMMessage, code: Int, error: String?) {
        LogUtils.e(error)
        mMessageCallBackLiveData.postValue(message)
    }

    override fun onMessageProgress(message: EMMessage, progress: Int, status: String?) {

    }

    override fun onSendMessageFinish(message: EMMessage) {
        LiveEventBus.get(SendMessageCallBackEvent::class.java)
            .post(SendMessageCallBackEvent(message))
    }

    override fun onCleared() {
        super.onCleared()
        FangIM.removeMessageListener(this)
        markAllMessagesAsRead()
    }

    fun markAllMessagesAsRead() {
        mConversation?.let {
            FangIM.markAllMessagesAsRead(it)
        }
    }
}