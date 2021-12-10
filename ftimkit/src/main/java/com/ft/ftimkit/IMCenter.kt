package com.ft.ftimkit

import android.app.Application
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import com.ft.ftimkit.interfaces.IFangIMCallBack
import com.ft.ftimkit.interfaces.IFangIMConnectionListener
import com.ft.ftimkit.interfaces.IFangIMSendMessage
import com.ft.ftimkit.interfaces.IFangMessageStatusCallback
import com.ft.ftimkit.model.ChatType
import com.ft.ftimkit.model.MessageBuilder
import com.ft.ftimkit.util.ChatHelper
import com.ft.ftimkit.util.IMConstants
import com.hyphenate.EMCallBack
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMConversationListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.*
import com.hyphenate.exceptions.HyphenateException
import org.json.JSONException
import org.json.JSONObject

/**
 * @author LDL
 * @date: 2021/11/1
 * @description:
 */
object IMCenter : IFangIMSendMessage {

    /**
     * sdk是否已初始化
     */
    private var isSdkInit: Boolean = false

    /**
     * 初始化三方SDK
     *
     * @param application application
     * @return
     */
    fun init(application: Application): Boolean {
        if (isSdkInit) {
            return true
        }
        //防止环信SDK被初始化2次
        if (!ProcessUtils.isMainProcess()) {
            LogUtils.e("enter the service process!")
            return false
        }
        //初始化环信
        EMClient.getInstance().init(application, initChatOptions())
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true)
        isSdkInit = true
        return true
    }

    /**
     * 初始化环信配置
     *
     * @return options
     */
    private fun initChatOptions(): EMOptions {
        val options = EMOptions()
        options.apply {
            // 默认添加好友时，是不需要验证的，改成需要验证
            acceptInvitationAlways = false
            // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
            autoTransferMessageAttachments = true
            // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
            setAutoDownloadThumbnail(true)
        }
        return options
    }


    /**
     * IM注册
     *
     * @param username 账号
     * @param password 密码
     * @discussion 开放注册是为了测试使用，正式环境中不推荐使用该方式注册环信账号；
     * @discussion 同步方法
     *
     * @return 注册是否成功
     */
    fun register(username: String, password: String): Boolean {
        return try {
            EMClient.getInstance().createAccount(username, password)
            true
        } catch (e: HyphenateException) {
            LogUtils.e("${e.errorCode}${e.description}")
            false
        }
    }

    /**
     * IM登录
     *
     * @param username 账号
     * @param password 密码
     * @param callBack 通用的IM回调函数接口
     */
    fun login(username: String, password: String, callBack: IFangIMCallBack?) {
        EMClient.getInstance().login(username, password, object : EMCallBack {
            override fun onSuccess() {
                //保证进入主页面后本地会话和群组都 load 完毕。
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                callBack?.onSuccess()
            }

            override fun onError(code: Int, error: String?) {
                callBack?.onError(code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                callBack?.onProgress(progress, status)
            }

        })
    }

    /**
     * 使用token方式登录
     *
     * @param username 账号
     * @param token token
     * @param callBack 通用的IM回调函数接口
     */
    fun loginWithToken(username: String, token: String, callBack: IFangIMCallBack?) {
        EMClient.getInstance().loginWithToken(username, token, object : EMCallBack {
            override fun onSuccess() {
                //保证进入主页面后本地会话和群组都 load 完毕。
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                callBack?.onSuccess()
            }

            override fun onError(code: Int, error: String?) {
                callBack?.onError(code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                callBack?.onProgress(progress, status)
            }
        })
    }

    /**
     * 退出登录
     *
     * @param unbindToken 解绑设备token
     * @discussion 同步方法
     */
    fun logout(unbindToken: Boolean = true) {
        EMClient.getInstance().logout(unbindToken)
    }

    /**
     * 异步退出登录
     *
     * @param unbindToken 解绑设备token
     * @param callBack 通用的IM回调函数接口
     * @discussion 如果集成了FCM等第三方推送，方法里第一个参数需要设为true，这样退出的时候会解绑设备token，否则可能会出现退出了，还能收到消息的现象
     */
    fun asyncLogout(unbindToken: Boolean = true, callBack: IFangIMCallBack?) {
        EMClient.getInstance().logout(unbindToken, object : EMCallBack {
            override fun onSuccess() {
                callBack?.onSuccess()
            }

            override fun onError(code: Int, error: String?) {
                callBack?.onError(code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                callBack?.onProgress(progress, status)
            }

        })
    }

    /**
     * 注册连接状态监听
     *
     * @param listener 接收连接状态监听器
     */
    fun addConnectionListener(listener: IFangIMConnectionListener?) {
        EMClient.getInstance().addConnectionListener(object : EMConnectionListener {

            override fun onConnected() {
                listener?.onConnected()
            }

            override fun onDisconnected(errorCode: Int) {
                listener?.onDisconnected(errorCode)
            }

        })
    }

    /**
     * 发送自定义消息
     *
     * @param body 自定义消息体
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendMessage(
        body: String,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
        val customBody = EMCustomMessageBody("message")
        val params = mutableMapOf<String, String>()
        params[IMConstants.MSG_BODY] = body
        customBody.params = params
        customMessage.apply {
            addBody(customBody)
            to = toId
            this.chatType =
                if (chatType.ordinal == ChatType.Chat.ordinal) EMMessage.ChatType.Chat else EMMessage.ChatType.GroupChat
        }
        // 设置自定义推送提示
        val extObject = JSONObject()
        try {
            extObject.put("em_push_name", toId)
            extObject.put("em_push_content", ChatHelper.getMessageDigest(customMessage))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // 将推送扩展设置到消息中
        customMessage.setAttribute("em_apns_ext", extObject)
        customMessage.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                callBack?.onMessageSuccess(customMessage)
            }

            override fun onError(code: Int, error: String?) {
                callBack?.onMessageError(customMessage, code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                callBack?.onMessageProgress(customMessage, progress, status)
            }
        })
        EMClient.getInstance().chatManager().sendMessage(customMessage)
        callBack?.onSendMessageFinish(customMessage)
    }

    /**
     * 发送自定义文本消息
     *
     * @param content 文本
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendTxtMessage(
        content: String,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val body = MessageBuilder.createTextMessage(content)
        sendMessage(GsonUtils.toJson(body), toId, chatType, callBack)
    }

    /**
     * 发送自定义图片消息
     *
     * @param fileName 文件名
     * @param localUrl 本地文件路径
     * @param remoteUrl 远程服务器文件地址
     * @param width 图片的宽度
     * @param height 图片的高度
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendImageMessage(
        fileName: String,
        localUrl: String,
        remoteUrl: String,
        width: Int,
        height: Int,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val body =
            MessageBuilder.createImageMessage(fileName, localUrl, remoteUrl, width, height)
        sendMessage(GsonUtils.toJson(body), toId, chatType, callBack)
    }

    /**
     * 发送自定义语音消息
     *
     * @param fileName 文件名
     * @param localUrl 本地文件路径
     * @param remoteUrl 远程服务器文件地址
     * @param duration 语音事件长度，单位为秒
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendVoiceMessage(
        fileName: String,
        localUrl: String,
        remoteUrl: String,
        duration: Int,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val body =
            MessageBuilder.createVoiceMessage(fileName, localUrl, remoteUrl, duration)
        sendMessage(GsonUtils.toJson(body), toId, chatType, callBack)
    }

    /**
     * 发送自定义文件消息
     *
     * @param fileName 文件名
     * @param localUrl 本地文件路径
     * @param remoteUrl 远程服务器文件地址
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendFileMessage(
        fileName: String,
        localUrl: String,
        remoteUrl: String,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val body = MessageBuilder.createFileMessage(fileName, localUrl, remoteUrl)
        sendMessage(GsonUtils.toJson(body), toId, chatType, callBack)
    }

    /**
     * 发送自定义视频消息
     *
     * @param fileName 文件名
     * @param localUrl 本地文件路径
     * @param remoteUrl 远程服务器文件地址
     * @param fileLength 文件大小
     * @param thumbnailUrl 远程视频缩略图
     * @param localThumb 本地视频缩略图
     * @param thumbnailWidth 视频缩略图的宽度
     * @param thumbnailHeight 视频缩略图的高度
     * @param duration 视频时长, 单位为秒
     * @param toId to指另一方环信id（或者群组id，聊天室id）
     * @param chatType 如果是群聊，设置chatType，默认是单聊
     * @param callBack 发送消息的回调
     */
    override fun sendVideoMessage(
        fileName: String,
        localUrl: String,
        remoteUrl: String,
        fileLength: Long,
        thumbnailUrl: String,
        localThumb: String,
        thumbnailWidth: Int,
        thumbnailHeight: Int,
        duration: Int,
        toId: String,
        chatType: ChatType,
        callBack: IFangMessageStatusCallback?
    ) {
        val body = MessageBuilder.createVideoMessage(
            fileName,
            localUrl,
            remoteUrl,
            fileLength,
            thumbnailUrl,
            localThumb,
            thumbnailWidth,
            thumbnailHeight,
            duration
        )
        sendMessage(GsonUtils.toJson(body), toId, chatType, callBack)
    }

    /**
     * 重发消息
     *
     * @param message message
     */
    override fun resendMessage(message: EMMessage, callBack: IFangMessageStatusCallback?) {
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                callBack?.onMessageSuccess(message)
            }

            override fun onError(code: Int, error: String?) {
                callBack?.onMessageError(message, code, error)
            }

            override fun onProgress(progress: Int, status: String?) {
                callBack?.onMessageProgress(message, progress, status)
            }
        })
        EMClient.getInstance().chatManager().sendMessage(message)
        callBack?.onSendMessageFinish(message)
    }

    /**
     * 删除指定ID的对话和本地的聊天记录
     *
     * @param id 会话ID
     * @param deleteMessages 是否同时删除本地的聊天记录
     */
    fun deleteConversation(id: String, deleteMessages: Boolean = true): Boolean {
        return EMClient.getInstance().chatManager().deleteConversation(id, deleteMessages)
    }

    /**
     * 删除当前会话的某条聊天记录
     *
     * @param conversation 当前会话
     * @param msgId 消息id
     */
    fun removeMessage(conversation: EMConversation, msgId: String) {
        conversation.removeMessage(msgId)
    }

    /**
     * 注册消息监听来接收消息
     *
     * @param listener EMMessageListener
     */
    fun addMessageListener(listener: EMMessageListener) {
        EMClient.getInstance().chatManager().addMessageListener(listener)
    }

    /**
     * 移除消息监听来接收消息
     *
     * @param listener EMMessageListener
     */
    fun removeMessageListener(listener: EMMessageListener) {
        EMClient.getInstance().chatManager().removeMessageListener(listener)
    }

    /**
     * 注册聊天会话变更及收到会话已读的监听器
     *
     * @param listener EMConversationListener
     */
    fun addConversationListener(listener: EMConversationListener) {
        EMClient.getInstance().chatManager().addConversationListener(listener)
    }


    /**
     * 移除聊天会话变更及收到会话已读的监听器
     *
     * @param listener EMConversationListener
     */
    fun removeConversationListener(listener: EMConversationListener) {
        EMClient.getInstance().chatManager().removeConversationListener(listener)
    }

    /**
     * 获取本地会话聊天记录
     *
     * @param conversation 当前会话
     * @param startMsgId 加载这个id之前的message，如果传入""或者null，将从最近的消息开始加载
     * @param pageSize 加载多少条
     */
    fun loadLocalMessages(
        conversation: EMConversation,
        startMsgId: String = "",
        pageSize: Int = 20
    ): List<EMMessage> {
        return conversation.loadMoreMsgFromDB(startMsgId, pageSize)
    }

    /**
     *指定会话消息未读数清零
     *
     * @param conversation 当前会话
     */
    fun markAllMessagesAsRead(conversation: EMConversation) {
        conversation.markAllMessagesAsRead()
    }

    /**
     * 获取本地所有会话聊天列表
     *
     * @return conversations
     */
    fun loadLocalAllConversations(): List<EMConversation> {
        val allConversations = EMClient.getInstance().chatManager().allConversations
        val conversations = arrayListOf<EMConversation>()
        allConversations.forEach {
            conversations.add(it.value)
        }
        return conversations
    }

}