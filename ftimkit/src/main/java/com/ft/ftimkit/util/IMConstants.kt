package com.ft.ftimkit.util

import com.blankj.utilcode.util.PathUtils

/**
 * @author LDL
 * @date: 2021/7/26
 * @description:
 */
object IMConstants {

    const val DEFAULT_AVATAR = "http://jiaoxuekuai.huihe2.com/assets/img/default/avatar.png"

    const val MSG_BODY = "msgBody"

    const val TIME_INTERVAL = 300000L

    private const val RECORD_DOWNLOAD_DIR_SUFFIX = "/record/download/"

    val PATH_RECORD = "${PathUtils.getExternalAppCachePath()}$RECORD_DOWNLOAD_DIR_SUFFIX"

    const val SELECT_ALL = "select_all"

    const val USER_ID_SELECT = "user_id_select"

    const val USER_NAME_SELECT = "user_name_select"
}