package com.ft.imdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ft.ftimkit.FangIM
import com.ft.ftimkit.model.FangConversationInfo
import com.ft.ftimkit.util.ChatHelper
import com.hyphenate.EMConversationListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import java.util.*

class ConversationViewModel : ViewModel(), EMConversationListener, EMMessageListener {

    val mConversationsLiveData = MutableLiveData<List<FangConversationInfo>>()
    val mConversationUpdateLiveData by lazy { MutableLiveData<Boolean>() }

    init {
        FangIM.addConversationListener(this)
        FangIM.addMessageListener(this)
    }

    /**
     * 获取本地所有会话聊天列表
     *
     * @desc 根据会话置顶和时间{timestamp}排序
     */
    fun loadLocalAllConversations() {
        val conversations = FangIM.loadLocalAllConversations()
        val sortList = arrayListOf<FangConversationInfo>()
        val topSortList = arrayListOf<FangConversationInfo>()
        synchronized(ConversationViewModel::class.java) {
            conversations.forEach {
                val info = FangConversationInfo()
                info.info = it
                val extField = it.extField
                val lastMsgTime = it.lastMessage.msgTime
                if (extField.isNotEmpty() && ChatHelper.isTimestamp(extField)) {
                    info.isTop = true
                    val makeTopTime = extField.toLong()
                    if (makeTopTime > lastMsgTime) {
                        info.timestamp = makeTopTime
                    } else {
                        info.timestamp = lastMsgTime
                    }
                    topSortList.add(info)
                } else {
                    info.timestamp = lastMsgTime
                    sortList.add(info)
                }
            }
        }
        sortList.sortByDescending { it.timestamp }
        topSortList.sortByDescending { it.timestamp }
        sortList.addAll(0, topSortList)
        mConversationsLiveData.postValue(sortList)
    }

    /**
     * 删除指定ID的对话和本地的聊天记录
     *
     * @param id 会话ID
     * @param deleteMessages 是否同时删除本地的聊天记录
     */
    fun deleteConversation(id: String, deleteMessages: Boolean = false) {
        FangIM.deleteConversation(id, deleteMessages)
    }

    fun sortData(data: List<FangConversationInfo>): List<FangConversationInfo> {
        if (data.isEmpty()) {
            return emptyList()
        }
        val sortList = arrayListOf<FangConversationInfo>()
        val topSortList = arrayListOf<FangConversationInfo>()
        synchronized(ConversationViewModel::class.java) {
            data.forEach {
                if (it.isTop) {
                    topSortList.add(it)
                } else {
                    sortList.add(it)
                }
            }
            sortByTimestamp(topSortList)
            sortByTimestamp(sortList)
            sortList.addAll(0, sortList)
        }
        return sortList
    }

    fun sortByTimestamp(data: MutableList<FangConversationInfo>) {
        if (data.isEmpty()) {
            return
        }
        data.sortWith { o1, o2 ->
            when {
                o2.timestamp > o1.timestamp -> {
                    return@sortWith 1
                }
                o2.timestamp == o1.timestamp -> {
                    return@sortWith 0
                }
                else -> {
                    return@sortWith -1
                }
            }
        }
    }

    fun makeConversationTop(info: FangConversationInfo) {
        val conversation = info.info
        if (conversation is EMConversation) {
            val timestamp = System.currentTimeMillis()
            conversation.extField = timestamp.toString()
            info.isTop = true
            info.timestamp = timestamp
        }
    }

    fun cancelConversationTop(info: FangConversationInfo) {
        val conversation = info.info
        if (conversation is EMConversation) {
            conversation.extField = ""
            info.isTop = false
            info.timestamp = conversation.lastMessage.msgTime
        }
    }

    override fun onCoversationUpdate() {
        mConversationUpdateLiveData.postValue(true)
    }

    override fun onConversationRead(from: String?, to: String?) {

    }

    override fun onCleared() {
        super.onCleared()
        FangIM.removeConversationListener(this)
        FangIM.removeMessageListener(this)
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        mConversationUpdateLiveData.postValue(true)
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
}