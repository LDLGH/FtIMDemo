package com.ft.ftimkit

import android.app.Application
import com.ft.ftimkit.interfaces.IFangIMCallBack
import com.ft.ftimkit.interfaces.IFangIMConnectionListener
import com.ft.ftimkit.interfaces.IFangIMSendMessage
import com.ft.ftimkit.interfaces.IFangMessageStatusCallback
import com.ft.ftimkit.model.ChatType
import com.hyphenate.EMConversationListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

/**
 * @author LDL
 * @date: 2021/11/1
 * @description:
 */
object FangIM : IFangIMSendMessage {

    /**
     * 初始化三方SDK
     *
     * @param application application
     * @return
     */
    fun init(application: Application): Boolean {
        return IMCenter.init(application)
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
        return IMCenter.register(username, password)
    }

    /**
     * IM登录
     *
     * @param username 账号
     * @param password 密码
     * @param callBack 通用的IM回调函数接口
     */
    fun login(username: String, password: String, callBack: IFangIMCallBack?) {
        IMCenter.login(username, password, callBack)
    }

    /**
     * 使用token方式登录
     *
     * @param username 账号
     * @param token token
     * @param callBack 通用的IM回调函数接口
     */
    fun loginWithToken(username: String, token: String, callBack: IFangIMCallBack?) {
        IMCenter.loginWithToken(username, token, callBack)
    }


    /**
     * 退出登录
     *
     * @param unbindToken 解绑设备token
     * @discussion 同步方法
     */
    fun logout(unbindToken: Boolean = true) {
        IMCenter.logout(unbindToken)
    }

    /**
     * 异步退出登录
     *
     * @param unbindToken 解绑设备token
     * @param callBack 通用的IM回调函数接口
     * @discussion 如果集成了FCM等第三方推送，方法里第一个参数需要设为true，这样退出的时候会解绑设备token，否则可能会出现退出了，还能收到消息的现象
     */
    fun asyncLogout(unbindToken: Boolean = true, callBack: IFangIMCallBack?) {
        IMCenter.asyncLogout(unbindToken, callBack)
    }

    /**
     * 注册连接状态监听
     *
     * @param listener 接收连接状态监听器
     */
    fun addConnectionListener(listener: IFangIMConnectionListener?) {
        IMCenter.addConnectionListener(listener)
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
        IMCenter.sendMessage(body, toId, chatType, callBack)
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
        IMCenter.sendTxtMessage(content, toId, chatType, callBack)
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
        IMCenter.sendImageMessage(
            fileName,
            localUrl,
            remoteUrl,
            width,
            height,
            toId,
            chatType,
            callBack
        )
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
        IMCenter.sendVoiceMessage(fileName, localUrl, remoteUrl, duration, toId, chatType, callBack)
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
        IMCenter.sendFileMessage(fileName, localUrl, remoteUrl, toId, chatType, callBack)
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
        IMCenter.sendVideoMessage(
            fileName,
            localUrl,
            remoteUrl,
            fileLength,
            thumbnailUrl,
            localThumb,
            thumbnailWidth,
            thumbnailHeight,
            duration,
            toId,
            chatType,
            callBack
        )
    }

    /**
     * 重发消息
     *
     * @param message message
     */
    override fun resendMessage(message: EMMessage, callBack: IFangMessageStatusCallback?) {
        IMCenter.resendMessage(message, callBack)
    }

    /**
     * 删除指定ID的对话和本地的聊天记录
     *
     * @param id 会话ID
     * @param deleteMessages 是否同时删除本地的聊天记录
     */
    fun deleteConversation(id: String, deleteMessages: Boolean = false): Boolean {
        return IMCenter.deleteConversation(id, deleteMessages)
    }

    /**
     * 删除当前会话的某条聊天记录
     *
     * @param conversation 当前会话
     * @param msgId 消息id
     */
    fun removeMessage(conversation: EMConversation, msgId: String) {
        IMCenter.removeMessage(conversation, msgId)
    }

    /**
     * 注册消息监听来接收消息
     *
     * @param listener EMMessageListener
     */
    fun addMessageListener(listener: EMMessageListener) {
        IMCenter.addMessageListener(listener)
    }

    /**
     * 移除消息监听来接收消息
     *
     * @param listener EMMessageListener
     */
    fun removeMessageListener(listener: EMMessageListener) {
        IMCenter.removeMessageListener(listener)
    }

    /**
     * 注册聊天会话变更及收到会话已读的监听器
     *
     * @param listener EMConversationListener
     */
    fun addConversationListener(listener: EMConversationListener) {
        IMCenter.addConversationListener(listener)
    }


    /**
     * 移除聊天会话变更及收到会话已读的监听器
     *
     * @param listener EMConversationListener
     */
    fun removeConversationListener(listener: EMConversationListener) {
        IMCenter.removeConversationListener(listener)
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
        return IMCenter.loadLocalMessages(conversation, startMsgId, pageSize)
    }

    /**
     *指定会话消息未读数清零
     *
     * @param conversation 当前会话
     */
    fun markAllMessagesAsRead(conversation: EMConversation) {
        IMCenter.markAllMessagesAsRead(conversation)
    }

    /**
     * 获取本地所有会话聊天列表
     *
     * @return conversations
     */
    fun loadLocalAllConversations(): List<EMConversation> {
        return IMCenter.loadLocalAllConversations()
    }
}