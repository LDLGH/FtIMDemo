package com.ft.ftimkit.model

/**
 * @author LDL
 * @date: 2021/11/2
 * @description: 各个类型消息体
 */

open class FangIMMessageBody //消息体基类

/**
 * 文本消息体
 *
 * @property message 文本
 * @constructor Create empty Fang text i m message body
 */
data class FangTextIMMessageBody(var message: String) : FangIMMessageBody()

/**
 * 文件类消息的基类
 *
 * @property fileName 文件名
 * @property localUrl 本地文件路径
 * @property remoteUrl 远程服务器文件地址
 * @property fileLength 文件长度
 * @constructor Create empty Fang file i m message body
 */
abstract class FangFileIMMessageBody : FangIMMessageBody() {
    var fileName: String? = null
    var localUrl: String? = null
    var remoteUrl: String? = null
    var fileLength: Long? = null
}

/**
 * 文件类消息体
 *
 * @constructor Create empty Fang file i m message body
 */
class FangNormalFileIMMessageBody : FangFileIMMessageBody()

/**
 *图片消息体
 *
 * @property width 图片的宽度
 * @property height 图片的高度
 * @constructor Create empty Fang image i m message body
 */
data class FangImageIMMessageBody(
    var width: Int,
    var height: Int
) : FangFileIMMessageBody()

/**
 * 语音消息体
 *
 * @property duration 语音事件长度，单位为秒
 * @constructor Create empty Fang voice i m message body
 */
data class FangVoiceIMMessageBody(
    var duration: Int,
) : FangFileIMMessageBody()


/**
 * 视频消息体
 *
 * @property thumbnailUrl 远程视频缩略图
 * @property localThumb 本地视频缩略图
 * @property thumbnailWidth 视频缩略图的宽度
 * @property thumbnailHeight 视频缩略图的高度
 * @property duration 视频时长, 单位为秒
 * @constructor Create empty Fang video i m message body
 */
data class FangVideoIMMessageBody(
    var thumbnailUrl: String,
    var localThumb: String,
    var thumbnailWidth: Int,
    var thumbnailHeight: Int,
    var duration: Int
) : FangFileIMMessageBody()