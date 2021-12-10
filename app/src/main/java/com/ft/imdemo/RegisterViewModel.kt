package com.ft.imdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.ftimkit.FangIM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author LDL
 * @date: 2021/11/2
 * @description:
 */
class RegisterViewModel : ViewModel() {

    val mRegisterLiveData = MutableLiveData<Boolean>()

    fun register(account: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val register = FangIM.register(account, password)
            mRegisterLiveData.postValue(register)
        }
    }
}