package com.ft.imdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ft.ftimkit.FangIM
import com.ft.ftimkit.interfaces.IFangIMCallBack

/**
 * @author LDL
 * @date: 2021/11/2
 * @description:
 */
class LoginViewModel : ViewModel() {

    val mLoginLiveData = MutableLiveData<Boolean>()

    fun login(account: String, password: String) {
        FangIM.login(account, password, object : IFangIMCallBack {
            override fun onSuccess() {
                mLoginLiveData.postValue(true)
            }

            override fun onError(code: Int, error: String?) {
                mLoginLiveData.postValue(false)
            }

            override fun onProgress(progress: Int, status: String?) {

            }

        })
    }

}